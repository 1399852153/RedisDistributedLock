package com.xiongyx.controller;

import com.xiongyx.config.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiongyx
 * @date 2019/5/17
 */
@RestController
public class HelloController {

    @Autowired
    private RedisConfig redisConfig;

    @RequestMapping("/test")
    public String hello(){
        return redisConfig.toString();
    }
}
