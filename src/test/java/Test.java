import lock.api.DistributeLock;
import lock.impl.RedisDistributeLock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Author xiongyx
 * @Date 2019/4/3
 */
public class Test {

    private static final String TEST_REDIS_LOCK_KEY = "lock_key";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        testLock();
    }

    private static void testLock() throws ExecutionException, InterruptedException {
        int threadNum = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        List<Future> futureList = new ArrayList<>();
        for(int i=0; i<threadNum; i++){
            int currentThreadNum = i;
            Future future = executorService.submit(()->{
                DistributeLock distributeLock = RedisDistributeLock.getInstance();
                System.out.println("线程尝试获得锁 i=" + currentThreadNum);
                String requestID = distributeLock.lockAndRetry(TEST_REDIS_LOCK_KEY);
                System.out.println("获得锁，开始执行任务 requestID=" + requestID + "i=" + currentThreadNum);

                if(currentThreadNum == 1){
                    System.out.println("模拟 宕机事件 不释放锁，直接返回 currentThreadNum=" + currentThreadNum);
                    return;
                }

                try {
                    // 休眠完毕
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务执行完毕" + "i=" + currentThreadNum);
                distributeLock.unlock(TEST_REDIS_LOCK_KEY,requestID);
                System.out.println("释放锁完毕");
            });

            futureList.add(future);
        }

        for(Future future : futureList){
            future.get();
        }
    }
}
