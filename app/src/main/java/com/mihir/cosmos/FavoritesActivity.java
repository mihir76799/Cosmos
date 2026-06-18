package com.mihir.cosmos;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Favorites");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FavoritesManager favoritesManager = new FavoritesManager(this);
        List<String> favoriteTitles = new ArrayList<>(favoritesManager.getFavorites());

        RecyclerView recyclerView = findViewById(R.id.favoritesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (favoriteTitles.isEmpty()) {
            Toast.makeText(this, "No favorites yet! Heart some events.", Toast.LENGTH_LONG).show();
        } else {
            FavoritesAdapter adapter = new FavoritesAdapter(favoriteTitles);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}