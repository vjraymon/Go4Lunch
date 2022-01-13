package com.openclassrooms.go4lunch.service;

import com.openclassrooms.go4lunch.model.Restaurant;

import java.util.List;

public class DummyRestaurantApiService implements RestaurantApiService {

    private List<Restaurant> restaurants = DummyRestaurantGenerator.generateNeighbours();

    @Override
    public List<Restaurant> getRestaurants() {
        return restaurants;
    }
}
