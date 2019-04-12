package com.xiongyx.controller;

import com.xiongyx.service.api.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author xiongyx
 * on 2019/4/12.
 */
@Controller
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        testService.method1();

        testService.method2();

        return "ok";
    }
}
