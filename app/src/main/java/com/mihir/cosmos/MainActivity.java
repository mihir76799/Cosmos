package com.mihir.cosmos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import java.util.ArrayList;
import java.util.List;
import com.mihir.cosmos.DetailActivity;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SpaceEventAdapter adapter;
    private List<SpaceEvent> events = new ArrayList<>();

    private static final String API_KEY = "ci9vNmtPQ5b0stthZYLjdV0agLkuAQqyOPqd68yZ";
    private static final String URL = "https://api.nasa.gov/planetary/apod?count=5&api_key=" + API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new SpaceEventAdapter(events, event -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("title", event.getTitle());
            intent.putExtra("date", event.getDate());
            intent.putExtra("description", event.getDescription());
            intent.putExtra("imageUrl", event.getImageUrl());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.d("COSMOS", "Calling API: " + URL);
        fetchSpaceEvents();

    }


    private void fetchSpaceEvents() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                response -> {
                    Log.d("COSMOS", "Got response, length: " + response.length());
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            org.json.JSONObject item = response.getJSONObject(i);
                            String title = item.getString("title");
                            String date = item.getString("date");
                            String description = item.getString("explanation");
                            String imageUrl = item.getString("url");
                            Log.d("COSMOS", "Event: " + title);
                            events.add(new SpaceEvent(title, date, description, imageUrl));
                        }
                        adapter.notifyDataSetChanged();
                        Log.d("COSMOS", "Adapter updated with " + events.size() + " events");
                    } catch (org.json.JSONException e) {
                        Log.e("COSMOS", "Parsing error: " + e.getMessage());
                    }
                },
                error -> Log.e("COSMOS", "API error: " + error.toString())
        ) {
            @Override
            public com.android.volley.RetryPolicy getRetryPolicy() {
                return new com.android.volley.DefaultRetryPolicy(
                        10000,
                        1,
                        1.0f
                );
            }
        };

        queue.add(request);
    }
}