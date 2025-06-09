package com.example.currencycalculator.service;

import java.util.*;

import com.example.currencycalculator.api.OpenExchangeRatesClient;
import com.example.currencycalculator.cache.ExchangeRateCache;


public class ExchangeRateService {
    private final OpenExchangeRatesClient client;
    private final ExchangeRateCache cache;

    public ExchangeRateService(OpenExchangeRatesClient client, ExchangeRateCache cache) {
        this.client = client;
        this.cache = cache;
    }

    public Map<String, Double> getLatestRates() throws Exception {
        if (cache.isCacheValid()) {
            return cache.getCachedRates();
        }

        Map<String, Double> rates = client.getLatestRates();
        cache.updateCache(rates);
        return rates;
    }

    public double getExchangeRate(String from, String to) throws Exception {
        if (from.equals(to)) return 1.0;

        Map<String, Double> rates = getLatestRates();
        Double usdToFrom = rates.get(from);
        Double usdToTo = rates.get(to);

        if (usdToFrom == null || usdToTo == null) {
            throw new IllegalArgumentException("Nieobsługiwana waluta: " + from + " lub " + to);
        }

        return usdToTo / usdToFrom;
    }

    public Map<String, Double> getHistoricalRatesToPLN(String baseCurrency, List<String> dates) throws Exception {
        Map<String, Double> results = new LinkedHashMap<>();
        for (String date : dates) {
            Map<String, Double> rates = client.getHistoricalRates(date);
            double baseToUSD = rates.get(baseCurrency);
            double plnToUSD = rates.get("PLN");
            double baseToPLN = plnToUSD / baseToUSD;
            results.put(date, baseToPLN);
        }
        return results;
    }
}
