package com.xiongyx.exception;

/**
 * @author xiongyx
 * @date 2019/5/27
 *
 * redis分布式锁 加锁失败异常
 */
public class RedisLockFailException extends RuntimeException {

    public RedisLockFailException(String message) {
        super(message);
    }
}
