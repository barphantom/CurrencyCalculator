package com.example.currencycalculator.controller;

import com.example.currencycalculator.cache.ExchangeRateCache;
import com.example.currencycalculator.config.ConfigLoader;
import com.example.currencycalculator.service.ExchangeRateService;
import com.example.currencycalculator.api.OpenExchangeRatesClient;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChartController {
    @FXML private LineChart<String, Number> lineChart;
    @FXML private AnchorPane rootPane;

    private ExchangeRateService apiService;


    public void loadChartData(String currency) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<String> dates = IntStream.rangeClosed(0, 5)
                .mapToObj(i -> today.minusMonths(i).withDayOfMonth(1).format(formatter))
                .collect(Collectors.toList());

        try {
            Map<String, Double> rawData = apiService.getHistoricalRatesToPLN(currency, dates);

            Map<String, Double> sortedData = rawData.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            double min = sortedData.values().stream().min(Double::compareTo).orElse(0.0);
            double max = sortedData.values().stream().max(Double::compareTo).orElse(1.0);
            double margin = 0.1;

            double lowerBound = Math.floor(min - margin);
            double upperBound = Math.ceil(max + margin);

            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Data");

            NumberAxis yAxis = new NumberAxis(lowerBound, upperBound, (upperBound - lowerBound) / 5);
            yAxis.setLabel("Kurs");

            LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle(currency + "/PLN – ostatnie 6 miesięcy");
            lineChart.setLegendVisible(false);
            lineChart.setAnimated(false);
            lineChart.setPrefSize(600, 400);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(currency + "/PLN");

            for (Map.Entry<String, Double> entry : sortedData.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            lineChart.getData().add(series);

            rootPane.getChildren().clear();
            rootPane.getChildren().add(lineChart);
            AnchorPane.setTopAnchor(lineChart, 0.0);
            AnchorPane.setBottomAnchor(lineChart, 0.0);
            AnchorPane.setLeftAnchor(lineChart, 0.0);
            AnchorPane.setRightAnchor(lineChart, 0.0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setExchangeRateService(ExchangeRateService apiService) {
        this.apiService = apiService;
    }
}

