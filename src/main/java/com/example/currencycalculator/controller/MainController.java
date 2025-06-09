package com.example.currencycalculator.controller;

import com.example.currencycalculator.cache.ExchangeRateCache;
import com.example.currencycalculator.config.ConfigLoader;
import com.example.currencycalculator.model.CurrencyRate;
import com.example.currencycalculator.service.ExchangeRateService;
import com.example.currencycalculator.repository.FavoriteCurrencyRepository;
import com.example.currencycalculator.api.OpenExchangeRatesClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


//public class MainController {
//    @FXML private Label welcomeText;
//    @FXML private ComboBox<String> fromCurrencyBox;
//    @FXML private ComboBox<String> toCurrencyBox;
//    @FXML private TextField amountField;
//    @FXML private Label resultLabel;
//
//    String apiKey = ConfigLoader.loadApiKey();
//    ExchangeRateCache cache = new ExchangeRateCache(5 * 60 * 1000); // 5 min
//    OpenExchangeRatesClient client = new OpenExchangeRatesClient(apiKey);
//    ExchangeRateService apiService = new ExchangeRateService(client, cache);
////    private ExchangeRateService apiService = new ExchangeRateService();
//    private FavoriteCurrencyRepository dbRepo = new FavoriteCurrencyRepository();
//    private ScheduledExecutorService scheduler;
//
//    @FXML private TableView<CurrencyRate> ratesTable;
//    @FXML private TableColumn<CurrencyRate, String> currencyColumn;
//    @FXML private TableColumn<CurrencyRate, Double> rateColumn;
//    @FXML private TableColumn<CurrencyRate, Void> chartColumn;
//
////    @FXML private Label favoritesLabel;
//    @FXML private VBox favoritesBox;
//
//    private final List<String> favoriteCurrencies = new ArrayList<>();
//    private final List<String> shownCurrencies = List.of("USD", "EUR", "GBP", "CHF", "JPY", "NOK", "CAD", "AUD", "CZK", "SEK");
//
//    @FXML
//    public void initialize() {
//        fromCurrencyBox.getItems().addAll("USD", "EUR", "PLN", "GBP");
//        toCurrencyBox.getItems().addAll("USD", "EUR", "PLN", "GBP");
//        currencyColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCurrency()));
//        rateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getValueInPln()));
//        addChartButtons();
//
//        favoriteCurrencies.addAll(dbRepo.getFavorites());
//
//        startAutoUpdate();
//        updateRates();
//    }
//
//    private void updateRates() {
//        try {
//            Map<String, Double> rates = apiService.getLatestRates();
//            double usdToPln = rates.get("PLN");
//
//            List<CurrencyRate> currencyRates = new ArrayList<>();
//
//            for (String currency : shownCurrencies) {
//                if (!currency.equals("PLN") && rates.containsKey(currency)) {
//                    double rateToUsd = rates.get(currency);
//                    double rateToPln = usdToPln / rateToUsd;
//                    currencyRates.add(new CurrencyRate(currency, rateToPln));
//                }
//            }
//
//            ratesTable.setItems(FXCollections.observableList(currencyRates));
//            updateFavoritesDisplay(currencyRates);
//
//        } catch (IOException e) {
//            new Alert(Alert.AlertType.ERROR, "Błąd pobierania danych z API").show();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void startAutoUpdate() {
//        scheduler = Executors.newSingleThreadScheduledExecutor();
//        System.out.println("Scheduler uruchomiony");
//
//        scheduler.scheduleAtFixedRate(() -> {
//            try {
//                System.out.println("Aktuazlizuję kurs walut: " + LocalDateTime.now());
//                Platform.runLater(this::updateRates);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }, 0, 10, TimeUnit.MINUTES); // co 10 minut
//    }
//
//    public void stopScheduler() {
//        if (scheduler != null && !scheduler.isShutdown()) {
//            scheduler.shutdown();
//            System.out.println("Zamykam scheduler!");
//        }
//    }
//
//    private void updateFavoritesDisplay(List<CurrencyRate> currencyRates) {
//        favoritesBox.getChildren().clear();
//
//        for (CurrencyRate cr : currencyRates) {
//            if (favoriteCurrencies.contains(cr.getCurrency())) {
//                String currency = cr.getCurrency();
//                double value = cr.getValueInPln();
//
//                Label label = new Label(String.format("%s: %.2f PLN", currency, value));
//                Button removeBtn = new Button("Usuń");
//                removeBtn.setOnAction(e -> {
//                    favoriteCurrencies.remove(currency);
//                    dbRepo.removeFavorite(currency);
//                    updateRates();
//                });
//
//                HBox itemBox = new HBox(10, label, removeBtn);
//                favoritesBox.getChildren().add(itemBox);
//            }
//        }
//    }
//
//
//    @FXML
//    public void addFavorite() {
//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setHeaderText("Dodaj ulubioną walutę (np. USD, EUR):");
//        Optional<String> result = dialog.showAndWait();
//
//        result.ifPresent(currency -> {
//            String upper = currency.toUpperCase();
//            if (!favoriteCurrencies.contains(upper)) {
//                favoriteCurrencies.add(upper);
//                dbRepo.addFavorite(upper);
//                updateRates();
//            }
//        });
//    }
//
//    private void addChartButtons() {
//        chartColumn.setCellFactory(col -> new TableCell<>() {
//            private final Button button = new Button("Wykres");
//
//            {
//                button.setOnAction(e -> {
//                    CurrencyRate rate = getTableView().getItems().get(getIndex());
//                    try {
//                        openChartWindow(rate.getCurrency());
//                    } catch (IOException ex) {
//                        new Alert(Alert.AlertType.ERROR, "Błąd otwierania wykresu").show();
//                    }
//                });
//            }
//
//            @Override
//            protected void updateItem(Void item, boolean empty) {
//                super.updateItem(item, empty);
//                if (empty) {
//                    setGraphic(null);
//                } else {
//                    setGraphic(button);
//                }
//            }
//        });
//    }
//
//    private void openChartWindow(String currencyCode) throws IOException {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/currencycalculator/chart-view.fxml"));
//        Parent root = loader.load();
//
//        ChartController controller = loader.getController();
//        controller.loadChartData(currencyCode);
//
//        Stage stage = new Stage();
//        stage.setTitle("Wykres - " + currencyCode + "/PLN");
//        stage.setScene(new Scene(root));
//        stage.show();
//    }
//
//    public void onConvert() {
//        try {
//            double amount = Double.parseDouble(amountField.getText());
//            String from = fromCurrencyBox.getValue();
//            String to = toCurrencyBox.getValue();
//            double rate = apiService.getExchangeRate(from, to);
//            double result = amount * rate;
//            resultLabel.setText(String.format("%.2f %s = %.2f %s", amount, from, result, to));
//
//        } catch (Exception e) {
//            resultLabel.setText("Wystąpił błąd: " + e.getMessage());
//        }
//    }
//
//}



import com.example.currencycalculator.model.CurrencyRate;
import com.example.currencycalculator.service.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML private ComboBox<String> fromCurrencyBox;
    @FXML private ComboBox<String> toCurrencyBox;
    @FXML private TextField amountField;
    @FXML private Label resultLabel;

    @FXML private TableView<CurrencyRate> ratesTable;
    @FXML private TableColumn<CurrencyRate, String> currencyColumn;
    @FXML private TableColumn<CurrencyRate, Double> rateColumn;
    @FXML private TableColumn<CurrencyRate, Void> chartColumn;
    @FXML private VBox favoritesBox;

    private final CurrencyService currencyService = new CurrencyService();
    private final FavoriteCurrencyService favoriteService = new FavoriteCurrencyService();
    private final ChartOpener chartOpener = new ChartOpener();
    private final RatesUpdater updater = new RatesUpdater(this::updateRates);

    private final List<String> shownCurrencies = List.of("USD", "EUR", "GBP", "CHF", "JPY", "NOK", "CAD", "AUD", "CZK", "SEK");

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
        Platform.runLater(() -> {
            try {
                List<CurrencyRate> rates = currencyService.getRates(shownCurrencies);
                ratesTable.setItems(FXCollections.observableList(rates));
                updateFavoritesDisplay(rates);
            } catch (Exception e) {
                showError("Błąd pobierania danych z API");
            }
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
            double result = currencyService.convert(amount, from, to);
            resultLabel.setText(String.format("%.2f %s = %.2f %s", amount, from, result, to));
        } catch (Exception e) {
            resultLabel.setText("Błąd: " + e.getMessage());
        }
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).show();
    }
}
