package com.openclassrooms.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.viewmodel.MyViewModel;

import java.util.List;

public class DisplayRestaurantActivity extends AppCompatActivity {
    /**
     * Used to navigate to this activity
     */
    public static void navigate(android.content.Context context, Restaurant restaurant) {
        Log.i("TestPlace", "id navigate = (" + restaurant.getLatLng().latitude + "," + restaurant.getLatLng().longitude + ")");
        Intent intent = new Intent(context, DisplayRestaurantActivity.class);
        intent.putExtra("keyLat", restaurant.getLatLng().latitude);
        intent.putExtra("keyLng", restaurant.getLatLng().longitude);
        ActivityCompat.startActivity(context, intent, null);
    }

    private Restaurant restaurant;
    private MyViewModel  myViewModel;
//    private RestaurantRepository restaurantRepository;
//    private Workmate workmate;
//    private WorkmateRepository workmateRepository;
    private LatLng currentId;
    private List<Workmate> workmates;
    private List<Restaurant> restaurants;
    private boolean workmateInitialized = false;
    private boolean restaurantInitialized = false;
    Button buttonRestaurantJoin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_restaurant);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.init(this);
        myViewModel.getRestaurants().observe(this, this::updateRestaurantsList);
        myViewModel.getWorkmates().observe(this, this::updateWorkmatesList);

        double mLatitude = getIntent().getDoubleExtra("keyLat", 0);
        double mLongitude = getIntent().getDoubleExtra("keyLng", 0);
        currentId = new LatLng(mLatitude,mLongitude);
        Log.i("TestPlace", "id onCreate= (" + currentId.latitude + "," + currentId.longitude + ")");
        buttonRestaurantJoin = findViewById(R.id.display_restaurant_join);

    }

    private void updateRestaurantsList(List<Restaurant> restaurants) {
        Log.i("TestJoin", "DisplayRestaurantActivity: updateRestaurantsList");
        this.restaurants = restaurants;
        for (Restaurant restaurant : restaurants) {
            Log.i("TestPlace", "location list retrieved = " + restaurant.getName());
        }
        Log.i("TestPlace", "end of location list retrieved");

        this.restaurant = myViewModel.getRestaurantByLatLng(restaurants, this.currentId);
        if (this.restaurant == null) {
            Log.i("TestJoin", "unknown restaurant ? (" + this.currentId.latitude + "," + this.currentId.longitude + ")");
        } else {
            Log.i("TestJoin", "restaurant " + this.restaurant.getName());
        }
        if (this.restaurant == null) {
            return;
        }
        Log.i("TestJoin", "call setDisplayJoin");
        restaurantInitialized = true;
        setDisplayJoin();
    }
    private void updateWorkmatesList(List<Workmate> workmates) {
        Log.i("TestJoin", "DisplayRestaurantActivity: updateWorkmatesList");
        this.workmates = workmates;
        for (Workmate workmate : workmates) {
            Log.i("TestPlace", "location list retrieved = " + workmate.getName());
        }
        Log.i("TestPlace", "end of location list retrieved");
        Log.i("TestJoin", "call setDisplayJoin");
        workmateInitialized = true;
        setDisplayJoin();
    }

    private void setDisplayJoin() {
        Log.i("TestJoin", "begin setDisplayJoin()");
        if ((restaurantInitialized) && workmateInitialized) {
            Log.i("TestsJoin", "enter setActivity()");
            Button buttonRestaurantJoin = findViewById(R.id.display_restaurant_join);
            buttonRestaurantJoin.setOnClickListener(v -> {
                Log.i("TestJoin", "clicked on Join (");
                myViewModel.joinRestaurant(this.restaurant);
            });

            //initialization for tests
            myViewModel.initForTest();
            restaurantInitialized = false;
            workmateInitialized = false;
        }
    }
}