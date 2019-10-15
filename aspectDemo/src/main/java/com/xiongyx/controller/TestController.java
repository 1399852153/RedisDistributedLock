package com.xiongyx.controller;

import com.xiongyx.aspect.RedisLockAspect;
import com.xiongyx.service.api.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author xiongyx
 * on 2019/4/12.
 */
@Controller
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);


    @Autowired
    private TestService testService;

    @RequestMapping("/test/{abc}/bcd")
    @ResponseBody
    public String test1(@PathVariable("abc") String abc) throws InterruptedException {
        LOGGER.info("接收到请求 " + abc);

        testService.method1(abc);

        return "ok";
    }

    @RequestMapping("/test/{abc}/efg")
    @ResponseBody
    public String test2(@PathVariable("abc") String abc) throws InterruptedException {
        LOGGER.info("接收到请求 " + abc);

        return "ok";
    }

    @RequestMapping("/test/efg")
    @ResponseBody
    public String test3(@RequestParam String abc) throws InterruptedException {
        LOGGER.info("接收到请求 " + abc);

        return "ok";
    }
}
