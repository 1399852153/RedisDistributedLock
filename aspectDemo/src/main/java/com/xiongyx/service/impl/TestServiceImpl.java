package com.xiongyx.service.impl;

import com.xiongyx.annotation.RedisLock;
import com.xiongyx.service.api.TestService;
import org.springframework.stereotype.Service;

/**
 * @Author xiongyx
 * on 2019/4/12.
 */
@Service("testService")
public class TestServiceImpl implements TestService {

    @Override
    @RedisLock(lockKey = "lockKey", expireTime = 100)
    public void method1(String num) throws InterruptedException {
        Thread.sleep(100);
        System.out.println("method1 ... num=" + num);
    }

    @Override
    @RedisLock(lockKey = "lockKey", expireTime = 100)

    public void method2(String num) throws InterruptedException {
        Thread.sleep(100);
        System.out.println("method2 ... num=" + num);
    }
}
