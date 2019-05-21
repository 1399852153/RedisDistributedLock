package com.xiongyx.annotation;

import com.xiongyx.enums.RedisLockKeyType;

import java.lang.annotation.*;

/**
 * @author xiongyx
 * @date 2019/5/20
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLockKey {
    RedisLockKeyType type();

    String field() default "";
}
