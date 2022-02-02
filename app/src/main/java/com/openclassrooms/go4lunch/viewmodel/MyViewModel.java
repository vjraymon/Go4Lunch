package com.openclassrooms.go4lunch.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.RestaurantLike;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.repository.RestaurantLikeRepository;
import com.openclassrooms.go4lunch.repository.RestaurantRepository;
import com.openclassrooms.go4lunch.repository.WorkmateRepository;
import com.openclassrooms.go4lunch.ui.MySettings;

import java.util.ArrayList;
import java.util.List;

public class MyViewModel extends AndroidViewModel {

    private final String TAG_LIKE = "TestLike";

    private LiveData<List<Restaurant>> restaurantsLiveData = new MutableLiveData<>();
    private LiveData<List<Workmate>> workmatesLiveData = new MutableLiveData<>();
    private LiveData<List<RestaurantLike>> restaurantLikesLiveData = new MutableLiveData<>();

    private final RestaurantRepository restaurantRepository;
    private final WorkmateRepository workmateRepository;
    private final RestaurantLikeRepository restaurantLikeRepository;

    private Workmate myself;

    public Workmate getMyself() {
        List<Workmate> workmates = workmatesLiveData.getValue();
        if (workmates == null) {
            Log.i("TestYourLunch","MyViewModel: getMyself: workmates null");
            return myself; // return the current value
        }
        Log.i("TestYourLunch","MyViewModel: getMyself: ");
        for (Workmate i: workmates) {
            if (i.getEmail().equals(myself.getEmail())) {
                Log.i("TestJoin","MyViewModel: getMyself: found workmate");
                myself = i; // override all
            }
        }
        return myself;
    }
    private final Application application;
    public MyViewModel(Application application) {
        super(application);
        this.application = application;
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
                        (user.getPhotoUrl() == null) ? null : user.getPhotoUrl().toString(),
                        null);
        }
        restaurantRepository = RestaurantRepository.getRestaurantRepository(application.getApplicationContext());
        workmateRepository = WorkmateRepository.getWorkmateRepository();
        restaurantLikeRepository = RestaurantLikeRepository.getRestaurantLikeRepository();
        workmateRepository.addWorkmate(myself); // adds only if it isn't registered yet
    }

    public void initializationNotification() {
        Restaurant restaurant = null;
        List<Workmate> workmates = null;
        myself = getMyself(); // updating
        if (myself != null) {
            restaurant = getRestaurantById(myself.getIdRestaurant());
            workmates = getWorkmatesByIdRestaurant(myself.getIdRestaurant());
        }
        MySettings mySettings = MySettings.getMySettings();
        mySettings.setRestaurant(restaurant);
        mySettings.setAttendees(workmates);
        Log.i("Fire", "MyViewModel.initializationNotification: " + myself
                + " restaurant = " + ((restaurant == null) ? "null" : restaurant.getName())
                + " attendees = " + ((workmates == null) ? "null" : workmates.size()));
    }

    public void initForTest() {
        Log.i("TestsJoin", "MyViewModel.initForTest()");

        try {
            List<Restaurant> restaurants = restaurantRepository.getRestaurants(application.getApplicationContext()).getValue();
            if (restaurants == null) return;
            Restaurant restaurant0 = restaurants.get(0);
            Restaurant restaurant1 = restaurants.get(1);
            if ((restaurant0 == null) || (restaurant1 == null)) return;
                workmateRepository.addWorkmate(new Workmate("Caroline@gmail.com", "Caroline",
                        "https://lh3.googleusercontent.com/a/AATXAJy5CJheMr1OSP2ef-jbhmBfNHRMc0XnYTnlrfj-=s96-c",
                        restaurant1.getId()));
                workmateRepository.addWorkmate(new Workmate("Jack@gmail.com", "Jack",
                        "https://lh3.googleusercontent.com/a/AATXAJy5CJheMr1OSP2ef-jbhmBfNHRMc0XnYTnlrfj-=s96-c",
                        null));
                workmateRepository.addWorkmate(new Workmate("Emilie@gmail.com", "Emilie",
                        "https://lh3.googleusercontent.com/a/AATXAJy5CJheMr1OSP2ef-jbhmBfNHRMc0XnYTnlrfj-=s96-c",
                        restaurant0.getId()));
                workmateRepository.addWorkmate(new Workmate("Albert@gmail.com", "Albert",
                        null,
                        restaurant1.getId()));
        } catch (Exception e) {
            Log.e("Test","MyViewModel.initForTest exception " + e.getMessage());
        }
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

    public void addRestaurantById(String id) {
        List<Restaurant> restaurants = restaurantsLiveData.getValue();
        if (id == null) {
            Log.i("TestJoin", "MyViewModel: addRestaurantById: id null");
            return;
        }
        boolean found = false;
        if (restaurants != null) for (Restaurant i: restaurants) {
            if (i.getId() == null) {
                Log.i("TestPlace","MyViewModel: addRestaurantById: " + i.getName() + " without Id");
            } else if (id.equals(i.getId())) {
                Log.i("TestJoin","MyViewModel: addRestaurantById: found restaurant");
                found = true;
                break;
            }
        }
        if (!found) {
            restaurantRepository.getRestaurantByIdFromGooglePlace(id);
        }
    }

    public void incLike(String id) {
        List<RestaurantLike> restaurantLikes = restaurantLikesLiveData.getValue();
        if ((restaurantLikes == null) || (id == null)) {
            if (restaurantLikes == null)
            {
                Log.i(TAG_LIKE,"MyViewModel: incLike: restaurantLikes null");
            }
            if (id == null) {
                Log.i(TAG_LIKE, "MyViewModel: incLike: id null");
            }
            return;
        }
        String idLike = id + myself.getEmail();
        for (RestaurantLike i: restaurantLikes) {
            if (i.getId() == null) {
                Log.i(TAG_LIKE,"MyViewModel: incLike: " + i.getName() + " without Id");
            } else if ((i.getId()).equals(idLike)) {
                Log.i(TAG_LIKE,"MyViewModel: incLike: found restaurant");
                restaurantLikeRepository.updateLike(i, i.getLike()+1);
                return;
            }
        }
        Log.i(TAG_LIKE,"MyViewModel: incLike: restaurant not found");
        Restaurant restaurant = getRestaurantById(id);
        if (restaurant == null) return;
        RestaurantLike restaurantLike = new RestaurantLike(idLike, restaurant.getName(), 1);
        restaurantLikeRepository.addRestaurantLike(restaurantLike);
    }

    public int getLikeById(String id) {
        if (restaurantLikesLiveData == null) {
            Log.i(TAG_LIKE,"MyViewModel: getLikeById: restaurantLikesLiveData null");
            return 1;
        }
        List<RestaurantLike> restaurantLikes = restaurantLikesLiveData.getValue();
        if ((restaurantLikes == null) || (id == null)) {
            if (restaurantLikes == null)
            {
                Log.i(TAG_LIKE,"MyViewModel: getLikeById: restaurantLikes null");
            }
            if (id == null) {
                Log.i(TAG_LIKE, "MyViewModel: getLikeById: id null");
            }
            return 1;
        }
        double sum = 0;
        double maxSum = 0;
        for (RestaurantLike i: restaurantLikes) {
            if (i == null) {
                Log.i(TAG_LIKE, "MyViewModel: getLikeById: element null");
            } else if (i.getId() == null) {
                Log.i(TAG_LIKE,"MyViewModel: getLikeById: " + i.getName() + " without Id");
            } else {
                if ((i.getId()).contains(id)) {
                    Log.i(TAG_LIKE,"MyViewModel: getLikeById: found restaurant");
                    sum = sum + i.getLike();
                }
                maxSum = maxSum + i.getLike();
            }
        }
        if ((restaurantsLiveData.getValue() == null) || (restaurantsLiveData.getValue().size() <= 0)) return 1;
        double averageSum = maxSum / restaurantsLiveData.getValue().size();
        double rate = (averageSum==0) ? 1 : Math.round(sum/averageSum) + 1;
        Log.i(TAG_LIKE,"MyViewModel: getLikeById: end of loop on restaurantsLikes sum = " + sum + " maxSum = " + maxSum + " averageSum = " + averageSum + " rate = " + rate);
        rate = Math.min(3, Math.max(1, rate));
        return (int)rate;
    }

//    List<Workmate> workmatesByIdRestaurant;
    public List<Workmate> getWorkmatesByIdRestaurant(String id) {
        List<Workmate> workmates = workmatesLiveData.getValue();
        List<Workmate> workmatesByIdRestaurant = new ArrayList<>();
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
                Log.i("TestJoin","MyViewModel: getWorkmatesByIdRestaurant: different restaurant");
            }
        }
        return workmatesByIdRestaurant;
    }

    public void joinRestaurant(Restaurant restaurant) {
        Log.i("TestJoin","click on join restaurant (MyViewModel)");
        if (restaurant != null) {
            workmateRepository.setRestaurant(myself, restaurant);
        }
        initializationNotification();
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
        restaurantsLiveData = restaurantRepository.getRestaurants(application.getApplicationContext());
        return restaurantsLiveData;
    }

    public LiveData<List<RestaurantLike>> getRestaurantLikes() {
        if (restaurantLikeRepository == null) {
            Log.i(TAG_LIKE,"Error restaurantLikeRepository null in MyViewModel");
            return null;
        }
        restaurantLikesLiveData = restaurantLikeRepository.getRestaurantLikes();
        return restaurantLikesLiveData;
    }
}
