package com.openclassrooms.go4lunch.model;

import com.google.android.gms.maps.model.LatLng;

public class Restaurant {
    private String name;
    private String address;
    private LatLng latLng;

    public Restaurant(String name, String address, LatLng latLng) {
        this.name = name;
        this.address = address;
        this.latLng = latLng;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LatLng getLatLng() { return latLng; }
    public void setLatLng(LatLng latLng) { this.latLng = latLng; }
}
