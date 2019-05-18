package com.xiongyx.controller;

import com.xiongyx.lock.impl.RedisDistributeLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author xiongyx
 * on 2019/5/18.
 */

@RestController
public class TestController {

    private static final String TEST_REDIS_LOCK_KEY = "lock_key";

    private static final int EXPIRE_TIME = 10;

    @Autowired
    private RedisDistributeLock redisDistributeLock;

    @RequestMapping("/test")
    public String test() throws ExecutionException, InterruptedException {
        int threadNum = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        List<Future> futureList = new ArrayList<>();
        for(int i=0; i<threadNum; i++){
            int currentThreadNum = i;
            Future future = executorService.submit(()->{
                System.out.println("线程尝试获得锁 i=" + currentThreadNum);
                String requestID = redisDistributeLock.lockAndRetry(TEST_REDIS_LOCK_KEY,EXPIRE_TIME);
                System.out.println("获得锁，开始执行任务 requestID=" + requestID + "i=" + currentThreadNum);

//                if(currentThreadNum == 1){
//                    System.out.println("模拟 宕机事件 不释放锁，直接返回 currentThreadNum=" + currentThreadNum);
//                    return;
//                }

                try {
                    // 休眠完毕
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务执行完毕" + "i=" + currentThreadNum);
                redisDistributeLock.unLock(TEST_REDIS_LOCK_KEY,requestID);
                System.out.println("释放锁完毕");

                redisDistributeLock.lockAndRetry(TEST_REDIS_LOCK_KEY,requestID,EXPIRE_TIME);
                System.out.println("重入获得锁，开始执行任务 requestID=" + requestID + "i=" + currentThreadNum);
                redisDistributeLock.unLock(TEST_REDIS_LOCK_KEY,requestID);
                System.out.println("释放重入锁完毕");
            });

            futureList.add(future);
        }

        for(Future future : futureList){
            future.get();
        }

        return "ok";
    }
}
