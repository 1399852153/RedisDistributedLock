package com.xiongyx.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

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
        DefaultRedisScript<Integer> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Integer.class);

        Object result = redisTemplate.execute((RedisCallback<Object>) redisConnection ->{
            Object nativeConnection = redisConnection.getNativeConnection();
            // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
            // 集群模式
            if (nativeConnection instanceof JedisCluster) {
                return (Long) ((JedisCluster) nativeConnection).eval(script, keys, args);
            }

            // 单机模式
            else if (nativeConnection instanceof Jedis) {
                return (Long) ((Jedis) nativeConnection).eval(script, keys, args);
            }
            return -1L;
        });
        return result;
    }

    public String get(String key){
        return (String)redisTemplate.opsForValue().get(key);
    }

    public void set(String key,String value){
        redisTemplate.opsForValue().set(key,value);
    }
}
