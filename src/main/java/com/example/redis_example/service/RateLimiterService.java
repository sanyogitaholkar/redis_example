package com.example.redis_example.service;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {
    /*
     * 
     * This is Fixed Window Counter
     * You want to limit how many requests a client can make in a given time window,
     * for example:
     * 
     * 10 requests / minute / user
     * 
     * 100 requests / minute / IP
     */
    private static final int MAX_REQUESTS = 5;
    private static final int WINDOW_SECONDS = 60;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public boolean isAllowed(String clientId) {
        String key = "rate_limit:" + clientId;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, WINDOW_SECONDS, TimeUnit.SECONDS);
        }

        return count <= MAX_REQUESTS;
    }
}
/*
 * TODO : 🔹 User-based limit
 * rate_limit:user:{userId}
 * 
 * 🔹 Endpoint-based limit
 * rate_limit:{clientId}:{endpoint}
 * 
 * 🔹 Sliding Window (Lua Script)
 * 
 * Avoids burst issue
 * 
 * More accurate
 * 
 * 🔹 Bucket Algorithms
 * 
 * Token Bucket (industry standard)
 * 
 * Leaky Bucket
 */
