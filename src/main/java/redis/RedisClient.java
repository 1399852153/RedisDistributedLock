package redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

/**
 * @Author xiongyx
 * @Date 2019/4/3
 */
public class RedisClient {

    private JedisPool pool;

    private static RedisClient instance = new RedisClient();

    private RedisClient() {
        init();
    }

    public static RedisClient getInstance(){
        return instance;
    }

    public Object eval(String script, List<String> keys, List<String> args) {
        Jedis jedis = getJedis();
        Object result = jedis.eval(script, keys, args);
        jedis.close();
        return result;
    }

    public String set(final String key, final String value, final String nxxx, final String expx, final int time) {
        Jedis jedis = getJedis();
        String result = jedis.set(key, value, nxxx, expx, time);
        jedis.close();
        return result;
    }

    private void init(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(10);
        pool = new JedisPool(jedisPoolConfig, "localhost",6379);
        System.out.println("连接池初始化成功");
    }

    private Jedis getJedis(){
        return pool.getResource();
    }
}
