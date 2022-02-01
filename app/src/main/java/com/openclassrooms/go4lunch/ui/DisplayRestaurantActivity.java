package com.openclassrooms.go4lunch.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
        Log.i("TestPlace", "DisplayRestaurantActivity.navigate id = " + restaurant.getId());
        Intent intent = new Intent(context, DisplayRestaurantActivity.class);
        intent.putExtra("keyId", restaurant.getId());
        ActivityCompat.startActivity(context, intent, null);
    }

    private Restaurant restaurant;
    private MyViewModel  myViewModel;
    private String currentId;
    private List<Workmate> workmates;
    private boolean workmateInitialized = false;
    private boolean restaurantInitialized = false;
    ImageButton buttonRestaurantJoin;
    TextView textRestaurantName;
    TextView textRestaurantOpeningHours;
    Button buttonRestaurantWebsiteUri;
    Button buttonRestaurantPhoneNumber;
    Button buttonRestaurantLike;
    ImageView imageRestaurant;

    RecyclerView recyclerView;
    List<Workmate> joinedWorkmates = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_restaurant);

        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getRestaurants().observe(this, this::updateRestaurantsList);
        myViewModel.getWorkmates().observe(this, this::updateWorkmatesList);
        myViewModel.getRestaurantLikes();

        currentId = getIntent().getStringExtra("keyId");
        Log.i("TestPlace", "DisplayRestaurantActivity.onCreate id = " + currentId);
        textRestaurantName = findViewById(R.id.display_restaurant_name);
        textRestaurantOpeningHours = findViewById(R.id.display_restaurant_opening_hours);
        buttonRestaurantWebsiteUri = findViewById(R.id.display_restaurant_website_uri);
        buttonRestaurantPhoneNumber = findViewById(R.id.display_restaurant_phone_number);
        buttonRestaurantLike = findViewById(R.id.display_restaurant_like);
        buttonRestaurantJoin = findViewById(R.id.display_restaurant_join);
        imageRestaurant = findViewById(R.id.display_restaurant_bitmap);
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

        this.restaurant = myViewModel.getRestaurantById(this.currentId);
        if (this.restaurant == null) {
            Log.i("TestJoin", "DisplayRestaurantActivity: updateRestaurantsList unknown restaurant ? " + this.currentId);
        } else {
            Log.i("TestJoin", "DisplayRestaurantActivity: updateRestaurantsList restaurant " + this.restaurant.getName());
        }
        if (this.restaurant == null) {
            return;
        }
        textRestaurantName.setText(restaurant.getName());
        textRestaurantOpeningHours.setText(restaurant.getOpeningHours());

        if (restaurant.getWebsiteUri() == null) {
            buttonRestaurantWebsiteUri.setVisibility(View.INVISIBLE);
        } else {
            buttonRestaurantWebsiteUri.setVisibility(View.VISIBLE);
//            buttonRestaurantWebsiteUri.setText(restaurant.getWebsiteUri());
            buttonRestaurantWebsiteUri.setOnClickListener(v -> {
                Log.i("TestJoin", "DisplayRestaurantActivity: clicked on Website" + restaurant.getWebsiteUri());
                Uri uri = Uri.parse(restaurant.getWebsiteUri());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(webIntent);
            });
        }

        if (restaurant.getPhoneNumber() == null) {
            buttonRestaurantPhoneNumber.setVisibility(View.INVISIBLE);
        } else {
            buttonRestaurantPhoneNumber.setVisibility(View.VISIBLE);
//            buttonRestaurantPhoneNumber.setText(restaurant.getPhoneNumber());
            buttonRestaurantPhoneNumber.setOnClickListener(v -> {
                Log.i("TestJoin", "DisplayRestaurantActivity: clicked on Call" + restaurant.getPhoneNumber());
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + restaurant.getPhoneNumber()));
                startActivity(callIntent);
            });
        }

        if (this.restaurant.getBitmap() != null) {
            imageRestaurant.setImageBitmap(this.restaurant.getBitmap());
        }

        buttonRestaurantLike.setOnClickListener(v -> {
            Log.i("TestLike", "DisplayRestaurantActivity: clicked on Like" + restaurant.getName());
            myViewModel.incLike(restaurant.getId());
        });

        Log.i("TestJoin", "DisplayRestaurantActivity: updateRestaurantsList call setDisplayJoin");
        restaurantInitialized = true;
        setDisplayJoin();
    }

    private void updateWorkmatesList(List<Workmate> workmates) {
        Log.i("TestJoin", "DisplayRestaurantActivity: updateWorkmatesList");
        this.workmates = workmates;
        for (Workmate workmate : workmates) {
                Log.i("TestPlace", "DisplayRestaurantActivity: updateWorkmatesListlocation list retrieved = " + workmate.getName() + " " + workmate.getIdRestaurant());
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
            myViewModel.initForTest();
            restaurantInitialized = false;
            workmateInitialized = false;
        }
    }

    private void refresh(String mIdRestaurant) {
        if ((this.workmates != null) && (myViewModel != null)) {
            Log.i("TestJoinedList", "JoinedWorkmateFragment.refresh call recyclerView.setAdapter IdRestaurant =" + mIdRestaurant);
            joinedWorkmates = myViewModel.getWorkmatesByIdRestaurant(mIdRestaurant);
        }
        recyclerView.setAdapter(new MyJoinedWorkmateRecyclerViewAdapter(joinedWorkmates));
    }
}