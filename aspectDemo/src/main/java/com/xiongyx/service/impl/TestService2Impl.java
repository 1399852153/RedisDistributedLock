package com.xiongyx.service.impl;

import com.xiongyx.annotation.RedisLock;
import com.xiongyx.annotation.RedisLockKey;
import com.xiongyx.enums.RedisLockKeyType;
import com.xiongyx.service.api.TestService2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author xiongyx
 * on 2019/10/15.
 */
@Service
public class TestService2Impl implements TestService2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestService2Impl.class);

    @Override
    @RedisLock(lockKey = "lockKey", expireTime = 100, retryCount = 3)
    public String method2(@RedisLockKey(type = RedisLockKeyType.ALL) String num) throws InterruptedException {
        int sleepMS = 1000;
        Thread.sleep(sleepMS);
        LOGGER.info("method2 ... 休眠{}ms num={}",sleepMS,num);
        return "method2";
    }
}
