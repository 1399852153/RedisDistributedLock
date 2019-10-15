package com.xiongyx.service.impl;

import com.xiongyx.annotation.RedisLock;
import com.xiongyx.annotation.RedisLockKey;
import com.xiongyx.aspect.RedisLockAspect;
import com.xiongyx.enums.RedisLockKeyType;
import com.xiongyx.service.api.TestService;
import com.xiongyx.service.api.TestService2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author xiongyx
 * on 2019/4/12.
 */
@Service
public class TestServiceImpl implements TestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestServiceImpl.class);

    private int number = 0;

    @Autowired
    private TestService2 testService2;

    @Override
    @RedisLock(lockKey = "lockKey", retryCount = 50, expireTime = 100)
    public String method1(@RedisLockKey(type = RedisLockKeyType.ALL) String num) throws InterruptedException {
        int sleepMS = 3000;
        Thread.sleep(sleepMS);
        number = number + 10;
        LOGGER.info("method1 ... 休眠{}ms num={}",sleepMS,num);
        testService2.method2(num);
        return "method1 " + number;
    }
}
