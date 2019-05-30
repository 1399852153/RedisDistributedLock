package com.xiongyx.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * @author xiongyx
 * redis 相关配置
 */
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    @Autowired
    private JedisConfig jedisConfig;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory (){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(jedisConfig.getMaxActive());
        poolConfig.setMaxIdle(jedisConfig.getMaxIdle());
        int maxWait = Integer.parseInt(jedisConfig.getMaxWait().substring(0,jedisConfig.maxWait.length()-2));
        poolConfig.setMaxWaitMillis(maxWait);
        poolConfig.setMinIdle(jedisConfig.getMaxIdle());
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(false);
        poolConfig.setTestWhileIdle(true);

        int readTimeout = Integer.parseInt(jedisConfig.getTimeout().substring(0,jedisConfig.maxWait.length()-2));
        // 使用jedis连接池
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder()
            .usePooling().poolConfig(poolConfig)
            .and().readTimeout(Duration.ofMillis(readTimeout)).build();

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(jedisConfig.getDatabase());
        redisStandaloneConfiguration.setPort(jedisConfig.getPort());
        redisStandaloneConfiguration.setHostName(jedisConfig.getHost());

        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
    }

    /**
     * retemplate相关配置
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 配置连接工厂
        template.setConnectionFactory(factory);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer jacksonSeial = new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);

        // 值采用json序列化
        template.setValueSerializer(jacksonSeial);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jacksonSeial);
        template.afterPropertiesSet();

        return template;
    }
}