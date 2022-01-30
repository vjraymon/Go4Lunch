package com.openclassrooms.go4lunch.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.repository.RestaurantRepository;
import com.openclassrooms.go4lunch.repository.WorkmateRepository;

import java.util.ArrayList;
import java.util.List;

public class MyViewModel extends AndroidViewModel {

    private LiveData<List<Restaurant>> restaurantsLiveData = new MutableLiveData<>();
    private LiveData<List<Workmate>> workmatesLiveData = new MutableLiveData<>();

    private final RestaurantRepository restaurantRepository;
    private final WorkmateRepository workmateRepository;

    private final Workmate myself;

    public MyViewModel(Application application) {
        super(application);
        // trigger user load.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
        {
            Log.i("TestMySelf", "MyViewModel.init user null");
            myself = null;
        } else {
            Log.i("TestMySelf", "MyViewModel.init name = " + user.getDisplayName());
            Log.i("TestMySelf", "MyViewModel.init email = " + user.getEmail());
            myself = new Workmate(user.getEmail(),
                    user.getDisplayName(),
                    user.getPhotoUrl(),
                    null);
        }
        restaurantRepository = RestaurantRepository.getRestaurantRepository(application.getApplicationContext());
        workmateRepository = WorkmateRepository.getWorkmateRepository();
        workmateRepository.addWorkmate(myself);
    }

    public void initForTest() {
        Log.i("TestsJoin", "MyViewModel.initForTest()");

        workmateRepository.addWorkmate(new Workmate("Caroline@gmail.com", "Caroline",
                Uri.parse("https://lh3.googleusercontent.com/a/AATXAJy5CJheMr1OSP2ef-jbhmBfNHRMc0XnYTnlrfj-=s96-c"),
                restaurantRepository.getRestaurants().getValue().get(1).getId()));
        workmateRepository.addWorkmate(new Workmate("Jack@gmail.com", "Jack",
                Uri.parse("https://lh3.googleusercontent.com/a/AATXAJy5CJheMr1OSP2ef-jbhmBfNHRMc0XnYTnlrfj-=s96-c"),
                null));
        workmateRepository.addWorkmate(new Workmate("Emilie@gmail.com", "Emilie",
                Uri.parse("https://lh3.googleusercontent.com/a/AATXAJy5CJheMr1OSP2ef-jbhmBfNHRMc0XnYTnlrfj-=s96-c"),
                restaurantRepository.getRestaurants().getValue().get(0).getId()));
        workmateRepository.addWorkmate(new Workmate("Albert@gmail.com", "Albert",
                Uri.parse("https://lh3.googleusercontent.com/a/AATXAJy5CJheMr1OSP2ef-jbhmBfNHRMc0XnYTnlrfj-=s96-c"),
                restaurantRepository.getRestaurants().getValue().get(1).getId()));

    }

    public Restaurant getRestaurantByLatLng(LatLng id) {
        List<Restaurant> restaurants = restaurantsLiveData.getValue();
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


    public Restaurant getRestaurantById(String id) {
        List<Restaurant> restaurants = restaurantsLiveData.getValue();
        if ((restaurants == null) || (id == null)) {
            if (restaurants == null)
            {
                Log.i("TestJoin","MyViewModel: getRestaurantById: restaurants null");
            }
            if (id == null) {
                Log.i("TestJoin", "MyViewModel: getRestaurantById: id null");
            }
            return null;
        }
        for (Restaurant i: restaurants) {
            if (i.getId() == null) {
                Log.i("TestPlace","MyViewModel: getRestaurantById: " + i.getName() + " without Id");
            } else if (id.equals(i.getId())) {
                Log.i("TestJoin","MyViewModel: getRestaurantById: found restaurant");
                return i;
            }
        }
        Log.i("TestJoin","MyViewModel: getRestaurantById: restaurant not found");
        return null;
    }
 /*
    public LatLng getRestaurant(Workmate w) {
        if (w.getHasJoined()) return new LatLng(w.getLatitude(), w.getLongitude());
        return null;
    }

    List<Workmate> workmatesByLatLng;
    public List<Workmate> getWorkmatesByLatLng(LatLng id) {
        List<Workmate> workmates = workmatesLiveData.getValue();
        workmatesByLatLng = new ArrayList<>();
        if ((workmates == null) || (id == null)) {
            if (workmates == null)
            {
                Log.i("TestJoin","MyViewModel: getWorkmatesByLatLng: restaurants null");
            }
            if (id == null) {
                Log.i("TestJoin", "MyViewModel: getWorkmatesByLatLng: id null");
            }
            return null;
        }
        Log.i("TestJoinedList","MyViewModel: getWorkmatesByLatLng: " + id.latitude + "," + id.longitude);
        for (Workmate i: workmates) {
            Log.i("TestJoinedList","MyViewModel: getWorkmatesByLatLng: " + i.getName());
            Log.i("TestJoinedList","MyViewModel: getWorkmatesByLatLng: " + i.getLatitude() + "," + i.getLongitude());
            if (getRestaurant(i) == null) {
                Log.i("TestPlace","MyViewModel: getWorkmatesByLatLng: " + i.getName() + " without LatLng");
            } else if (id.equals(getRestaurant(i))) {
                Log.i("TestJoin","MyViewModel: getWorkmatesByLatLng: found restaurant");
                workmatesByLatLng.add(i);
            } else {
                Log.i("TestJoin","MyViewModel: getWorkmatesByLatLng: differents restaurant");
            }
        }
        return workmatesByLatLng;
    }
*/
    List<Workmate> workmatesByIdRestaurant;
    public List<Workmate> getWorkmatesByIdRestaurant(String id) {
        List<Workmate> workmates = workmatesLiveData.getValue();
        workmatesByIdRestaurant = new ArrayList<>();
        if ((workmates == null) || (id == null)) {
            if (workmates == null)
            {
                Log.i("TestJoin","MyViewModel: getWorkmatesByIdRestaurant: restaurants null");
            }
            if (id == null) {
                Log.i("TestJoin", "MyViewModel: getWorkmatesByIdRestaurant: id null");
            }
            return null;
        }
        Log.i("TestJoinedList","MyViewModel: getWorkmatesByIdRestaurant: " + id);
        for (Workmate i: workmates) {
            Log.i("TestJoinedList","MyViewModel: getWorkmatesByIdRestaurant: " + i.getName());
            Log.i("TestJoinedList","MyViewModel: getWorkmatesByIdRestaurant: " + i.getIdRestaurant());
            if (i.getIdRestaurant() == null) {
                Log.i("TestPlace","MyViewModel: getWorkmatesByIdRestaurant: " + i.getName() + " without IdRestaurant");
            } else if (id.equals(i.getIdRestaurant())) {
                Log.i("TestJoin","MyViewModel: getWorkmatesByIdRestaurant: found restaurant");
                workmatesByIdRestaurant.add(i);
            } else {
                Log.i("TestJoin","MyViewModel: getWorkmatesByIdRestaurant: differents restaurant");
            }
        }
        return workmatesByIdRestaurant;
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
        workmatesLiveData = workmateRepository.getWorkmates();
        return workmatesLiveData;
    }

    public LiveData<List<Restaurant>> getRestaurants() {
        if (restaurantRepository == null) {
            Log.i("TestPlace","Error restaurantRepository null in MyViewModel");
            return null;
        }
        restaurantsLiveData = restaurantRepository.getRestaurants();
        return restaurantsLiveData;
    }
}
