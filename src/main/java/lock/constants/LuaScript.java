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

    /**
     * 可重入锁更新脚本
     * */
    public static final String RLOCK_SCRIPT = "";
}
