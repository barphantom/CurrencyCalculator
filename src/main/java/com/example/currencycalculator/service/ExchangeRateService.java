package com.example.currencycalculator.service;

import com.example.currencycalculator.api.OpenExchangeRatesClient;
import com.example.currencycalculator.cache.ExchangeRateCache;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class ExchangeRateService {
    private final OpenExchangeRatesClient client;
    private final ExchangeRateCache cache;

    public ExchangeRateService(OpenExchangeRatesClient client, ExchangeRateCache cache) {
        this.client = client;
        this.cache = cache;
    }

    public CompletableFuture<Map<String, Double>> getLatestRates() {
        if (cache.isCacheValid()) {
            return CompletableFuture.completedFuture(cache.getCachedRates());
        }

        return client.getLatestRates()
                .thenApply(rates -> {
                    cache.updateCache(rates);
                    return rates;
                });
    }


    public CompletableFuture<Double> getExchangeRate(String from, String to) {
        if (from.equals(to)) {
            return CompletableFuture.completedFuture(1.0);
        }

        return getLatestRates().thenApply(rates -> {
            Double usdToFrom = rates.get(from);
            Double usdToTo = rates.get(to);

            if (usdToFrom == null || usdToTo == null) {
                throw new IllegalArgumentException("Nieobsługiwana waluta: " + from + " lub " + to);
            }

            return usdToTo / usdToFrom;
        });
    }

    public CompletableFuture<Map<String, Double>> getHistoricalRatesToPLN(String baseCurrency, List<String> dates) {
        List<CompletableFuture<Map.Entry<String, Double>>> futures = dates.stream()
                .map(date -> client.getHistoricalRates(date)
                        .thenApply(rates -> {
                            double baseToUSD = rates.get(baseCurrency);
                            double plnToUSD = rates.get("PLN");
                            double baseToPLN = plnToUSD / baseToUSD;
                            return Map.entry(date, baseToPLN);
                        })
                )
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    Map<String, Double> result = new LinkedHashMap<>();
                    for (CompletableFuture<Map.Entry<String, Double>> future : futures) {
                        Map.Entry<String, Double> entry = future.join(); // join – bez checked exception
                        result.put(entry.getKey(), entry.getValue());
                    }
                    return result;
                });
    }

}
