package com.example.currencycalculator.service;

import com.example.currencycalculator.api.OpenExchangeRatesClient;
import com.example.currencycalculator.cache.ExchangeRateCache;
import com.example.currencycalculator.config.ConfigLoader;
import com.example.currencycalculator.model.CurrencyRate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CurrencyService {
    private final ExchangeRateService exchangeService = new ExchangeRateService(
            new OpenExchangeRatesClient(ConfigLoader.loadApiKey()),
            new ExchangeRateCache(5 * 60 * 1000)
    );

    public CompletableFuture<List<CurrencyRate>> getRates(List<String> currencies) {
        return exchangeService.getLatestRates().thenApply(rates -> {
            double usdToPln = rates.get("PLN");

            return currencies.stream()
                    .filter(c -> !c.equals("PLN") && rates.containsKey(c))
                    .map(c -> new CurrencyRate(c, usdToPln / rates.get(c)))
                    .toList();
        });
    }


    public CompletableFuture<Double> convert(double amount, String from, String to) {
        return exchangeService.getExchangeRate(from, to)
                .thenApply(rate -> amount * rate);
    }

}

