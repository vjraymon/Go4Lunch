package com.openclassrooms.go4lunch.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private Workmate myself;

    public MyViewModel() {
        // trigger user load.
    }

    public void init(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i("TestMySelf", "MyViewModel.init name = " + user.getDisplayName());
        Log.i("TestMySelf", "MyViewModel.init email = " + user.getEmail());
        myself = new Workmate(user.getEmail(), user.getDisplayName(), null);
        restaurantRepository = RestaurantRepository.getRestaurantRepository(context);
        workmateRepository = WorkmateRepository.getWorkmateRepository(context);
        workmateRepository.addWorkmate(myself);

    }

    public void initForTest() {
        Log.i("TestsJoin", "begin initForTest()");
        workmateRepository.addWorkmate(new Workmate("Caroline@gmail.com", "Caroline",
                restaurantRepository.getRestaurants().getValue().get(1).getLatLng()));
        workmateRepository.addWorkmate(new Workmate("Jack@gmail.com", "Jack", null));
        workmateRepository.addWorkmate(new Workmate("Emilie@gmail.com", "Emilie",
                restaurantRepository.getRestaurants().getValue().get(0).getLatLng()));
        workmateRepository.addWorkmate(new Workmate("Albert@gmail.com", "Albert",
                restaurantRepository.getRestaurants().getValue().get(1).getLatLng()));

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

    public LatLng getRestaurant(Workmate w) {
        if (w.getHasJoined()) return new LatLng(w.getLatitude(), w.getLongitude());
        return null;
    }
    public void setRestaurant(Workmate w, LatLng restaurant) {
        w.setHasJoined((restaurant != null));
        if (w.getHasJoined()) {
            w.setLatitude(restaurant.latitude);
            w.setLongitude(restaurant.longitude);
        }
    }

    List<Workmate> workmatesByLatLng;
    public List<Workmate> getWorkmatesByLatLng(List<Workmate> workmates, LatLng id) {
//        workmates = workmateRepository.getWorkmates().getValue();
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
        Log.i("TestJoinedList","MyViewModel: getRestaurantByLatLng: " + id.latitude + "," + id.longitude);
        for (Workmate i: workmates) {
            Log.i("TestJoinedList","MyViewModel: getRestaurantByLatLng: " + i.getName());
            Log.i("TestJoinedList","MyViewModel: getRestaurantByLatLng: " + i.getLatitude() + "," + i.getLongitude());
            if (getRestaurant(i) == null) {
                Log.i("TestPlace","MyViewModel: getRestaurantByLatLng: " + i.getName() + " without LatLng");
            } else if (id.equals(getRestaurant(i))) {
                Log.i("TestJoin","MyViewModel: getRestaurantByLatLng: found restaurant");
                workmatesByLatLng.add(i);
            } else {
                Log.i("TestJoin","MyViewModel: getRestaurantByLatLng: differents restaurant");
            }
        }
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
