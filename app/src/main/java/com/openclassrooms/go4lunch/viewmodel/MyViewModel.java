package com.openclassrooms.go4lunch.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
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
import java.util.Objects;

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

    public void initForTest() {
        Log.i("TestsJoin", "begin initForTest()");
        workmateRepository.setRestaurant(
                Objects.requireNonNull(workmateRepository.getWorkmates().getValue()).get(2),
                Objects.requireNonNull(restaurantRepository.getRestaurants().getValue()).get(1));
    }

    public Restaurant getRestaurantByLatLng(List<Restaurant> restaurants, LatLng id) {
//        List<Restaurant> restaurants = restaurantRepository.getRestaurants().getValue();
        if ((restaurants == null) || (id == null)) {
            if (restaurants == null)
            {
                Log.i("TestJoin","MyViewModel: getRestaurantByLatLng: restaurants null");
            }
            if (id == null) {
                Log.i("TestJoin", "MyViewModel: getRestaurantByLatLng: id null");
            }
            return null;
        }
        for (Restaurant i: restaurants) {
            if (i.getLatLng() == null) {
                Log.i("TestPlace","MyViewModel: getRestaurantByLatLng: " + i.getName() + " without LatLng");
            } else if (id.equals(i.getLatLng())) {
                Log.i("TestJoin","MyViewModel: getRestaurantByLatLng: found restaurant");
                return i;
            }
        }
        Log.i("TestJoin","MyViewModel: getRestaurantByLatLng: restaurant not found");
        return null;
    }

    List<Workmate> workmatesByLatLng;
    public List<Workmate> getWorkmatesByLatLng(List<Workmate> workmates, LatLng id) {
        workmatesByLatLng = new ArrayList<>();
        if ((workmates == null) || (id == null)) {
            if (workmates == null)
            {
                Log.i("TestJoin","MyViewModel: getRestaurantByLatLng: restaurants null");
            }
            if (id == null) {
                Log.i("TestJoin", "MyViewModel: getRestaurantByLatLng: id null");
            }
            return null;
        }
        for (Workmate i: workmates) {
            if (i.getRestaurant() == null) {
                Log.i("TestPlace","MyViewModel: getRestaurantByLatLng: " + i.getName() + " without LatLng");
            } else if (id.equals(i.getRestaurant())) {
                Log.i("TestJoin","MyViewModel: getRestaurantByLatLng: found restaurant");
                workmatesByLatLng.add(i);
            }
        }
        Log.i("TestJoin","MyViewModel: getRestaurantByLatLng: restaurant not found");
        return workmatesByLatLng;
    }

    public void joinRestaurant(Restaurant restaurant) {
        // depending on the action, do necessary business logic calls and update the
        // userLiveData.
        Log.i("TestJoin","click on join restaurant (MyViewModel)");
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
