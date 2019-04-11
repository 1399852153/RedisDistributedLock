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
     * 加锁脚本 lock.lua
     * 1. 判断key是否存在
     * 2. 如果存在，判断requestID是否相等
     *            相等，则删除掉key重新创建新的key值，重置过期时间
     *            不相等，说明已经被抢占，加锁失败，返回null
     * 3. 如果不存在，说明恰好已经过期，重新生成key
     * */
    public static String LOCK_SCRIPT = "";

    /**
     * 解锁脚本 unlock.lua
     * */
    public static String UN_LOCK_SCRIPT = "";

    public static void init(){
        try {
            initLockScript();
            initUnLockScript();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initLockScript() throws IOException {
        String filePath = "src/main/resources/lock.lua";
        LOCK_SCRIPT = readFile(filePath);
    }

    private static void initUnLockScript() throws IOException {
        String filePath = "src/main/resources/unlock.lua";
        UN_LOCK_SCRIPT = readFile(filePath);
    }

    private static String readFile(String filePath) throws IOException {
        try (
            FileReader reader = new FileReader(filePath);
            BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append(System.lineSeparator());
            }

            return stringBuilder.toString();
        }
    }


    public static void main(String[] args) {
//        printTest();

//        printLockScript();

        init();
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
