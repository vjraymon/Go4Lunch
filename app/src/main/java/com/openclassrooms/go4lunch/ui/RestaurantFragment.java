package com.openclassrooms.go4lunch.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.RestaurantLike;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.viewmodel.MyViewModel;
import com.openclassrooms.go4lunch.viewmodel.MyViewModelFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A fragment representing a list of Items.
 */
public class RestaurantFragment extends Fragment {
    private final static String TAG = "TestSearch";

    List<Restaurant> restaurants;

    private enum SortMode { NONE, NAME, DISTANCE }
    private SortMode sortMode = SortMode.NONE;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RestaurantFragment() {
    }

    public static RestaurantFragment newInstance() {
        return new RestaurantFragment();
    }
    private MyViewModel myViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if ((getContext() != null) && (!Places.isInitialized())) Places.initialize(getContext(), getString(R.string.google_maps_key));
        myViewModel = new ViewModelProvider(this, new MyViewModelFactory(Objects.requireNonNull(this.getActivity()).getApplication())).get(MyViewModel.class);
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_sort_restaurant, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.restaurant_search) {
            onSearchCalled();
            return true;
        }
        if (item.getItemId() == R.id.restaurant_alphabetical_sort) {
            sortMode = refresh(SortMode.NAME);
            return true;
        }
        if (item.getItemId() == R.id.restaurant_proximity_sort) {
            sortMode = refresh(SortMode.DISTANCE);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) getActivity().finish();
            return true;
        }
        return false;
    }

    // TODO code to refactor (common to map and restaurant list)
    private final ActivityResultLauncher<Intent> autoCompleteResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                int resultCode = result.getResultCode();
                Intent data = result.getData();
                if (data == null) return;
                if (resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    if (place.getTypes() == null) return;
                    Log.i(TAG, "MainActivity.autoCompleteResultLauncher Place: " + place.getId());
                    if (place.getTypes().contains(Place.Type.RESTAURANT)) {
                        myViewModel.addRestaurantById(place.getId());
                        Restaurant restaurant = myViewModel.getRestaurantById(place.getId());
                        // if it is a new restaurant, it will be available on the next search
                        // but the next load of Go4Lunch will removes it
                        // TODO: enhance the logic
                        if (restaurant != null)
                        {
                            Log.i("TestPlace", "MainActivity.autoCompleteResultLauncher id = (" + restaurant.getName() + ")");
                            DisplayRestaurantActivity.navigate(getContext(), restaurant);
                        }
                    }
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.i(TAG, "MainActivity.autoCompleteResultLauncher error = " + status.getStatusMessage());
                } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i(TAG, "MainActivity.autoCompleteResultLauncher RESULT_CANCELED");
                }

            });

    public void onSearchCalled() {
        Log.i(TAG, "MainActivity.onActivityResult");
        if (getContext() != null) {
            // Set the fields to specify which types of place data to return.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.TYPES);
            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("FR").setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(getContext());
            autoCompleteResultLauncher.launch(intent);
        }
    }

    private static class CustomComparatorName implements Comparator<Restaurant> {
        @Override
        public int compare(Restaurant o1, Restaurant o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    private long distance(Restaurant r) {
        double distance = 0;
        if ((MapFragment.getLastKnownLocation() != null) && (r != null) && (r.getLatLng() != null)) {
            float[] results = new float[1];
            Location.distanceBetween(MapFragment.getLastKnownLocation().getLatitude(), MapFragment.getLastKnownLocation().getLongitude(),
                    r.getLatLng().latitude, r.getLatLng().longitude,
                    results);
            distance = results[0];
        }
        return Math.round(distance);
    }

    private class CustomComparatorDistance implements Comparator<Restaurant> {
        @Override
        public int compare(Restaurant o1, Restaurant o2) {
            return (int) (distance(o1) - distance(o2));
        }
    }

    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            myViewModel.getWorkmates().observe(getViewLifecycleOwner(), this::updateWorkmatesList);
            myViewModel.getRestaurants().observe(getViewLifecycleOwner(), this::updateRestaurantsList);
            myViewModel.getRestaurantLikes().observe(getViewLifecycleOwner(), this::updateRestaurantLikesList);
        }
        return view;
    }
    private void updateRestaurantsList(List<Restaurant> restaurants) {
        Log.i("TestJoin", "RestaurantFragment: updateRestaurantsList");
        this.restaurants = restaurants;
        if (restaurants == null) return;
            for (Restaurant restaurant : restaurants) {
            Log.i("TestPlace", "RestaurantFragment: updateRestaurantsList location list retrieved = " + restaurant.getName());
        }
        Log.i("TestPlace", "RestaurantFragment: updateRestaurantsList end of location list retrieved");
        refresh(sortMode);
    }
    private void updateWorkmatesList(List<Workmate> workmates) {
        Log.i("TestWork", "RestaurantFragment: updateWorkmatesList");
        refresh(sortMode);
    }
    private void updateRestaurantLikesList(List<RestaurantLike> unused) {
        Log.i("TestLike", "RestaurantFragment: updateRestaurantLikesList");
        refresh(sortMode);
    }

    private SortMode refresh(SortMode s) {
        switch (s) {
            case NONE:
                break;
            case NAME:
                Collections.sort(restaurants, new CustomComparatorName());
                break;
            case DISTANCE:
                Collections.sort(restaurants, new CustomComparatorDistance());
                break;
        }
        recyclerView.setAdapter(new MyRestaurantRecyclerViewAdapter(restaurants, myViewModel));
        return s;
   }

}