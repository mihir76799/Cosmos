package com.mihir.cosmos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.mihir.cosmos.BuildConfig;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;



public class DetailActivity extends AppCompatActivity {

    private static final String API_KEY = BuildConfig.ANTHROPIC_API_KEY;
    private static final String CLAUDE_URL = "https://api.anthropic.com/v1/messages";

    private String currentImageUrl;
    private String currentTitle;
    private String currentAiFact;
    private FavoritesManager favoritesManager;
    private FloatingActionButton fabFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        currentTitle       = getIntent().getStringExtra("title");
        String date        = getIntent().getStringExtra("date");
        String description = getIntent().getStringExtra("description");
        currentImageUrl    = getIntent().getStringExtra("imageUrl");

        TextView titleView = findViewById(R.id.detail_title);
        TextView dateView  = findViewById(R.id.detail_date);
        TextView descView  = findViewById(R.id.detail_description);
        TextView aiView    = findViewById(R.id.ai_response);
        Button aiButton    = findViewById(R.id.btn_ask_ai);
        ImageView imageView = findViewById(R.id.detail_image);
        FloatingActionButton fabShare = findViewById(R.id.fabShare);

        favoritesManager = new FavoritesManager(this);
        fabFavorite = findViewById(R.id.fabFavorite);
        updateFabIcon();

        fabFavorite.setOnClickListener(v -> {
            if (favoritesManager.isFavorite(currentTitle)) {
                favoritesManager.removeFavorite(currentTitle);
            } else {
                favoritesManager.addFavorite(currentTitle);
            }
            updateFabIcon();
        });

        titleView.setText(currentTitle);
        dateView.setText(date);
        descView.setText(description);

        Glide.with(this).load(currentImageUrl).into(imageView);

        aiButton.setOnClickListener(v -> {
            aiButton.setText("Loading...");
            aiButton.setEnabled(false);
            askCosmosAI(currentTitle, description, aiView, aiButton);
        });



        // FAB share button
        fabShare.setOnClickListener(v -> shareSpaceImage());

    }

    private void shareSpaceImage() {
        Toast.makeText(this, "Preparing image...", Toast.LENGTH_SHORT).show();

        Glide.with(this)
                .asBitmap()
                .load(currentImageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        try {
                            // Save bitmap to app cache
                            File cachePath = new File(getCacheDir(), "images");
                            cachePath.mkdirs();
                            File imageFile = new File(cachePath, "cosmos_share.png");
                            FileOutputStream stream = new FileOutputStream(imageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            stream.close();

                            // Convert file to secure URI via FileProvider
                            Uri imageUri = FileProvider.getUriForFile(
                                    DetailActivity.this,
                                    getPackageName() + ".fileprovider",
                                    imageFile
                            );

                            // Build share text
                            String shareText = "🌌 " + currentTitle;
                            if (currentAiFact != null && !currentAiFact.isEmpty()) {
                                shareText += "\n\n✨ " + currentAiFact;
                            }
                            shareText += "\n\nShared via Cosmos App";

                            // Fire the share intent
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            startActivity(Intent.createChooser(shareIntent, "Share via"));

                        } catch (Exception e) {
                            Toast.makeText(DetailActivity.this, "Could not share image.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void askCosmosAI(String title, String description, TextView aiView, Button aiButton) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String prompt = "Give me one fascinating, lesser-known fact about this space event or topic in 2-3 sentences. Be specific and exciting. Topic: " + title + ". Context: " + description;

        try {
            JSONObject body = new JSONObject();
            body.put("model", "claude-sonnet-4-20250514");
            body.put("max_tokens", 300);

            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.put(message);
            body.put("messages", messages);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    CLAUDE_URL,
                    body,
                    response -> {
                        try {
                            String aiText = response
                                    .getJSONArray("content")
                                    .getJSONObject(0)
                                    .getString("text");
                            currentAiFact = aiText; // save for sharing
                            aiView.setText("✦ " + aiText);
                            aiView.setVisibility(View.VISIBLE);
                            aiButton.setText("✦ Ask Cosmos AI");
                            aiButton.setEnabled(true);
                        } catch (Exception e) {
                            aiView.setText("Something went wrong. Try again.");
                            aiView.setVisibility(View.VISIBLE);
                            aiButton.setText("✦ Ask Cosmos AI");
                            aiButton.setEnabled(true);
                        }
                    },
                    error -> {
                        aiView.setText("Could not reach Cosmos AI. Check your connection.");
                        aiView.setVisibility(View.VISIBLE);
                        aiButton.setText("✦ Ask Cosmos AI");
                        aiButton.setEnabled(true);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("x-api-key", API_KEY);
                    headers.put("anthropic-version", "2023-06-01");
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            queue.add(request);

        } catch (Exception e) {
            aiView.setText("Error building request.");
            aiView.setVisibility(View.VISIBLE);
        }
    }
    private void updateFabIcon() {
        if (favoritesManager.isFavorite(currentTitle)) {
            fabFavorite.setImageResource(R.drawable.ic_favorite);
            fabFavorite.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            getResources().getColor(R.color.space_gold, getTheme())
                    )
            );
        } else {
            fabFavorite.setImageResource(R.drawable.ic_favorite_border);
            fabFavorite.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            getResources().getColor(R.color.space_card, getTheme())
                    )
            );
        }
    }
}
