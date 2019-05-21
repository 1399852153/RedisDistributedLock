package com.xiongyx.aspect;

import com.xiongyx.annotation.RedisLock;
import com.xiongyx.annotation.RedisLockKey;
import com.xiongyx.lock.api.DistributeLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

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
        boolean lockSuccess = lock(annotation,joinPoint);
        // 如果加锁成功
        if(lockSuccess){
            // 执行方法
            Object result = joinPoint.proceed();
            // 方法执行后，进行解锁
            unlock(annotation,joinPoint);
            return result;
        }else{
            throw new RuntimeException("redis分布式锁加锁失败，method=" + method.getName());
        }
    }

    /**
     * 加锁
     * */
    private boolean lock(RedisLock annotation,ProceedingJoinPoint joinPoint) {
        int retryCount = annotation.retryCount();

        String requestID = REQUEST_ID_MAP.get();
        // 获取参数 拼接redisLock的key
        String redisLockKey = annotation.lockKey() + appendRedisLockKey(joinPoint);
        if(requestID != null){
            // 当前线程 已经存在requestID
            distributeLock.lockAndRetry(redisLockKey,requestID,annotation.expireTime(),retryCount);
            LOGGER.info("重入加锁成功 redisLockKey=" + redisLockKey);

            return true;
        }else{
            // 当前线程 不存在requestID
            String newRequestID = distributeLock.lockAndRetry(redisLockKey,annotation.expireTime(),retryCount);

            if(newRequestID != null){
                // 加锁成功，设置新的requestID
                REQUEST_ID_MAP.set(newRequestID);
                LOGGER.info("加锁成功 redisLockKey=" + redisLockKey);

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
    private void unlock(RedisLock annotation,ProceedingJoinPoint joinPoint) {
        String requestID = REQUEST_ID_MAP.get();
        // 获取参数 拼接redisLock的key
        String redisLockKey = annotation.lockKey() + appendRedisLockKey(joinPoint);
        if(requestID != null){
            // 解锁成功
            boolean unLockSuccess = distributeLock.unLock(redisLockKey,requestID);
            if(unLockSuccess){
                // 移除 ThreadLocal中的数据
                REQUEST_ID_MAP.remove();
                LOGGER.info("解锁成功 redisLockKey=" + redisLockKey);
            }
        }
    }


    /**
     * 查询条件中是否包含空列表
     * */
    private String appendRedisLockKey(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();

        // 方法参数
        Object[] args = proceedingJoinPoint.getArgs();

        for(int i=0; i<parameterAnnotations.length; i++){
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            Object arg = args[i];

            for(Annotation itemAnnotation : parameterAnnotation){
                if (itemAnnotation instanceof RedisLockKey) {
                    RedisLockKey redisKeyLockAnnotation = (RedisLockKey)itemAnnotation;
                    switch (redisKeyLockAnnotation.type()) {
                        case ALL:
                            return getRedisKeyByAll(arg);
                        case FIELD:
                            return getRedisKeyByField(redisKeyLockAnnotation.expression(),arg);
                        default:
                            throw new RuntimeException("error RedisLockKey.type");
                    }
                }
            }
        }

        return "";
    }

    /**
     * ALL类型，当前参数直接通过toString返回
     * */
    private String getRedisKeyByAll(Object arg){
        return ":" + arg.toString();
    }

    private String getRedisKeyByField(String expression,Object arg) {
        String setMethodName = makeGetMethodName(expression);

        try{
            if(arg instanceof Map){
                // map中获取key值对应的value
                String currentArgKey = ((Map)arg).get(expression).toString();
                if(currentArgKey == null){
                    return "";
                }else{
                    return ":" + currentArgKey;
                }
            }else{
                //:::获得bean中key的method对象
                Method beanGetMethod = arg.getClass().getMethod(setMethodName);
                //:::调用获得当前的key
                String currentArgKey = (String) beanGetMethod.invoke(arg);
                return ":" + currentArgKey;
            }
        }catch (Exception e){
            throw new RuntimeException("getRedisKeyByField error",e);
        }
    }

    /***
     * 将通过keyName获得对应的bean对象的get方法名称的字符串
     * @param keyName 属性名
     * @return 返回get方法名称的字符串
     */
    private static String makeGetMethodName(String keyName) {
        //:::将第一个字母转为大写
        String newKeyName = transFirstCharUpperCase(keyName);

        return "get" + newKeyName;
    }

    /**
     * 将字符串的第一个字母转为大写
     * @param str 需要被转变的字符串
     * @return 返回转变之后的字符串
     */
    private static String transFirstCharUpperCase(String str) {
        return str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase());
    }
}
