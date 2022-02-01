package com.openclassrooms.go4lunch.model;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class RestaurantLike {
    private String name;
    private String id;
    private int like;

    // Mandatory for FireStore
    public RestaurantLike() {}

    public RestaurantLike(String id, String name, int like) {
        this.id = id;
        this.name = name;
        this.like = like;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getLike() { return like; }
    public void setLike(int like) { this.like = like; }
}
