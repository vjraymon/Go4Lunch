package com.openclassrooms.go4lunch.model;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class Workmate {
    private String name;
    private String email;
    private String photoUrl;
    private String idRestaurant;

    public Workmate() {}

    public Workmate(String email, String name, Uri photoUri, String idRestaurant) {
        this.email = email;
        this.name = name;
        this.photoUrl = photoUri.toString();
        this.idRestaurant = idRestaurant;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getIdRestaurant() { return idRestaurant; }
    public void setIdRestaurant(String idRestaurant) { this.idRestaurant = idRestaurant; }
}
