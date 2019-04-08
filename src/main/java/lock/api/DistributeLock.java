package lock.api;

/**
 * @Author xiongyx
 * @Date 2019/4/8
 *
 * 分布式锁 api接口
 */
public interface DistributeLock {

    String lock(String lockKey);

    boolean unlock(String lockKey, String requestID);

    String lockAndRetry(String lockKey);
}
