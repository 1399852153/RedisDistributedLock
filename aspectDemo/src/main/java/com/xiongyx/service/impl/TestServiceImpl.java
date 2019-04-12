package com.xiongyx.service.impl;

import com.xiongyx.service.api.TestService;
import org.springframework.stereotype.Service;

/**
 * @Author xiongyx
 * on 2019/4/12.
 */
@Service("testService")
public class TestServiceImpl implements TestService {

    @Override
    public void method1() {
        System.out.println("method1 ...");
    }

    @Override
    public void method2() {
        System.out.println("method2 ...");
    }
}
