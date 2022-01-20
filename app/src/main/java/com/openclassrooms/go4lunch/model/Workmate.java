package com.openclassrooms.go4lunch.model;

import com.google.android.gms.maps.model.LatLng;

public class Workmate {
    private String name;
    private LatLng restaurant;
    private String email;

    public Workmate(String email, String name, LatLng restaurant) {
        this.email = email;
        this.name = name;
        this.restaurant = restaurant;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LatLng getRestaurant() { return restaurant; }
    public void setRestaurant(LatLng restaurant) { this.restaurant = restaurant; }

}
