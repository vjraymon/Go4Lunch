package com.openclassrooms.go4lunch.repository;

import static com.google.android.libraries.places.api.model.Place.Type.RESTAURANT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RestaurantRepository {
    private static RestaurantRepository service;

    /**
     * Get an instance on RestaurantRepository
     */
    public static RestaurantRepository getRestaurantRepository(Context context) {
        if (service == null) {
            service = new RestaurantRepository(context);
        }
        return service;
    }

    private PlacesClient placesClient;
    private static final int M_MAX_ENTRIES = 5;

    public RestaurantRepository(Context context) {
        Log.i("TestPlace", "Places.initialize");
        Places.initialize(Objects.requireNonNull(context), context.getString(R.string.google_maps_key));
        Log.i("TestPlace", "Places.createClient");
        placesClient = Places.createClient(Objects.requireNonNull(context));
    }
    private final MutableLiveData<List<Restaurant>> restaurants = new MutableLiveData<>();

    public void select(List<Restaurant> item) {
        restaurants.setValue(item);
    }

    public LiveData<List<Restaurant>> getRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();

        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.TYPES
        );
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
        Log.i("TestPlace", "placesClient.findCurrentPlace");
        @SuppressLint("MissingPermission")
        final Task<FindCurrentPlaceResponse> placeResult = placesClient.findCurrentPlace(request);
        placeResult.addOnCompleteListener(
                (response) -> {
                    if (response.isSuccessful() && response.getResult() != null) {
                        FindCurrentPlaceResponse likelyPlaces = response.getResult();
                        int count = Math.min(likelyPlaces.getPlaceLikelihoods().size(), M_MAX_ENTRIES);
                        int i=0;
                        for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                            if (Objects.requireNonNull(placeLikelihood.getPlace().getTypes()).contains(RESTAURANT)) {
                                i++;
                                if (i>count) {
                                    break;
                                }
                                Log.i("TestPlace", "location i = " + i + " : " + placeLikelihood.getPlace().getName());
                                Restaurant restaurant = new Restaurant(
                                        placeLikelihood.getPlace().getName(),
                                        placeLikelihood.getPlace().getAddress(),
                                        placeLikelihood.getPlace().getLatLng()
                                );
                                restaurants.add(restaurant);
                            }
                        }
                        select(restaurants);
                    } else {
                        Log.i("TestPlace", "incorrect response");
                    }
                }
        );

        return this.restaurants;
    }

    public Restaurant getRestaurantByLatLng(LatLng id) {
        List<Restaurant> restaurants = this.restaurants.getValue();
        if (restaurants == null)
        {
            restaurants = new ArrayList<>();
        }
        for (Restaurant i: restaurants) {
            if (id.equals(i.getLatLng())) {
                return i;
            }
        }
        return null;
    }
}
