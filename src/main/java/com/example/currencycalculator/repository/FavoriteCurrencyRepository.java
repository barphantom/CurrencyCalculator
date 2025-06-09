package com.example.currencycalculator.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoriteCurrencyRepository {

    private final String url = "jdbc:sqlite:favorites.db";

    public FavoriteCurrencyRepository() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS favorites (currency TEXT PRIMARY KEY)";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addFavorite(String currency) {
        String sql = "INSERT OR IGNORE INTO favorites(currency) VALUES(?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, currency);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFavorite(String currency) {
        String sql = "DELETE FROM favorites WHERE currency = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, currency);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getFavorites() {
        List<String> favorites = new ArrayList<>();
        String sql = "SELECT currency FROM favorites";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                favorites.add(rs.getString("currency"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favorites;
    }
}

