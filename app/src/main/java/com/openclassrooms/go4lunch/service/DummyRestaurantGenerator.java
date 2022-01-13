package com.openclassrooms.go4lunch.service;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.go4lunch.model.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DummyRestaurantGenerator {
    public static List<Restaurant> DUMMY_RESTAURANTS = Arrays.asList(
            new Restaurant("Caroline", "https://i.pravatar.cc/150?u=a042581f4e29026704d", new LatLng(-34, 151)),
            new Restaurant("Chez Jack", "https://i.pravatar.cc/150?u=a042581f4e29026704d", new LatLng(-34, 151)),
            new Restaurant("La Tour d'Argent", "https://i.pravatar.cc/150?u=a042581f4e29026704d", new LatLng(-34, 151))
        );

    static List<Restaurant> generateNeighbours() {
        return new ArrayList<>(DUMMY_RESTAURANTS);
    }
}