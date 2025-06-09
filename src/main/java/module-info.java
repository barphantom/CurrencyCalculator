module com.example.currencycalculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;
    requires java.sql;


    opens com.example.currencycalculator to javafx.fxml;
    exports com.example.currencycalculator;
    exports com.example.currencycalculator.controller;
    opens com.example.currencycalculator.controller to javafx.fxml;
}