package com.example.redis_example.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;

@Configuration
@EnableCaching
public class RedisCacheConfig {
    /*
     * CacheManager is a core interface in Spring's caching abstraction. It provides
     * methods for managing and accessing cache instances. Spring Boot can
     * automatically configure a CacheManager based on the cache provider (e.g.,
     * Redis, EhCache, etc.) that you have in your application.
     * You can configure a CacheManager to define properties like:
     * 1. Cache expiration time
     * 2. Cache eviction strategies
     * 3. Serialization methods
     */

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        cacheConfiguration.entryTtl(Duration.ofMinutes(2));
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
    /*
     * When you need a custom Redis bean
     * 
     * You might want a custom RedisTemplate or StringRedisTemplate if:
     * 
     * You need custom serialization (JSON, Protobuff, etc.)
     * 
     * You want different TTL settings or multiple Redis DBs
     * 
     * You want Lettuce vs Jedis client explicitly
     */
}
