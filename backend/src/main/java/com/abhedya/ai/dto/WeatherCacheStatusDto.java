package com.abhedya.ai.dto;

import java.time.LocalDateTime;
import java.util.List;

public class WeatherCacheStatusDto {

    private int cachedEntriesCount;
    private long cacheHits;
    private long cacheMisses;
    private double hitRatioPercent;
    private int defaultTtlSeconds;
    private List<String> cachedKeys;
    private LocalDateTime checkedAt;

    public WeatherCacheStatusDto() {
        this.checkedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getCachedEntriesCount() { return cachedEntriesCount; }
    public void setCachedEntriesCount(int cachedEntriesCount) { this.cachedEntriesCount = cachedEntriesCount; }

    public long getCacheHits() { return cacheHits; }
    public void setCacheHits(long cacheHits) { this.cacheHits = cacheHits; }

    public long getCacheMisses() { return cacheMisses; }
    public void setCacheMisses(long cacheMisses) { this.cacheMisses = cacheMisses; }

    public double getHitRatioPercent() { return hitRatioPercent; }
    public void setHitRatioPercent(double hitRatioPercent) { this.hitRatioPercent = hitRatioPercent; }

    public int getDefaultTtlSeconds() { return defaultTtlSeconds; }
    public void setDefaultTtlSeconds(int defaultTtlSeconds) { this.defaultTtlSeconds = defaultTtlSeconds; }

    public List<String> getCachedKeys() { return cachedKeys; }
    public void setCachedKeys(List<String> cachedKeys) { this.cachedKeys = cachedKeys; }

    public LocalDateTime getCheckedAt() { return checkedAt; }
    public void setCheckedAt(LocalDateTime checkedAt) { this.checkedAt = checkedAt; }
}

