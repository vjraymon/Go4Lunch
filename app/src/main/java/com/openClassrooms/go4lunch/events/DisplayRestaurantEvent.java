package com.openClassrooms.go4lunch.events;

import com.openClassrooms.go4lunch.model.Restaurant;

public class DisplayRestaurantEvent {

    /**
     * Restaurant to display
     */
    public final Restaurant restaurant;

    /**
     * Constructor.
     */
    public DisplayRestaurantEvent(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}