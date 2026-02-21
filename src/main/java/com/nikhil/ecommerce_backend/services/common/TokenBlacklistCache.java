package com.nikhil.ecommerce_backend.services.common;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class TokenBlacklistCache {

    private final CacheManager cacheManager;
    private static final String CACHE_NAME = "blacklistedTokens";

    public TokenBlacklistCache(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void cacheBlacklistedToken(String token, LocalDateTime expiry) {

        cacheManager.getCache(CACHE_NAME).put(token, expiry);
    }

    public boolean isBlacklisted(String token) {
        LocalDateTime expiry = cacheManager.getCache(CACHE_NAME).get(token, LocalDateTime.class);
        return expiry != null && expiry.isAfter(LocalDateTime.now());
    }
}

