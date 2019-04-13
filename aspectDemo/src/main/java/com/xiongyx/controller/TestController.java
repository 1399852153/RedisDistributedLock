package com.xiongyx.controller;

import com.xiongyx.service.api.TestService;
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

    @Autowired
    private TestService testService;

    @RequestMapping("/test")
    @ResponseBody
    public String test(HttpServletRequest request){
        String num = request.getParameter("num");
        System.out.println("接收到请求 num=" + num);

        testService.method1(num);

        testService.method2(num);

        System.out.println("请求处理完毕 num=" + num);
        return "ok";
    }
}
