package com.example.currencycalculator.api;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OpenExchangeRatesClient {
    private final String apiKey;
    private final HttpClient httpClient;

    public OpenExchangeRatesClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient(); // można też ustawić niestandardowy
    }

    public CompletableFuture<Map<String, Double>> getLatestRates() {
        String urlStr = "https://openexchangerates.org/api/latest.json?app_id=" + apiKey;
        return fetchRatesFromUrlAsync(urlStr);
    }

    public CompletableFuture<Map<String, Double>> getHistoricalRates(String date) {
        String urlStr = String.format("https://openexchangerates.org/api/historical/%s.json?app_id=%s", date, apiKey);
        return fetchRatesFromUrlAsync(urlStr);
    }

    private CompletableFuture<Map<String, Double>> fetchRatesFromUrlAsync(String urlStr) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlStr))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(JSONObject::new)
                .thenApply(json -> {
                    JSONObject ratesJson = json.getJSONObject("rates");
                    Map<String, Double> rates = new HashMap<>();
                    for (String key : ratesJson.keySet()) {
                        rates.put(key, ratesJson.getDouble(key));
                    }
                    return rates;
                });
    }
}
