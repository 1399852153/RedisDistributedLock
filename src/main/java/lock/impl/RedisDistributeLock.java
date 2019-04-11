package lock.impl;

import lock.api.DistributeLock;
import lock.script.LuaScript;
import redis.RedisClient;

import java.util.*;

/**
 * redis 分布式锁的简单实现
 * @author xiongyx
 */
public final class RedisDistributeLock implements DistributeLock {

    private RedisDistributeLock() {
        LuaScript.init();
    }

    private static DistributeLock instance = new RedisDistributeLock();

    /**
     * 持有锁 成功标识
     * */
    private static final Long ADD_LOCK_SUCCESS = 1L;
    /**
     * 释放锁 失败标识
     * */
    private static final Integer RELEASE_LOCK_SUCCESS = 1;

    /**
     * 默认过期时间 单位：秒
     * */
    private static final int DEFAULT_EXPIRE_TIME_SECOND = 300;
    /**
     * 默认加锁重试时间 单位：毫秒
     * */
    private static final int DEFAULT_RETRY_FIXED_TIME = 3000;
    /**
     * 默认的加锁浮动时间区间 单位：毫秒
     * */
    private static final int DEFAULT_RETRY_TIME_RANGE = 1000;

    /**
     * lockCount Key前缀
     * */
    private static final String LOCK_COUNT_KEY_PREFIX = "lock_count:";

    //===========================================api=======================================

    public static DistributeLock getInstance(){
        return instance;
    }

    @Override
    public String lock(String lockKey) {
        String uuid = UUID.randomUUID().toString();

        return lock(lockKey,uuid);
    }

    @Override
    public String lock(String lockKey, String requestID) {
        RedisClient redisClient = RedisClient.getInstance();

        List<String> keyList = Arrays.asList(
            lockKey,
            LOCK_COUNT_KEY_PREFIX + lockKey
        );

        List<String> argsList = Arrays.asList(
            requestID,
            DEFAULT_EXPIRE_TIME_SECOND + ""
        );
        Long result = (Long)redisClient.eval(LuaScript.LOCK_SCRIPT, keyList, argsList);

        if(result.equals(ADD_LOCK_SUCCESS)){
            return requestID;
        }else{
            return null;
        }
    }

    @Override
    public boolean unLock(String lockKey, String requestID) {
        List<String> keyList = Arrays.asList(
            lockKey,
            LOCK_COUNT_KEY_PREFIX + lockKey
        );

        List<String> argsList = Collections.singletonList(requestID);

        Object result = RedisClient.getInstance().eval(LuaScript.UN_LOCK_SCRIPT, keyList, argsList);

        // 释放锁没有失败 = 释放锁成功
        return RELEASE_LOCK_SUCCESS.equals(result);
    }

    @Override
    public String lockAndRetry(String lockKey) {
        while(true){
            String result = lock(lockKey);
            if(result != null){
                System.out.println("加锁成功 currentName=" + Thread.currentThread().getName());
                return result;
            }else{
                // 重试时间 单位：毫秒
                int retryTime = getFinallyGetLockRetryTime();
                System.out.println("加锁失败 currentName=" + Thread.currentThread().getName() + " 重试间隔时间=" + retryTime + "ms");
                try {
                    Thread.sleep(retryTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException("redis锁重试时，出现异常",e);
                }
            }
        }
    }

    //==============================================私有方法========================================

    /**
     * 获得最终的获得锁的重试时间
     * */
    private int getFinallyGetLockRetryTime(){
        Random ra = new Random();

        // 最终重试时间 = 固定时间 + 浮动时间
        return DEFAULT_RETRY_FIXED_TIME + ra.nextInt(DEFAULT_RETRY_TIME_RANGE);
    }
}
