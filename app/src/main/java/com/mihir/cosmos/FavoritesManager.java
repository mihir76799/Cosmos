package com.mihir.cosmos;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class FavoritesManager {

    private static final String PREFS_NAME = "cosmos_favorites";
    private static final String KEY_FAVORITES = "favorite_titles";

    private SharedPreferences prefs;

    public FavoritesManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void addFavorite(String title) {
        Set<String> favorites = getFavorites();
        favorites.add(title);
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }

    public void removeFavorite(String title) {
        Set<String> favorites = getFavorites();
        favorites.remove(title);
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }

    public boolean isFavorite(String title) {
        return getFavorites().contains(title);
    }

    public Set<String> getFavorites() {
        // putStringSet stores a reference, so we wrap in new HashSet to avoid mutation bugs
        return new HashSet<>(prefs.getStringSet(KEY_FAVORITES, new HashSet<>()));
    }
}
