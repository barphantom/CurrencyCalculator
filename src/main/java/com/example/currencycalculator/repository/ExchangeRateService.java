package com.example.currencycalculator.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class ExchangeRateService {
    private final String apiKey;
    private Map<String, Double> cachedRates = null;
    private long cacheTimestamp = 0;
    private static final long CACHE_DURATION_MS = 5 * 60 * 1000; // 5 minut

    public ExchangeRateService() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                apiKey = props.getProperty("api.key");
            } else {
                throw new RuntimeException("Nie znaleziono pliku config.properties");
            }
        } catch (IOException e) {
            throw new RuntimeException("Nie można załadować API key", e);
        }
    }


    public Map<String, Double> getLatestRates() throws IOException {
        String urlStr = "https://openexchangerates.org/api/latest.json?app_id=" + apiKey;
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        try (InputStream in = con.getInputStream(); Scanner scanner = new Scanner(in)) {
            String json = scanner.useDelimiter("\\A").next();
            JSONObject obj = new JSONObject(json);
            JSONObject ratesObj = obj.getJSONObject("rates");

            Map<String, Double> rates = new HashMap<>();
            for (String key : ratesObj.keySet()) {
                rates.put(key, ratesObj.getDouble(key));
            }

            return rates;
        }
    }

    public Map<String, Double> getHistoricalRatesToPLN(String baseCurrency, List<String> dates) throws IOException {
        Map<String, Double> results = new LinkedHashMap<>();
        for (String date : dates) {
            String url = String.format("https://openexchangerates.org/api/historical/%s.json?app_id=%s", date, apiKey);
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String response = reader.lines().collect(Collectors.joining());
                JSONObject json = new JSONObject(response);
                JSONObject rates = json.getJSONObject("rates");

                double baseToUSD = rates.getDouble(baseCurrency);  // Np. EUR -> USD
                double plnToUSD = rates.getDouble("PLN");          // PLN -> USD

                double baseToPLN = plnToUSD / baseToUSD;           // EUR -> PLN (bo: (PLN/USD) / (EUR/USD))

                results.put(date, baseToPLN);
            }
        }
        return results;
    }

    public double getExchangeRate(String from, String to) throws IOException {
        if (from.equals(to)) return 1.0;

        Map<String, Double> rates = fetchLatestRates(); // z cache lub z API
        Double usdToFrom = rates.get(from);
        Double usdToTo = rates.get(to);

        if (usdToFrom == null || usdToTo == null) {
            throw new IllegalArgumentException("Nieobsługiwana waluta: " + from + " lub " + to);
        }

        return usdToTo / usdToFrom;
    }

    private Map<String, Double> fetchLatestRates() throws IOException {
        long now = System.currentTimeMillis();

        // Jeśli mamy cache i nie jest przeterminowany, zwracamy go
        if (cachedRates != null && (now - cacheTimestamp) < CACHE_DURATION_MS) {
            return cachedRates;
        }

        // Inaczej pobieramy nowe dane
        String urlStr = "https://openexchangerates.org/api/latest.json?app_id=" + apiKey;
        JSONObject json = getJsonObject(urlStr);
        JSONObject ratesJson = json.getJSONObject("rates");

        Map<String, Double> rates = new HashMap<>();
        for (String key : ratesJson.keySet()) {
            rates.put(key, ratesJson.getDouble(key));
        }

        // Zapisz do cache
        cachedRates = rates;
        cacheTimestamp = now;

        return rates;
    }

    private static JSONObject getJsonObject(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject json = new JSONObject(response.toString());
        return json;
    }

}

