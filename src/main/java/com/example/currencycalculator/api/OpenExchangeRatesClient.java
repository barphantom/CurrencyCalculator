package com.example.currencycalculator.api;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class OpenExchangeRatesClient {
    private final String apiKey;

    public OpenExchangeRatesClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public Map<String, Double> getLatestRates() throws Exception {
        String urlStr = "https://openexchangerates.org/api/latest.json?app_id=" + apiKey;
        return fetchRatesFromUrl(urlStr);
    }

    public Map<String, Double> getHistoricalRates(String date) throws Exception {
        String urlStr = String.format("https://openexchangerates.org/api/historical/%s.json?app_id=%s", date, apiKey);
        return fetchRatesFromUrl(urlStr);
    }

    private Map<String, Double> fetchRatesFromUrl(String urlStr) throws Exception {
        JSONObject json = getJsonObject(urlStr);
        JSONObject ratesJson = json.getJSONObject("rates");

        Map<String, Double> rates = new HashMap<>();
        for (String key : ratesJson.keySet()) {
            rates.put(key, ratesJson.getDouble(key));
        }

        return rates;
    }

    private JSONObject getJsonObject(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return new JSONObject(response.toString());
        }
    }
}
