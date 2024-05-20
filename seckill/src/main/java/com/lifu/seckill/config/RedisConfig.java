package com.lifu.seckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String , Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        //序列化设置
        //key
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //value
        // -- GenericJackson2JsonRedisSerializer转json数据,且不需要类对象
        // -- Jackson2JsonRedisSerializer 转java字符串,需要类对象
        // -- JdkSerializationRedisSerializer 产生二进制
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        //hash类型也是k-v形式
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;

    }
}
