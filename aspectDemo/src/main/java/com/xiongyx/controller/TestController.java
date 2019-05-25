package com.xiongyx.controller;

import com.xiongyx.aspect.RedisLockAspect;
import com.xiongyx.service.api.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author xiongyx
 * on 2019/4/12.
 */
@CrossOrigin
@Controller
public class TestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestController.class);


    @Autowired
    private TestService testService;

    @RequestMapping("/test")
    @ResponseBody
    public String test(HttpServletRequest request) throws InterruptedException {
        String num = request.getParameter("num");
        LOGGER.info("接收到请求 num=" + num);

        String method1Result = testService.method1(num);

//        String method2Result = testService.method2(num);

//        LOGGER.info("请求处理完毕 num={},method1Result={},method2Result={}",num,method1Result,method2Result);

        LOGGER.info("请求处理完毕 num={},method1Result={}",num,method1Result);
        return "ok";
    }
}
