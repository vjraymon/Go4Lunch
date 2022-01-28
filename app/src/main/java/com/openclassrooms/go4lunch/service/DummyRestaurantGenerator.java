package com.openclassrooms.go4lunch.service;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.model.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DummyRestaurantGenerator {
    public final static List<Restaurant> DUMMY_RESTAURANTS = Arrays.asList(
/*
            new Restaurant("Caroline", "https://i.pravatar.cc/150?u=a042581f4e29026704d", new LatLng(-34, 151),
                    "8h-20h","toto","0177465177"),
            new Restaurant("Chez Jack", "https://i.pravatar.cc/150?u=a042581f4e29026704d", new LatLng(-34, 151),
                    "8h-20h","toto","0177465177"),
            new Restaurant("La Tour d'Argent", "https://i.pravatar.cc/150?u=a042581f4e29026704d", Bitnew LatLng(-34, 151),
                    "8h-20h","toto","0177465177")
*/
    );

    static List<Restaurant> generateNeighbours() {
        return new ArrayList<>(DUMMY_RESTAURANTS);
    }
}
