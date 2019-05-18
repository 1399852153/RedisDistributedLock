package com.xiongyx.service.impl;

import com.xiongyx.service.api.RedisClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author pc
 */
public class RedisClientImpl implements RedisClient {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;


    @Override
    public String get(final String key) {
        return (String)redisTemplate.opsForValue().get(key);
    }

    @Override
    public void set(final String key, final String value) {
        redisTemplate.opsForValue().set(key,value);
    }

    @Override
    public Object eval(String script, List<String> keys, List<String> args) {
        return redisTemplate.execute(new RedisScript<Object>() {
            @Override
            public String getSha1() {
                return null;
            }

            @Override
            public Class<Object> getResultType() {
                return null;
            }

            @Override
            public String getScriptAsString() {
                return null;
            }
        },keys,args);
    }

}