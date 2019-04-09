package lock.constants;

/**
 * @Author xiongyx
 * on 2019/4/9.
 */
public class LuaScript {

    /**
     * 解锁脚本
     * */
    public static final String UN_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] "
            + "then return redis.call('del', KEYS[1]) "
            + "else return 0 end";

    /**k
     * 可重入锁更新脚本 todo
     *
     * 1. 判断key是否存在
     * 2. 如果存在，判断requestID是否相等
     *            相等，则删除掉key重新创建新的key值，重置过期时间
     *            不相等，说明已经被抢占，加锁失败，返回null
     * 3. 如果不存在，说明恰好已经过期，重新生成key
     * */
    public static final String RLOCK_SCRIPT = "";
}
