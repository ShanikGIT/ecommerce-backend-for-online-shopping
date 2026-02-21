package com.nikhil.ecommerce_backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.LocalDateTime;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Caffeine<Object, LocalDateTime> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfter(new Expiry<Object, LocalDateTime>() {
                    @Override
                    public long expireAfterCreate(Object key, LocalDateTime expiry, long currentTime) {
                        long ttlNanos = Duration.between(LocalDateTime.now(), expiry).toNanos();
                        return Math.max(ttlNanos, 0);
                    }

                    @Override
                    public long expireAfterUpdate(Object key, LocalDateTime expiry, long currentTime, long currentDuration) {
                        return expireAfterCreate(key, expiry, currentTime);
                    }

                    @Override
                    public long expireAfterRead(Object key, LocalDateTime expiry, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .maximumSize(10_000);
    }

    @Bean
    public CacheManager cacheManager(Caffeine<Object, LocalDateTime> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("blacklistedTokens");
        cacheManager.setCaffeine((Caffeine)caffeine);
        return cacheManager;
    }
}
