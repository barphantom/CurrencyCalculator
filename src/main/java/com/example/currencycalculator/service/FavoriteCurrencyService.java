package com.example.currencycalculator.service;

import com.example.currencycalculator.repository.FavoriteCurrencyRepository;

import java.util.ArrayList;
import java.util.List;

public class FavoriteCurrencyService {
    private final FavoriteCurrencyRepository repo = new FavoriteCurrencyRepository();
    private final List<String> favorites = new ArrayList<>();

    public void loadFavorites() {
        favorites.clear();
        favorites.addAll(repo.getFavorites());
    }

    public void addFavorite(String currency) {
        if (!favorites.contains(currency)) {
            favorites.add(currency);
            repo.addFavorite(currency);
        }
    }

    public void removeFavorite(String currency) {
        favorites.remove(currency);
        repo.removeFavorite(currency);
    }

    public boolean isFavorite(String currency) {
        return favorites.contains(currency);
    }
}

