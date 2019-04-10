package lock.script;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @Author xiongyx
 * on 2019/4/9.
 */
public class LuaScript {

    /**
     * 加锁脚本
     * 1. 判断key是否存在
     * 2. 如果存在，判断requestID是否相等
     *            相等，则删除掉key重新创建新的key值，重置过期时间
     *            不相等，说明已经被抢占，加锁失败，返回null
     * 3. 如果不存在，说明恰好已经过期，重新生成key
     * */
    public static final String LOCK_SCRIPT = ""
        + "local requestIDKey = KEYS[1] "
        + "local lockCountKey = KEYS[2] "
        + "local currentRequestID = ARGV[1] "
        + "local expireTimeTTL = ARGV[2] "
        + "local lockSet = redis.call('setnx',requestIDKey,currentRequestID) "
        + "if lockSet == 1 "
        + "then "
        + "redis.call('expire',requestIDKey,expireTimeTTL) "
        + "redis.call('set',lockCountKey,1) "
        + "redis.call('expire',lockCountKey,expireTimeTTL) "
        + "return 1 "
        + "else "
        + "local oldRequestID = redis.call('get',requestIDKey) "
        + "if currentRequestID == oldRequestID "
        + "then "
        + "redis.call('incr',lockCountKey) "
        + "redis.call('expire',requestIDKey,expireTimeTTL) "
        + "redis.call('expire',lockCountKey,expireTimeTTL) "
        + "return 1 "
        + "else return 0  "
        + "end "
        + "end ";

    /**
     * 解锁脚本 todo
     * */
        public static final String UN_LOCK_SCRIPT = "";

    public static void main(String[] args) {
//        printTest();

        printLockScript();
    }

    private static void printTest(){
        String pathname = "C:\\Users\\xiongyx\\Desktop\\lua.txt";
        print(pathname);
    }

    private static void printLockScript(){
        String pathname = "C:\\Users\\xiongyx\\Desktop\\luaLock.txt";
        print(pathname);
    }

    private static void print(String pathName){
        try (FileReader reader = new FileReader(pathName);
            BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                stringBuilder.append(line + " ");
            }
            System.out.println("==================================");
            System.out.println("eval \"" + stringBuilder + "\"" + getKeys() + getArgs());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getKeys(){
        return " 2 lockKey lockCountKey ";
    }

    private static String getArgs(){
        return " user111 1000";
    }
}
