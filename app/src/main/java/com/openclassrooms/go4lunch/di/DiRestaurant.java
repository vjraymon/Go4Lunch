package com.openclassrooms.go4lunch.di;

import com.openclassrooms.go4lunch.service.DummyRestaurantApiService;
import com.openclassrooms.go4lunch.service.RestaurantApiService;

public class DiRestaurant {

    private static RestaurantApiService service = new DummyRestaurantApiService();

    /**
     * Get an instance on RestaurantApiService
     */
    public static RestaurantApiService getRestaurantApiService() {
        return service;
    }

    /**
     * Get always a new instance on RestaurantApiService. Useful for tests, so we ensure the context is clean.
     */
    public static RestaurantApiService getNewInstanceApiService() {
        service = new DummyRestaurantApiService();
        return service;
    }
}
