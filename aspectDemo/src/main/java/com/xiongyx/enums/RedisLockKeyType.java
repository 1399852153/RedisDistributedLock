package com.xiongyx.enums;

/**
 * @author xiongyx
 * @date 2019/5/20
 */
public enum RedisLockKeyType {
    /**
     * 当前对象的toString做key
     * */
    ALL,

    /**
     * 当前对象的内部属性的toString做key
     * */
    FIELD,
    ;
}
