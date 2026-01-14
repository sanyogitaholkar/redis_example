package com.example.redis_example.filter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {
    /*
     * The OncePerRequestFilter is an abstract base class in the Spring framework
     * that guarantees a filter is executed exactly once for a single HTTP request
     */
    private static final int USER_LIMIT = 7; // per user per minute
    private static final int GLOBAL_LIMIT = 100; // global per minute
    private static final int WINDOW_SECONDS = 60;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 1️⃣ Identify user (IP-based for simplicity)
        String userId = getUserId(request);

        // 2️⃣ Redis keys
        String userKey = "rate:user:" + userId;
        String globalKey = "rate:global";

        // 3️⃣ Increment counters
        Long userCount = redisTemplate.opsForValue().increment(userKey);
        Long globalCount = redisTemplate.opsForValue().increment(globalKey);

        // 4️⃣ Set expiry on first hit
        if (userCount != null && userCount == 1) {
            redisTemplate.expire(userKey, WINDOW_SECONDS, TimeUnit.SECONDS);
        }
        if (globalCount != null && globalCount == 1) {
            redisTemplate.expire(globalKey, WINDOW_SECONDS, TimeUnit.SECONDS);
        }

        // 5️⃣ Enforce limits
        if (userCount != null && userCount > USER_LIMIT) {
            block(response, "User rate limit exceeded");
            return;
        }

        if (globalCount != null && globalCount > GLOBAL_LIMIT) {
            block(response, "Global rate limit exceeded");
            return;
        }

        // 6️⃣ Continue request
        filterChain.doFilter(request, response);
    }

    private String getUserId(HttpServletRequest request) {
        // Prefer X-Forwarded-For in real systems
        String ip = request.getHeader("X-Forwarded-For");
        return (ip != null) ? ip.split(",")[0] : request.getRemoteAddr();
    }

    private void block(HttpServletResponse response, String message)
            throws IOException {

        response.setStatus(429);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"error\": \"" + message + "\"}");
    }
}
/*
 * Alright! Let’s make this super simple. Imagine your web app is like a big
 * office building.
 * 
 * A visitor (the HTTP request) comes to the front door.
 * 
 * There are security guards (filters) in the hallways checking things like ID
 * cards, logging visitor info, and making sure rules are followed.
 * 
 * Now, sometimes the visitor has to go to multiple rooms inside the building
 * (internal forwards to different servlets or pages). Every time they go
 * through a new hallway, the guards see them again and do all the checks over
 * and over. That can be annoying! Imagine having to show your ID five times
 * just to get a sandwich—ugh!
 * 
 * The OncePerRequestFilter is like giving the visitor a special wristband the
 * first time they pass a guard. When the visitor goes to the next rooms, the
 * guards see the wristband and say:
 * "Oh, you already got checked. You can go ahead!"
 * 
 * ✅ So the visitor only gets checked once, even if they move around the
 * building multiple times.
 * This prevents problems like:
 * Double-logging (writing the same info twice)
 * Doing security checks again and again
 * Wasting time parsing the visitor’s info multiple times
 * In short: OncePerRequestFilter is a magic wristband for requests so the
 * filter only acts once per visitor.
 * If you want, I can draw a tiny office diagram to make it even easier to see.
 * Do you want me to do that?
 */

/*
 * Common Use Cases
 * You should extend OncePerRequestFilter when implementing custom filters for
 * tasks that must only occur once per request lifecycle:
 * JWT Authentication: Extracting and validating a JSON Web Token from the
 * header and setting the security context.
 * Logging: Accurately logging request details (URI, method, etc.) without
 * duplication.
 * CORS Handling: Managing Cross-Origin Resource Sharing (CORS) headers
 * consistently.
 * Request/Response Wrapping: Modifying the request or response objects before
 * they reach the rest of the application.
 */
/*
 * It only handles internal forwards of the same request, not multiple new
 * clicks from the same user.
 * 
 * So it cannot solve this "click spam" problem on its own.
 */