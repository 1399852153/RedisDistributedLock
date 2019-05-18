package com.xiongyx.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author xiongyx
 * @date 2019/4/3
 */
@Component
public class RedisClient {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public Object eval(String script, List<String> keys, List<String> args) {
        Object result = redisTemplate.execute(
                new RedisScript<Object>() {
                    @Override
                    public String getSha1() {
                        return null;
                    }

                    @Override
                    public Class<Object> getResultType() {
                        return Object.class;
                    }

                    @Override
                    public String getScriptAsString() {
                        return script;
                    }
                }
        ,keys,args);

        return result;
    }
}
