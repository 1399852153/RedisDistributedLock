package com.xiongyx.annotation;

import java.lang.annotation.*;

/**
 * @Author xiongyx
 * @Date 2019/4/12
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {
    String lockKey();
    int expireTime();
    int retryCount();
}
