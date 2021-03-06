package com.openclassrooms.go4lunch.model;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class Restaurant {
    private String name;
    private String address;
    private LatLng latLng;
    private String phoneNumber;
    private String openingHours;
    private String websiteUri;
    private Bitmap bitmap;
    private String id;

    public Restaurant(String id, String name, String address, LatLng latLng, String openingHours, String websiteUri, Bitmap bitmap, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latLng = latLng;
        this.phoneNumber = phoneNumber;
        this.openingHours = openingHours;
        this.websiteUri = websiteUri;
        this.bitmap = bitmap;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LatLng getLatLng() { return latLng; }
    public void setLatLng(LatLng latLng) { this.latLng = latLng; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public String getWebsiteUri() { return websiteUri; }
    public void setWebsiteUri(String websiteUri) { this.websiteUri = websiteUri; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Bitmap getBitmap() { return bitmap; }
    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }
}
