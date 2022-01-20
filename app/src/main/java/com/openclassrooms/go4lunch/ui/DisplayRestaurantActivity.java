package com.openclassrooms.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.repository.RestaurantRepository;

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
    private RestaurantRepository restaurantRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_restaurant);
        double lattitude = getIntent().getDoubleExtra("keyLat", 0);
        double longitude = getIntent().getDoubleExtra("keyLng", 0);

        LatLng id = new LatLng( lattitude, longitude);
        Log.i("TestPlace", "id onCreate= (" + id.latitude + "," + id.longitude + ")");

        restaurantRepository = RestaurantRepository.getRestaurantRepository(this);
        restaurant = restaurantRepository.getRestaurantByLatLng(id);
        if (restaurant == null) {
            Log.i("TestPlace", "unknown restaurant ? (" + id.latitude + "," + id.longitude + ")");
        } else {
            Log.i("TestPlace", "restaurant " + restaurant.getName());
        }
    }
}