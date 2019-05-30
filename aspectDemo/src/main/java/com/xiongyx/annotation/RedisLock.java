package com.xiongyx.annotation;

import com.xiongyx.exception.RedisLockFailException;
import com.xiongyx.lock.impl.RedisDistributeLock;

import java.lang.annotation.*;

/**
 * @Author xiongyx
 * @Date 2019/4/12
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {
    /**
     * redis锁，重试次数-1代表无限重试
     * */
    int unLimitRetryCount = RedisDistributeLock.UN_LIMIT_RETRY_COUNT;

    /**
     * redis锁对应的key 会拼接此参数，用于进一步区分，避免redis的key被覆盖
     * */
    String lockKey() default "";

    /**
     * redis锁过期时间（单位：秒）
     * */
    int expireTime() default 10;

    /**
     * redis锁，加锁失败重试次数 默认30次，大约3s
     * 超过指定次数后，抛出加锁失败异常，可以由调用方自己补偿
     * @see RedisLockFailException
     * */
    int retryCount() default 30;
}
