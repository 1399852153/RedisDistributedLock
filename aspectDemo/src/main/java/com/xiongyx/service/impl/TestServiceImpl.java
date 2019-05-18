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
    @RedisLock(lockKey = "lockKey", expireTime = 100, retryCount = RedisLock.unLimitRetryCount)
    public String method1(String num) throws InterruptedException {
        int sleepMS = 1000;
        Thread.sleep(sleepMS);
        LOGGER.info("method1 ... 休眠{}ms num={}",sleepMS,num);
        return "method1";
    }

    @Override
    @RedisLock(lockKey = "lockKey", expireTime = 100, retryCount = 3)
    public String method2(String num) throws InterruptedException {
        int sleepMS = 1000;
        Thread.sleep(sleepMS);
        LOGGER.info("method2 ... 休眠{}ms num={}",sleepMS,num);
        return "method2";
    }
}
