package com.xiongyx.aspect;

import com.xiongyx.annotation.RedisLock;
import lock.api.DistributeLock;
import lock.impl.RedisDistributeLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Author xiongyx
 * @Date 2019/4/12
 *
 * redis锁 切面定义
 */

@Component
@Aspect
public class RedisLockAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLockAspect.class);

    private static final ThreadLocal<String> REQUEST_ID_MAP = new ThreadLocal<>();

    @Autowired
    private DistributeLock distributeLock;

    @Pointcut("@annotation(com.xiongyx.annotation.RedisLock)")
    public void annotationPointcut() {
    }

    @Around("annotationPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RedisLock annotation = method.getAnnotation(RedisLock.class);

        // 方法执行前，先尝试加锁
        boolean lockSuccess = lock(annotation);
        // 如果加锁成功
        if(lockSuccess){
            // 执行方法
            Object result = joinPoint.proceed();
            // 方法执行后，进行解锁
            unlock(annotation);
            return result;
        }

        return null;
    }

    /**
     * 加锁
     * */
    private boolean lock(RedisLock annotation){
        int retryCount = annotation.retryCount();

        String requestID = REQUEST_ID_MAP.get();
        if(requestID != null){
            // 当前线程 已经存在requestID
            distributeLock.lockAndRetry(annotation.lockKey(),requestID,annotation.expireTime(),retryCount);
            LOGGER.info("重入加锁成功 requestID=" + requestID);

            return true;
        }else{
            // 当前线程 不存在requestID
            String newRequestID = distributeLock.lockAndRetry(annotation.lockKey(),annotation.expireTime(),retryCount);

            if(newRequestID != null){
                // 加锁成功，设置新的requestID
                REQUEST_ID_MAP.set(newRequestID);
                LOGGER.info("加锁成功 newRequestID=" + newRequestID);

                return true;
            }else{
                LOGGER.info("加锁失败，超过重试次数，直接返回 retryCount={}",retryCount);

                return false;
            }
        }
    }

    /**
     * 解锁
     * */
    private void unlock(RedisLock annotation){
        String requestID = REQUEST_ID_MAP.get();
        if(requestID != null){
            // 解锁成功
            boolean unLockSuccess = distributeLock.unLock(annotation.lockKey(),requestID);
            if(unLockSuccess){
                // 移除 ThreadLocal中的数据
                REQUEST_ID_MAP.remove();
                LOGGER.info("解锁成功 requestID=" + requestID);
            }
        }
    }
}
