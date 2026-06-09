package com.mihir.cosmos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.BuildConfig;
import com.bumptech.glide.Glide;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private static final String API_KEY = BuildConfig.ANTHROPIC_API_KEY;
    private static final String CLAUDE_URL = "https://api.anthropic.com/v1/messages";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String title       = getIntent().getStringExtra("title");
        String date        = getIntent().getStringExtra("date");
        String description = getIntent().getStringExtra("description");
        String imageUrl    = getIntent().getStringExtra("imageUrl");

        TextView titleView = findViewById(R.id.detail_title);
        TextView dateView  = findViewById(R.id.detail_date);
        TextView descView  = findViewById(R.id.detail_description);
        TextView aiView    = findViewById(R.id.ai_response);
        Button aiButton    = findViewById(R.id.btn_ask_ai);
        ImageView imageView = findViewById(R.id.detail_image);

        titleView.setText(title);
        dateView.setText(date);
        descView.setText(description);

        Glide.with(this).load(imageUrl).into(imageView);

        aiButton.setOnClickListener(v -> {
            aiButton.setText("Loading...");
            aiButton.setEnabled(false);
            askCosmosAI(title, description, aiView, aiButton);
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
}
