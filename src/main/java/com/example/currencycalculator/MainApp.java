package com.example.currencycalculator;

import com.example.currencycalculator.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    private MainController mainController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("main-view.fxml"));
        Parent root = loader.load();

        mainController = loader.getController();

        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Kalkulator walut!");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        if (mainController != null) {
            mainController.stopScheduler();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}