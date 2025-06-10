package com.example.currencycalculator.controller;

import com.example.currencycalculator.model.CurrencyRate;
import com.example.currencycalculator.service.ChartOpener;
import com.example.currencycalculator.service.CurrencyService;
import com.example.currencycalculator.service.FavoriteCurrencyService;
import com.example.currencycalculator.service.RatesUpdater;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class MainController {

    private final CurrencyService currencyService = new CurrencyService();
    private final FavoriteCurrencyService favoriteService = new FavoriteCurrencyService();
    private final ChartOpener chartOpener = new ChartOpener();
    private final List<String> shownCurrencies = List.of("USD", "EUR", "GBP", "CHF", "JPY", "NOK", "CAD", "AUD", "CZK", "SEK");
    @FXML
    private ComboBox<String> fromCurrencyBox;
    @FXML
    private ComboBox<String> toCurrencyBox;
    @FXML
    private TextField amountField;
    @FXML
    private Label resultLabel;
    @FXML
    private TableView<CurrencyRate> ratesTable;
    @FXML
    private TableColumn<CurrencyRate, String> currencyColumn;
    @FXML
    private TableColumn<CurrencyRate, Double> rateColumn;
    @FXML
    private TableColumn<CurrencyRate, Void> chartColumn;
    @FXML
    private VBox favoritesBox;
    private final RatesUpdater updater = new RatesUpdater(this::updateRates);

    @FXML
    public void initialize() {
        fromCurrencyBox.getItems().addAll("USD", "EUR", "PLN", "GBP");
        toCurrencyBox.getItems().addAll("USD", "EUR", "PLN", "GBP");

        currencyColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCurrency()));
        rateColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getValueInPln()));

        chartColumn.setCellFactory(col -> chartOpener.createChartButtonCell());

        favoriteService.loadFavorites();
        updater.startScheduler();
        updateRates();
    }

    public void stop() {
        updater.stopScheduler();
    }

    public void updateRates() {
        currencyService.getRates(shownCurrencies)
                .thenAccept(rates -> {
                    Platform.runLater(() -> {
                        ratesTable.setItems(FXCollections.observableList(rates));
                        updateFavoritesDisplay(rates);
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        showError("Błąd pobierania danych z API: " + ex.getMessage());
                    });
                    return null;
                });
    }


    private void updateFavoritesDisplay(List<CurrencyRate> currencyRates) {
        favoritesBox.getChildren().clear();
        for (CurrencyRate cr : currencyRates) {
            if (favoriteService.isFavorite(cr.getCurrency())) {
                Label label = new Label(String.format("%s: %.2f PLN", cr.getCurrency(), cr.getValueInPln()));
                Button removeBtn = new Button("Usuń");
                removeBtn.setOnAction(e -> {
                    favoriteService.removeFavorite(cr.getCurrency());
                    updateRates();
                });
                favoritesBox.getChildren().add(new HBox(10, label, removeBtn));
            }
        }
    }

    @FXML
    public void addFavorite() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Dodaj ulubioną walutę (np. USD, EUR):");
        Optional<String> result = dialog.showAndWait();
        result.map(String::toUpperCase).ifPresent(currency -> {
            favoriteService.addFavorite(currency);
            updateRates();
        });
    }

    @FXML
    public void onConvert() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String from = fromCurrencyBox.getValue();
            String to = toCurrencyBox.getValue();

            currencyService.convert(amount, from, to)
                    .thenAccept(result -> {
                        Platform.runLater(() -> {
                            resultLabel.setText(String.format("%.2f %s = %.2f %s", amount, from, result, to));
                        });
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> {
                            resultLabel.setText("Błąd: " + ex.getMessage());
                        });
                        return null;
                    });

        } catch (NumberFormatException e) {
            resultLabel.setText("Błąd: niepoprawna kwota");
        } catch (Exception e) {
            resultLabel.setText("Błąd: " + e.getMessage());
        }
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).show();
    }
}
