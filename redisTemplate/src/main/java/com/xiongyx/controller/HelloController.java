package com.xiongyx.controller;

import com.xiongyx.config.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiongyx
 * @date 2019/5/17
 */
@RestController
public class HelloController {

    @Autowired
    private RedisConfig redisConfig;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @RequestMapping("/test")
    public String hello(@RequestParam("id")String id){
        String oldValue = (String)redisTemplate.opsForValue().get("userID");
        System.out.println(oldValue);

        redisTemplate.opsForValue().set("userID",id);

        String newValue = (String)redisTemplate.opsForValue().get("userID");
        System.out.println(newValue);

        return newValue;
    }
}
