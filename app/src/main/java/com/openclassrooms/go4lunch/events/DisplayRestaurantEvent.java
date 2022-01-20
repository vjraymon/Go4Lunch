package com.openclassrooms.go4lunch.events;

import com.openclassrooms.go4lunch.model.Restaurant;

public class DisplayRestaurantEvent {

    /**
     * Restaurant to display
     */
    public Restaurant restaurant;

    /**
     * Constructor.
     */
    public DisplayRestaurantEvent(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}