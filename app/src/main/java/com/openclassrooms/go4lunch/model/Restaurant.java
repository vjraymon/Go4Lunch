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
    private String iconUrl;

    public Restaurant(String name, String address, LatLng latLng, String openingHours, String websiteUri, Bitmap bitmap, String iconUrl, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.latLng = latLng;
        this.phoneNumber = phoneNumber;
        this.openingHours = openingHours;
        this.websiteUri = websiteUri;
        this.iconUrl = iconUrl;
        this.bitmap = bitmap;
    }

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

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public Bitmap getBitmap() { return bitmap; }
    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }
}
