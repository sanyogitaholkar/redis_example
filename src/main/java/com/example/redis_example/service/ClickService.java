package com.example.redis_example.service;

import java.time.LocalDateTime;

import org.apache.el.stream.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.redis_example.entity.AdClick;
import com.example.redis_example.entity.Advertisments;
import com.example.redis_example.repo.AdClickRepository;
import com.example.redis_example.repo.AdsRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ClickService {

    @Autowired
    private AdClickRepository repository;

    @Autowired
    private AdsRepository repo;

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

            /*
             * Why redirect after saving click?
             * Because:
             * Advertiser only sees real user traffic
             * You track every click
             * Analytics is accurate
             */
            // normally fetched from DB

            Advertisments ads = repo.findById(adId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Ad not found for id: " + adId));

            redirectUrl = ads.getRedirectURL();
            /*
             * Why DB is slow:
             * Network IO
             * Disk fsync
             * Locks
             * Even a fast DB write costs 5–20 ms.
             * At 100k clicks/sec → impossible.
             */
            click.setRedirectUrl(redirectUrl);
            repository.save(click);

        } catch (Exception e) {
            System.err.println(e);
        }
        return redirectUrl;
    }

}
