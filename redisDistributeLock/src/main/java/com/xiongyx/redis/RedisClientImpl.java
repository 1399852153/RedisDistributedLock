package com.xiongyx.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xiongyx
 * @date 2019/4/3
 */
@Component("redisClient")
public class RedisClientImpl implements RedisClient{

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
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

    @Override
    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void set(String key,Object value){
        redisTemplate.opsForValue().set(key,value);
    }

    @Override
    public void set(String key, Object value, long expireTime, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key,value,expireTime,timeUnit);
    }

    @Override
    public Boolean setNX(String key, Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key,value);
    }

    @Override
    public Boolean expire(String key, long time, TimeUnit timeUnit) {
        return redisTemplate.boundValueOps(key).expire(time, timeUnit);
    }

    @Override
    public Boolean persist(String key){
        return redisTemplate.boundValueOps(key).persist();
    }

    @Override
    public Long increment(String key, long number) {
        return redisTemplate.opsForValue().increment(key, number);
    }

    @Override
    public Double increment(String key, double number) {
        return redisTemplate.opsForValue().increment(key, number);
    }

    @Override
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public void hset(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public void hsetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public Boolean hsetNX(String key, String hashKey, Object value) {
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    @Override
    public Object hget(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key,hashKey);
    }

    @Override
    public Map hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

}
