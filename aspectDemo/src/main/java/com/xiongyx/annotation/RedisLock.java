package com.xiongyx.annotation;

import lock.impl.RedisDistributeLock;

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
     * 无限重试
     * */
    int unLimitRetryCount = RedisDistributeLock.UN_LIMIT_RETRY_COUNT;

    String lockKey();
    int expireTime();
    int retryCount();
}
