package com.example.currencycalculator.model;

public class CurrencyRate {
    private final String currency;
    private final Double valueInPln;

    public CurrencyRate(String currency, Double valueInPln) {
        this.currency = currency;
        this.valueInPln = valueInPln;
    }

    public String getCurrency() { return currency; }
    public Double getValueInPln() { return valueInPln; }
}

