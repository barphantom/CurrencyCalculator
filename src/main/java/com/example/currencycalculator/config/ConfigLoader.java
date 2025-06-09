package com.example.currencycalculator.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    public static String loadApiKey() {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) throw new RuntimeException("Nie znaleziono pliku config.properties");

            Properties props = new Properties();
            props.load(input);
            return props.getProperty("api.key");
        } catch (IOException e) {
            throw new RuntimeException("Nie można załadować API key", e);
        }
    }
}
