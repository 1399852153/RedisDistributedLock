package com.xiongyx.lock.impl;

import com.xiongyx.lock.api.DistributeLock;
import com.xiongyx.lock.script.LuaScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.xiongyx.redis.RedisClient;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * redis 分布式锁的简单实现
 * @author xiongyx
 */
@Component("distributeLock")
public final class RedisDistributeLock implements DistributeLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisDistributeLock.class);

    /**
     * 无限重试
     * */
    public static final int UN_LIMIT_RETRY_COUNT = -1;

    private RedisDistributeLock() {
        try {
            LuaScript.initLockScript();
            LuaScript.initUnLockScript();
        } catch (IOException e) {
            throw new RuntimeException("LuaScript init error!",e);
        }
    }

    /**
     * 持有锁 成功标识
     * */
    private static final Long ADD_LOCK_SUCCESS = 1L;
    /**
     * 释放锁 失败标识
     * */
    private static final Long RELEASE_LOCK_SUCCESS = 1L;

    /**
     * 默认过期时间 单位：秒
     * */
    private static final int DEFAULT_EXPIRE_TIME_SECOND = 300;
    /**
     * 默认加锁重试时间 单位：毫秒
     * */
    private static final int DEFAULT_RETRY_FIXED_TIME = 100;
    /**
     * 默认的加锁浮动时间区间 单位：毫秒
     * */
    private static final int DEFAULT_RETRY_TIME_RANGE = 10;
    /**
     * 默认的加锁重试次数
     * */
    private static final int DEFAULT_RETRY_COUNT = 30;

    @Resource
    private RedisClient redisClient;

    //===========================================api=======================================

    @Override
    public String lock(String lockKey) {
        String uuid = UUID.randomUUID().toString();

        return lock(lockKey,uuid);
    }

    @Override
    public String lock(String lockKey, int expireTime) {
        String uuid = UUID.randomUUID().toString();

        return lock(lockKey,uuid,expireTime);
    }

    @Override
    public String lock(String lockKey, String requestID) {
        return lock(lockKey,requestID,DEFAULT_EXPIRE_TIME_SECOND);
    }

    @Override
    public String lock(String lockKey, String requestID, int expireTime) {
        List<String> keyList = Collections.singletonList(lockKey);

        List<String> argsList = Arrays.asList(
                requestID,
                expireTime + ""
        );
        Long result = (Long)redisClient.eval(LuaScript.LOCK_SCRIPT, keyList, argsList);

        if(result.equals(ADD_LOCK_SUCCESS)){
            return requestID;
        }else{
            return null;
        }
    }

    @Override
    public String lockAndRetry(String lockKey) {
        String uuid = UUID.randomUUID().toString();

        return lockAndRetry(lockKey,uuid);
    }

    @Override
    public String lockAndRetry(String lockKey, String requestID) {
        return lockAndRetry(lockKey,requestID,DEFAULT_EXPIRE_TIME_SECOND);
    }

    @Override
    public String lockAndRetry(String lockKey, int expireTime) {
        String uuid = UUID.randomUUID().toString();

        return lockAndRetry(lockKey,uuid,expireTime);
    }

    @Override
    public String lockAndRetry(String lockKey, int expireTime, int retryCount) {
        String uuid = UUID.randomUUID().toString();

        return lockAndRetry(lockKey,uuid,expireTime,retryCount);
    }

    @Override
    public String lockAndRetry(String lockKey, String requestID, int expireTime) {
        return lockAndRetry(lockKey,requestID,expireTime,DEFAULT_RETRY_COUNT);
    }

    @Override
    public String lockAndRetry(String lockKey, String requestID, int expireTime, int retryCount) {
        if(retryCount <= 0){
            // retryCount小于等于0 无限循环，一直尝试加锁
            while(true){
                String result = lock(lockKey,requestID,expireTime);
                if(result != null){
                    return result;
                }

                LOGGER.info("加锁失败，稍后重试：lockKey={},requestID={}",lockKey,requestID);
                redisClient.increment("retryCount",1);
                // 休眠一会
                sleepSomeTime();
            }
        }else{
            // retryCount大于0 尝试指定次数后，退出
            for(int i=0; i<retryCount; i++){
                String result = lock(lockKey,requestID,expireTime);
                if(result != null){
                    return result;
                }

                // 休眠一会
                sleepSomeTime();
            }

            return null;
        }
    }

    @Override
    public boolean unLock(String lockKey, String requestID) {
        List<String> keyList = Collections.singletonList(lockKey);

        List<String> argsList = Collections.singletonList(requestID);

        Object result = redisClient.eval(LuaScript.UN_LOCK_SCRIPT, keyList, argsList);

        // 释放锁成功
        return RELEASE_LOCK_SUCCESS.equals(result);
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

    /**
     * 当前线程 休眠一端时间
     * */
    private void sleepSomeTime(){
        // 重试时间 单位：毫秒
        int retryTime = getFinallyGetLockRetryTime();
        try {
            Thread.sleep(retryTime);
        } catch (InterruptedException e) {
            throw new RuntimeException("redis锁重试时，出现异常",e);
        }
    }
}
