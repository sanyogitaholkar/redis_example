package com.example.redis_example.controller;

import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.redis_example.service.ClickService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/c")
public class ClickController {

    @Autowired
    private ClickService clickService;

    /*
     * “After a click is logged, the service returns a 302 redirect. The browser
     * follows the redirect and displays the destination page, which is why HTML
     * content from the advertiser’s site is shown instead of a JSON response.”
     */
    @GetMapping("/click_event")
    public ResponseEntity<Void> handleClick(
            @RequestParam Long adId,
            @RequestParam Long campaignId,
            HttpServletRequest request) {

        String redirectUrl = clickService.logClickAndGetRedirectUrl(adId, campaignId, request);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
/*
 * Below is a practical Spring Boot–style refactor to handle thousands of click
 * requests efficiently and to measure DB time with and without cache for
 * redirectUrl.
 * The key ideas are:
 * 
 * Do not save synchronously in the controller
 * 
 * Use async + batching for DB writes
 * 
 * Cache redirectUrl (e.g., Redis / Caffeine)
 * 
 * Measure DB vs cache latency separately
 */
/*
 * Yes — **almost right**. Short and precise correction 👇
 ** 
 * Actual flow:**
 * 
 * 1️⃣ Request comes in (filter for oneUserperrequest window using redis)
 * 2️⃣ **Redirect URL is fetched synchronously** (Redis cache → DB fallback(if
 * the redirect url not present in database))
 * 3️⃣ **Click event is enqueued asynchronously** into `BlockingQueue` (capacity
 * = 100,000)
 * 4️⃣ **User is redirected immediately (302)**
 * 5️⃣ **Every 100 ms**, the scheduler drains a batch from the queue and inserts
 * into DB
 ** 
 * Key clarification:**
 * 
 * Queue capacity **limits memory**, it does **not** trigger batching
 * Batch insert timing is controlled by the **scheduler (100 ms)**
 * 
 * ✔ Redirect is never delayed by DB insert
 * ✔ DB writes happen fully in background
 */
