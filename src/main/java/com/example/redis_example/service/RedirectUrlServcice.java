package com.example.redis_example.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.redis_example.entity.Advertisments;
import com.example.redis_example.repo.AdsRepository;

@Service
public class RedirectUrlServcice {

    private final AdsRepository repo;

    public RedirectUrlServcice(AdsRepository repo) {
        this.repo = repo;
    }

    // WITH CACHE
    @Cacheable(value = "redirectUrls", key = "#adId")
    public String getRedirectUrlCached(Long adId) {
        return getRedirectUrl(adId);
    }

    // WITHOUT CACHE
    public String getRedirectUrlDb(Long adId) {
        return getRedirectUrl(adId);
    }

    // SWITCH HERE
    public String getRedirectUrl(Long adId) {
        long start = System.nanoTime();

        Advertisments ads = repo.findById(adId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ad not found for id: " + adId));

        String redirectUrl = ads.getRedirectURL();
        /*
         * Why DB is slow:
         * Network IO
         * Disk fsync
         * Locks
         * Even a fast DB write costs 5–20 ms.
         * At 100k clicks/sec → impossible.
         */

        long end = System.nanoTime();
        // log.info("Redirect lookup time (ns): {}", (end - start));

        return ads.getRedirectURL();
    }
}
