package lock.api;

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
     * 尝试加锁 可重入
     * @param lockKey 锁的key
     * @return 加锁成功 返回uuid
     *          加锁失败 返回null
     * */
    String lock(String lockKey, String requestID);

    /**
     * 释放锁
     * @param lockKey 锁的key
     * @param requestID 用户ID
     * @return true     释放自己所持有的锁 成功
     *          false    释放自己所持有的锁 失败
     * */
    boolean unLock(String lockKey, String requestID);

    /**
     * 尝试加锁，失败自动重试 会阻塞当前线程
     * @param lockKey 锁的key
     * @return 加锁成功 返回uuid
     *          加锁失败 返回null
     * */
    String lockAndRetry(String lockKey);
}
