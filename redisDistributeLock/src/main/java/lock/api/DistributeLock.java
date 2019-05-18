package lock.api;

import org.springframework.stereotype.Component;

/**
 * 分布式锁 api接口
 */
public interface DistributeLock {

    /**
     * 尝试加锁
     * @param lockKey 锁的key
     * @return 加锁成功 返回uuid
     *          加锁失败 返回null
     * */
    String lock(String lockKey);

    /**
     * 尝试加锁 (requestID相等 可重入)
     * @param lockKey 锁的key
     * @param expireTime 过期时间 单位：秒
     * @return 加锁成功 返回uuid
     *          加锁失败 返回null
     * */
    String lock(String lockKey, int expireTime);

    /**
     * 尝试加锁 (requestID相等 可重入)
     * @param lockKey 锁的key
     * @param requestID 用户ID
     * @return 加锁成功 返回uuid
     *          加锁失败 返回null
     * */
    String lock(String lockKey, String requestID);

    /**
     * 尝试加锁 (requestID相等 可重入)
     * @param lockKey 锁的key
     * @param requestID 用户ID
     * @param expireTime 过期时间 单位：秒
     * @return 加锁成功 返回uuid
     *          加锁失败 返回null
     * */
    String lock(String lockKey, String requestID, int expireTime);

    /**
     * 尝试加锁，失败自动重试 会阻塞当前线程
     * @param lockKey 锁的key
     * @return 加锁成功 返回uuid
     *          加锁失败 返回null
     * */
    String lockAndRetry(String lockKey);

    /**
     * 尝试加锁，失败自动重试 会阻塞当前线程 (requestID相等 可重入)
     * @param lockKey 锁的key
     * @param requestID 用户ID
     * @return 加锁成功 返回uuid
     *          加锁失败 返回null
     * */
    String lockAndRetry(String lockKey, String requestID);

    /**
     * 尝试加锁 (requestID相等 可重入)
     * @param lockKey 锁的key
     * @param expireTime 过期时间 单位：秒
     * @return 加锁成功 返回uuid
     *          加锁失败 返回null
     * */
    String lockAndRetry(String lockKey, int expireTime);

    /**
     * 尝试加锁 (requestID相等 可重入)
     * @param lockKey 锁的key
     * @param expireTime 过期时间 单位：秒
     * @param retryCount 重试次数
     * @return 加锁成功 返回uuid
     *          加锁失败 返回null
     * */
    String lockAndRetry(String lockKey, int expireTime, int retryCount);

    /**
     * 尝试加锁 (requestID相等 可重入)
     * @param lockKey 锁的key
     * @param requestID 用户ID
     * @param expireTime 过期时间 单位：秒
     * @return 加锁成功 返回uuid
     *          加锁失败 返回null
     * */
    String lockAndRetry(String lockKey, String requestID, int expireTime);

    /**
     * 尝试加锁 (requestID相等 可重入)
     * @param lockKey 锁的key
     * @param expireTime 过期时间 单位：秒
     * @param requestID 用户ID
     * @param retryCount 重试次数
     * @return 加锁成功 返回uuid
     *          加锁失败 返回null
     * */
    String lockAndRetry(String lockKey, String requestID, int expireTime, int retryCount);

    /**
     * 释放锁
     * @param lockKey 锁的key
     * @param requestID 用户ID
     * @return true     释放自己所持有的锁 成功
     *          false    释放自己所持有的锁 失败
     * */
    boolean unLock(String lockKey, String requestID);
}
