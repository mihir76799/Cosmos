package com.mihir.cosmos;

import java.io.Serializable;

public class SpaceEvent implements Serializable {

    private String title, date, description, imageUrl;

    public SpaceEvent(String title, String date, String description, String imageUrl) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
}
