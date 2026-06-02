package com.mihir.cosmos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);


        List<SpaceEvent> events = new ArrayList<>();
        events.add(new SpaceEvent("Perseid Meteor Shower", "Aug 12, 2025", "Best meteor shower of the year", ""));
        events.add(new SpaceEvent("SpaceX Starship Launch", "TBD", "Next gen rocket test flight", ""));
        events.add(new SpaceEvent("Lunar Eclipse", "Sep 7, 2025", "Total lunar eclipse visible from Asia", ""));
        events.add(new SpaceEvent("James Webb Image Drop", "Oct 2025", "New deep field images releasing", ""));

        SpaceEventAdapter adapter = new SpaceEventAdapter(events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

}