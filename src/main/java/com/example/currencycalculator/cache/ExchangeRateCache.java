package com.example.currencycalculator.cache;

import java.util.Map;

public class ExchangeRateCache {
    private Map<String, Double> cachedRates;
    private long cacheTimestamp;
    private final long cacheDurationMs;

    public ExchangeRateCache(long cacheDurationMs) {
        this.cacheDurationMs = cacheDurationMs;
    }

    public boolean isCacheValid() {
        return cachedRates != null && (System.currentTimeMillis() - cacheTimestamp) < cacheDurationMs;
    }

    public Map<String, Double> getCachedRates() {
        return cachedRates;
    }

    public void updateCache(Map<String, Double> rates) {
        this.cachedRates = rates;
        this.cacheTimestamp = System.currentTimeMillis();
    }
}

