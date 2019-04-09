package lock.impl;

import lock.api.DistributeLock;
import lock.constants.LuaScript;
import lock.model.LockContent;
import redis.RedisClient;
import util.JsonUtil;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;

/**
 * redis 分布式锁的简单实现
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
     * 释放锁 失败标识
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

        LockContent lockContent = new LockContent(uuid,1);
        String lockContentJsonStr = JsonUtil.objToJsonStr(lockContent);

        RedisClient redisClient = RedisClient.getInstance();
        String result = redisClient.set(lockKey, lockContentJsonStr, NX, EX, DEFAULT_EXPIRE_TIME_SECOND);

        // 如果加锁成功
        if(ADD_LOCK_SUCCESS.equalsIgnoreCase(result)){
            return uuid;
        }else{
            return null;
        }
    }

    @Override
    public String lock(String lockKey, String requestID) {
        RedisClient redisClient = RedisClient.getInstance();

        String currentLockContentJsonStr = redisClient.get(lockKey);
        if(currentLockContentJsonStr == null){
            // 之前不存在锁，直接加锁
            return lock(lockKey);
        }

        LockContent currentLockContent = JsonUtil.jsonStrToObj(currentLockContentJsonStr,LockContent.class);
        // 如果锁存在，而且requestID 相等
        if(currentLockContent.getRequestID().equals(requestID)){
            // requestID 相等 lockCount自增
            currentLockContent.lockCountInc();

            String newLockContentJsonStr = JsonUtil.objToJsonStr(currentLockContent);
            // 存入新的content内容，并且重置超时时间 todo 实现可重入锁加锁脚本
            String result = (String)redisClient.eval(LuaScript.RLOCK_SCRIPT, Collections.singletonList(lockKey), Collections.singletonList(null));

            // todo 判断是否可重入锁加锁成功
            return requestID;
        }else{
            // requestID 不相等，加锁失败
            return null;
        }
    }

    @Override
    public boolean unLock(String lockKey, String requestID) {
        Object result = RedisClient.getInstance().eval(LuaScript.UN_LOCK_SCRIPT, Collections.singletonList(lockKey), Collections.singletonList(requestID));

        // 释放锁没有失败 = 释放锁成功
        return !RELEASE_LOCK_FAIL.equals(result);
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
