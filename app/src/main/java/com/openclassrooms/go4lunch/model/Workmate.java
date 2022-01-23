package com.openclassrooms.go4lunch.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

public class Workmate {
    private String name;
    private double latitude;
    private double longitude;
    private String email;
    private boolean hasJoined;

    public Workmate() {}

    public Workmate(String email, String name, LatLng restaurant) {
        this.email = email;
        this.name = name;
        hasJoined = (restaurant != null);
        if (hasJoined) {
            this.latitude = restaurant.latitude;
            this.longitude = restaurant.longitude;
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean getHasJoined() { return hasJoined; }
    public void setHasJoined(boolean hasJoined) { this.hasJoined = hasJoined; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
