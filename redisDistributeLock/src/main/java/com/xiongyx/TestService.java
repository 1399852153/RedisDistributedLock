package com.xiongyx;

import com.xiongyx.lock.impl.RedisDistributeLock;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * Description:
 * <p>
 *
 * @author zhangjw
 * @version 1.0
 */
@Service
public class TestService {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestService.class);

    @Resource
    private RedisDistributeLock redisDistributeLock;


    private AtomicInteger count = new AtomicInteger();


    @Transactional
    public void exeTask(Integer i) {
        try {
            String s = redisDistributeLock.lockAndRetry(i.toString(), 2000, 1);
            TimeUnit.SECONDS.sleep(8);
            count.incrementAndGet();
            redisDistributeLock.unLock(i.toString(),s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void monitor() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate((Runnable) () -> {

            int qps = count.getAndSet(0);
            LOGGER.info("qps = {}", qps);

        }, 0, 1, TimeUnit.SECONDS);

    }

}
