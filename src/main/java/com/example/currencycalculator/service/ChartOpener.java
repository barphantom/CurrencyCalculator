package com.example.currencycalculator.service;

import com.example.currencycalculator.api.OpenExchangeRatesClient;
import com.example.currencycalculator.cache.ExchangeRateCache;
import com.example.currencycalculator.config.ConfigLoader;
import com.example.currencycalculator.controller.ChartController;
import com.example.currencycalculator.model.CurrencyRate;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.stage.Stage;

import java.io.IOException;

public class ChartOpener {
    public TableCell<CurrencyRate, Void> createChartButtonCell() {
        return new TableCell<>() {
            private final Button button = new Button("Wykres");

            {
                button.setOnAction(e -> {
                    CurrencyRate rate = getTableView().getItems().get(getIndex());
                    openChartWindow(rate.getCurrency());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        };
    }

    private void openChartWindow(String currencyCode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/currencycalculator/chart-view.fxml"));
            Parent root = loader.load();
            ChartController controller = loader.getController();

            // INICJALIZACJA zależności
            String apiKey = ConfigLoader.loadApiKey();
            ExchangeRateCache cache = new ExchangeRateCache(5 * 60 * 1000); // 5 minut
            OpenExchangeRatesClient client = new OpenExchangeRatesClient(apiKey);
            ExchangeRateService apiService = new ExchangeRateService(client, cache);
            controller.setExchangeRateService(apiService);

            controller.loadChartData(currencyCode);

            Stage stage = new Stage();
            stage.setTitle("Wykres - " + currencyCode + "/PLN");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Błąd otwierania wykresu").show();
        }
    }
}

