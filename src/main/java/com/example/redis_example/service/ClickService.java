package com.example.redis_example.service;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.redis_example.queue.ClickEventQueue;
import com.example.redis_example.entity.AdClick;
import com.example.redis_example.entity.Advertisments;
import com.example.redis_example.repo.AdClickRepository;
import com.example.redis_example.repo.AdsRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ClickService {

    private final ClickEventQueue queue;

    public ClickService(ClickEventQueue queue) {
        this.queue = queue;
    }

    /*
     * This service is:
     * Stateless
     * High-throughput
     * Separate from business services
     */
    public String logClickAndGetRedirectUrl(
            Long adId,
            Long campaignId,
            HttpServletRequest request) {
        String redirectUrl = "xyz";

        try {
            AdClick click = new AdClick();
            click.setAdId(adId);
            click.setCampaignId(campaignId);
            click.setUserIp(request.getRemoteAddr());
            click.setUserAgent(request.getHeader("User-Agent"));
            click.setClickedAt(LocalDateTime.now());

            queue.add(click);

        } catch (Exception e) {
            System.err.println(e);
        }
        return redirectUrl;
    }
    /*
     * Queue capacity = 100,000
     * Batch size = 1000
     * Scheduler = every 100 ms
     * 
     * #### Scenario: 10,000 requests/sec
     * 
     * ```
     * 0 ms → 1000 events added
     * 100 ms → scheduler drains 1000 → DB insert
     * 200 ms → drains next 1000
     */

}
