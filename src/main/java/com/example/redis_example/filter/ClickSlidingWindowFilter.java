package com.example.redis_example.filter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
A Filter is like a checkpoint in the request pipeline.

Every incoming HTTP request automatically passes through the filter chain before reaching a servlet or controller.

You do not manually call the filter; the servlet container (Tomcat, Jetty, etc.) does it for you. */
@Component
public class ClickSlidingWindowFilter extends OncePerRequestFilter {
    private static final int WINDOW_SECONDS = 60;
    /*
     * We implement a sliding window per user using Redis. Each user’s last click
     * timestamp is stored with a 1-minute TTL. Subsequent clicks within the TTL are
     * ignored or blocked, ensuring that all clicks from the same user in 1 minute
     * are counted as a single request.”
     */

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String userId = getUserId(request);
        String redisKey = "click:user:" + userId;
        long now = System.currentTimeMillis();

        String lastClickStr = redisTemplate.opsForValue().get(redisKey);

        if (lastClickStr != null) {
            long lastClick = Long.parseLong(lastClickStr);
            if ((now - lastClick) < WINDOW_SECONDS * 1000L) {
                // User clicked within 1 minute → block
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Click *_* rate limit exceeded\"}");
                return;
            }
        }

        // Allow click → store timestamp with TTL 1 min
        redisTemplate.opsForValue().set(redisKey,
                String.valueOf(now), WINDOW_SECONDS, TimeUnit.SECONDS);

        filterChain.doFilter(request, response);
    }

    /*
     * User clicks ad at 12:00:00
     * 
     * Redis key doesn’t exist → store timestamp → allowed
     * 
     * User clicks again at 12:00:30
     * 
     * Redis key exists, 30s < 60s → blocked
     * 
     * User clicks at 12:01:01
     * 
     * Redis key expired → allowed → new timestamp stored
     */
    private String getUserId(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return (ip != null) ? ip.split(",")[0] : request.getRemoteAddr();
    }
}
/*
 * filterChain.doFilter(request, response) → controller and service executed
 * 
 * Rate limiting happens after controller
 * 
 * You can log extra metrics
 * 
 * You can ignore clicks that happen too frequently
 * 
 * You could even roll back DB updates (but that requires a transactional
 * service)
 * 
 * Redis TTL ensures sliding-window logic
 */