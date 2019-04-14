package com.xiongyx.aspect;

import com.xiongyx.annotation.RedisLock;
import lock.api.DistributeLock;
import lock.impl.RedisDistributeLock;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.RedisClient;

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

    @Pointcut("@annotation(com.xiongyx.annotation.RedisLock)")
    public void annotationPointcut() {
    }

    @Before("annotationPointcut()")
    public void before(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RedisLock annotation = method.getAnnotation(RedisLock.class);
        
        DistributeLock distributeLock = RedisDistributeLock.getInstance();

        String requestID = REQUEST_ID_MAP.get();
        if(requestID != null){
            // 当前线程 已经存在requestID
            distributeLock.lockAndRetry(annotation.lockKey(),requestID,annotation.expireTime());
            LOGGER.info("重入加锁成功 requestID=" + requestID);
        }else{
            // 当前线程 不存在requestID
            String newRequestID = distributeLock.lockAndRetry(annotation.lockKey(),annotation.expireTime());
            // 加锁成功，设置新的requestID
            REQUEST_ID_MAP.set(newRequestID);
            LOGGER.info("加锁成功 newRequestID=" + newRequestID);
        }
    }

    @After("annotationPointcut()")
    public void after(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RedisLock annotation = method.getAnnotation(RedisLock.class);

        DistributeLock distributeLock = RedisDistributeLock.getInstance();
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
