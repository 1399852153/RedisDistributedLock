package lock.impl;

import lock.api.DistributeLock;
import redis.RedisClient;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;

/**
 * @Author xiongyx
 * @Date 2019/4/8
 */
public final class RedisDistributeLock implements DistributeLock {

    private static DistributeLock instance = new RedisDistributeLock();

    private static final String NX = "NX";
    private static final String EX = "EX";

    private RedisDistributeLock() {
    }

    /**
     * 持有锁 成功标识
     * */
    private static final String ADD_LOCK_SUCCESS = "OK";
    /**
     * 释放锁 成功标识
     * */
    private static final Integer RELEASE_LOCK_FAIL = 0;

    /**
     * 默认过期时间 单位：秒
     * */
    private static final int DEFAULT_EXPIRE_TIME_SECOND = 30;
    /**
     * 默认加锁重试时间 单位：毫秒
     * */
    private static final int DEFAULT_RETRY_FIXED_TIME = 3000;
    /**
     * 默认的加锁浮动时间区间 单位：毫秒
     * */
    private static final int DEFAULT_RETRY_TIME_RANGE = 1000;

    //===========================================api=======================================

    public static DistributeLock getInstance(){
        return instance;
    }

    @Override
    public String lock(String lockKey) {
        String uuid = UUID.randomUUID().toString();
        String result = RedisClient.getInstance().set(lockKey, uuid, NX, EX, DEFAULT_EXPIRE_TIME_SECOND);

        // 如果加锁成功
        if(ADD_LOCK_SUCCESS.equalsIgnoreCase(result)){
            return uuid;
        }else{
            return null;
        }
    }

    @Override
    public boolean unlock(String lockKey, String requestID) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] "
            + "then return redis.call('del', KEYS[1]) "
            + "else return 0 end";

        Object result = RedisClient.getInstance().eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestID));

        // 释放锁没有失败 = 释放锁成功
        if(!RELEASE_LOCK_FAIL.equals(result)) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public String lockAndRetry(String lockKey) {
        while(true){
            String result = tryGetLock(lockKey);
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
     * 尝试获得锁
     * */
    private String tryGetLock(String lockKey){
        String uuid = UUID.randomUUID().toString();
        String result = RedisClient.getInstance().set(lockKey, uuid, NX, EX, DEFAULT_EXPIRE_TIME_SECOND);

        // 如果加锁成功
        if(ADD_LOCK_SUCCESS.equalsIgnoreCase(result)){
            return uuid;
        }else{
            return null;
        }
    }

    /**
     * 获得最终的获得锁的重试时间
     * */
    private int getFinallyGetLockRetryTime(){
        Random ra = new Random();

        // 最终重试时间 = 固定时间 + 浮动时间
        return DEFAULT_RETRY_FIXED_TIME + ra.nextInt(DEFAULT_RETRY_TIME_RANGE);
    }
}
