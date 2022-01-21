package com.openclassrooms.go4lunch.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.repository.RestaurantRepository;
import com.openclassrooms.go4lunch.repository.WorkmateRepository;

import java.util.ArrayList;
import java.util.List;

public class MyViewModel extends ViewModel {
/*
    private final MutableLiveData<Restaurant> restaurantLiveData = new MutableLiveData<>();


    public LiveData<Restaurant> getRestaurant() {
        return restaurantLiveData;
    }
*/

    private RestaurantRepository restaurantRepository;
    private WorkmateRepository workmateRepository;

    private Workmate myself = new Workmate("vjraymon@gmail.com", "Myself", null);

    public MyViewModel() {
        // trigger user load.
    }

    public void init(Context context) {
        restaurantRepository = RestaurantRepository.getRestaurantRepository(context);
        workmateRepository = WorkmateRepository.getWorkmateRepository(context);
        workmateRepository.addWorkmate(myself);
    }

    public Restaurant getRestaurantByLatLng(List<Restaurant> restaurants, LatLng id) {
        if ((restaurants == null) || (id == null))
        {
            return null;
        }
        for (Restaurant i: restaurants) {
            if (i.getLatLng() == null) {
                Log.i("TestPlace",i.getName() + " without LatLng");
            } else if (id.equals(i.getLatLng())) {
                return i;
            }
        }
        return null;
    }

    public void joinRestaurant(Restaurant restaurant) {
        // depending on the action, do necessary business logic calls and update the
        // userLiveData.
        Log.i("TestPlace","click on join restaurant (MyViewModel)");
        workmateRepository.setRestaurant(myself, restaurant);
    }

    public LiveData<List<Workmate>> getWorkmates() {
        if (workmateRepository == null) {
            Log.i("TestPlace","Error workmateRepository null in MyViewModel");
            return null;
        }
        return workmateRepository.getWorkmates();
    }

    public LiveData<List<Restaurant>> getRestaurants() {
        if (restaurantRepository == null) {
            Log.i("TestPlace","Error restaurantRepository null in MyViewModel");
            return null;
        }
        return restaurantRepository.getRestaurants();
    }
}
