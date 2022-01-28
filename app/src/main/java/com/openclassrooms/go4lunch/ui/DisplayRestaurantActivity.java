package com.openclassrooms.go4lunch.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        Log.i("TestPlace", "DisplayRestaurantActivity.navigate id = (" + restaurant.getLatLng().latitude + "," + restaurant.getLatLng().longitude + ")");
        Intent intent = new Intent(context, DisplayRestaurantActivity.class);
        intent.putExtra("keyLat", restaurant.getLatLng().latitude);
        intent.putExtra("keyLng", restaurant.getLatLng().longitude);
        ActivityCompat.startActivity(context, intent, null);
    }

    private Restaurant restaurant;
    private MyViewModel  myViewModel;
    private LatLng currentId;
    private List<Workmate> workmates;
    private boolean workmateInitialized = false;
    private boolean restaurantInitialized = false;
    Button buttonRestaurantJoin;
    TextView textRestaurantName;
    TextView textRestaurantOpeningHours;
    TextView textRestaurantWebsiteUri;
    Button buttonRestaurantPhoneNumber;

    RecyclerView recyclerView;
    List<Workmate> joinedWorkmates = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_restaurant);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getRestaurants().observe(this, this::updateRestaurantsList);
        myViewModel.getWorkmates().observe(this, this::updateWorkmatesList);

        double mLatitude = getIntent().getDoubleExtra("keyLat", 0);
        double mLongitude = getIntent().getDoubleExtra("keyLng", 0);
        currentId = new LatLng(mLatitude,mLongitude);
        Log.i("TestPlace", "DisplayRestaurantActivity.onCreate id = (" + currentId.latitude + "," + currentId.longitude + ")");
        textRestaurantName = findViewById(R.id.display_restaurant_name);
        textRestaurantOpeningHours = findViewById(R.id.display_restaurant_opening_hours);
        textRestaurantWebsiteUri = findViewById(R.id.display_restaurant_website_uri);
        buttonRestaurantPhoneNumber = findViewById(R.id.display_restaurant_phone_number);
        buttonRestaurantJoin = findViewById(R.id.display_restaurant_join);
        buttonRestaurantJoin.setEnabled(false);
        buttonRestaurantJoin.setOnClickListener(v -> {
            Log.i("TestJoin", "DisplayRestaurantActivity: clicked on Join");
            myViewModel.joinRestaurant(this.restaurant);
        });

            recyclerView = findViewById(R.id.listJoinWorkmate);
            Context context = getApplicationContext();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void updateRestaurantsList(List<Restaurant> restaurants) {
        Log.i("TestJoin", "DisplayRestaurantActivity: updateRestaurantsList");
        for (Restaurant restaurant : restaurants) {
            Log.i("TestPlace", "DisplayRestaurantActivity: updateRestaurantsList list retrieved = " + restaurant.getName());
        }
        Log.i("TestPlace", "DisplayRestaurantActivity: updateRestaurantsList end of location list retrieved");

        this.restaurant = myViewModel.getRestaurantByLatLng(this.currentId);
        if (this.restaurant == null) {
            Log.i("TestJoin", "DisplayRestaurantActivity: updateRestaurantsList unknown restaurant ? (" + this.currentId.latitude + "," + this.currentId.longitude + ")");
        } else {
            Log.i("TestJoin", "DisplayRestaurantActivity: updateRestaurantsList restaurant " + this.restaurant.getName());
        }
        if (this.restaurant == null) {
            return;
        }
        if (restaurant.getLatLng() == null) {
            textRestaurantName.setText(restaurant.getName() + " null");
        } else {
            textRestaurantName.setText(restaurant.getName()
                        + "(" + restaurant.getLatLng().latitude
                        + " , " + restaurant.getLatLng().longitude);
            textRestaurantOpeningHours.setText(restaurant.getOpeningHours());
            textRestaurantWebsiteUri.setText(restaurant.getWebsiteUri());
            buttonRestaurantPhoneNumber.setText(restaurant.getPhoneNumber());
            buttonRestaurantPhoneNumber.setOnClickListener(v -> {
                Log.i("TestJoin", "DisplayRestaurantActivity: clicked on Call");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+buttonRestaurantPhoneNumber.getText()));
                startActivity(callIntent);
            });
        }

        Log.i("TestJoin", "DisplayRestaurantActivity: updateRestaurantsList call setDisplayJoin");
        restaurantInitialized = true;
        setDisplayJoin();
    }
    private void updateWorkmatesList(List<Workmate> workmates) {
        Log.i("TestJoin", "DisplayRestaurantActivity: updateWorkmatesList");
        this.workmates = workmates;
        for (Workmate workmate : workmates) {
            if (workmate.getHasJoined()) {
                Log.i("TestPlace", "DisplayRestaurantActivity: updateWorkmatesListlocation list retrieved = " + workmate.getName() + " (" + workmate.getLatitude() + "," + workmate.getLongitude() + ")");
            } else {
                Log.i("TestPlace", "DisplayRestaurantActivity: updateWorkmatesListlocation list retrieved = " + workmate.getName() + " none");
            }
        }
        Log.i("TestJoin", "DisplayRestaurantActivity: updateWorkmatesList call setDisplayJoin");
        workmateInitialized = true;
        setDisplayJoin();
        this.refresh(currentId);
    }

    private void setDisplayJoin() {
        Log.i("TestJoin", "DisplayRestaurantActivity.setDisplayJoin()");
        if ((restaurantInitialized) && workmateInitialized) {
            Log.i("TestsJoin", "DisplayRestaurantActivity.setDisplayJoin enable button");
            buttonRestaurantJoin.setEnabled(true);
            //initialization for tests
//            myViewModel.initForTest();
            restaurantInitialized = false;
            workmateInitialized = false;
        }
    }

    private void refresh(LatLng mLatLng) {
        if (mLatLng == null) return;
        if ((this.workmates != null) && (myViewModel != null)) {
            Log.i("TestJoinedList", "JoinedWorkmateFragment.refresh call recyclerView.setAdapter LatLng = (" + mLatLng.latitude + "," + mLatLng.longitude);
            joinedWorkmates = myViewModel.getWorkmatesByLatLng(mLatLng);
        }
        recyclerView.setAdapter(new MyJoinedWorkmateRecyclerViewAdapter(joinedWorkmates));
    }
}