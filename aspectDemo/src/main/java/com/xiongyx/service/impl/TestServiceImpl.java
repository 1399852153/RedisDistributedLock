package com.xiongyx.service.impl;

import com.xiongyx.annotation.RedisLock;
import com.xiongyx.aspect.RedisLockAspect;
import com.xiongyx.service.api.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Author xiongyx
 * on 2019/4/12.
 */
@Service("testService")
public class TestServiceImpl implements TestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLockAspect.class);

    @Override
    @RedisLock(lockKey = "lockKey", expireTime = 100)
    public void method1(String num) throws InterruptedException {
        Thread.sleep(100);
        LOGGER.info("method1 ... 休眠100ms num=" + num);
    }

    @Override
    @RedisLock(lockKey = "lockKey", expireTime = 100)

    public void method2(String num) throws InterruptedException {
        Thread.sleep(100);
        LOGGER.info("method2 ... 休眠100ms num=" + num);
    }
}
