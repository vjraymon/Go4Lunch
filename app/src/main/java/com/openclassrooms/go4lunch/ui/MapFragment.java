package com.openclassrooms.go4lunch.ui;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.events.DisplayRestaurantEvent;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.viewmodel.MyViewModel;
import com.openclassrooms.go4lunch.viewmodel.MyViewModelFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

// Adaptation from https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial?hl=fr
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;

    MyViewModel myViewModel;

    private static Location lastKnownLocation = null;

    public static Location getLastKnownLocation() { return lastKnownLocation; }

    private static final int DEFAULT_ZOOM = 17;

    private boolean locationPermissionGranted = false;

    private LocationManager objGps;
    private LocationListener objListener;

    /**
     * Called when the activity is first created.
     */
    private void initGps() {
        objGps = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(LOCATION_SERVICE);
        objListener = new MyObjListener();
    }

    private class MyObjListener implements LocationListener {
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
            // TODO Auto-generated method stub
        }

        public void onLocationChanged(Location location) {
            lastKnownLocation = location;
            if (lastKnownLocation != null) {
                Log.i("TestPlace", "map.moveCamera");
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastKnownLocation.getLatitude(),
                                lastKnownLocation.getLongitude()),
                        DEFAULT_ZOOM
                        )
                );
                showCurrentPlace();
            }
        }
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                 objGps.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        60 * 1000,
                        10.0F,
                         objListener);
            }
        } catch (SecurityException e) {
            Log.i("TestPlace", "Exception");
            Log.e("Exception: %s", e.getMessage(), e);
        }

    }

    private final ActivityResultLauncher<String> permissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> locationPermissionGranted = result
            );

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            permissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.clear();
        initGps();
        Log.i("TestPlace", "first getLocationPermission()");
        getLocationPermission();
        while (!locationPermissionGranted) {
            updateLocationUI();
        }
        Log.i("TestPlace", "last updateLocationUI()");
        updateLocationUI();
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        getDeviceLocation();
        setHasOptionsMenu(true);
        if ((getContext() != null) && (!Places.isInitialized())) Places.initialize(getContext(), getString(R.string.google_maps_key));
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                // button focus on the map
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.i("TestPlace", "exception");
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private final static String TAG = "TestSearch";

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.app_bar_search) {
            onSearchCalled();
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) getActivity().finish();
            return true;
        }
        return false;
    }

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myViewModel = new ViewModelProvider(this, new MyViewModelFactory(Objects.requireNonNull(this.getActivity()).getApplication())).get(MyViewModel.class);

        Log.i("TestPlace", "add fragment");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    List<Restaurant> restaurants = new ArrayList<>();
//    List<Workmate> workmates = new ArrayList<>();
    boolean isRestaurantsInitialized;
    boolean isWorkmatesInitialized;

    private void showCurrentPlace() {
        Log.i("TestMarker", "MapsFragment.showCurrentPlace");
        if (map == null) {
            return;
        }
        if (locationPermissionGranted) {
            //          restaurantRepository.getRestaurants().observe(this, this::updateRestaurantsList);
            isRestaurantsInitialized = false;
            isWorkmatesInitialized = false;
            myViewModel.getRestaurants().observe(this, this::updateRestaurantsList);
            // Mandatory to color the restaurant pins depending of attendees
            myViewModel.getWorkmates().observe(this, this::updateWorkmatesList);
            map.setOnMarkerClickListener(marker -> {
                Log.i("TestMarker", "MapsFragment.showCurrentPlace OnMarkerClickListener");
                Restaurant restaurant = myViewModel.getRestaurantByLatLng(marker.getPosition());
                EventBus.getDefault().post(new DisplayRestaurantEvent(restaurant));
                Log.i("TestMarker", "MapsFragment.showCurrentPlace OnMarkerClickListener " + restaurant.getName() + " " + restaurant.getId());
                return false;
            });
        }
    }

    private void updateRestaurantsList(List<Restaurant> restaurants) {
        Log.i("TestMarker", "MapsFragment.updateRestaurantsList");
        if (myViewModel.isGetRestaurantsFailure()) {
            Log.e("TestMarker", "MapsFragment.updateWorkmatesList: handle getRestaurants exception");
            myViewModel.getRestaurants();
            return;
        }
        this.restaurants = restaurants;
        isRestaurantsInitialized = true;
        if (isWorkmatesInitialized) {
            initializeMarkers();
        }
    }

    private void updateWorkmatesList(List<Workmate> workmates) {
        Log.i("TestMarker", "MapsFragment.updateWorkmatesList");
//        this.workmates = workmates;
        isWorkmatesInitialized = true;
        if (isRestaurantsInitialized) {
            initializeMarkers();
        }
    }

    private void initializeMarkers() {
        Log.i("TestMarker", "MapsFragment.initializeMarkers");
        for (Restaurant i : restaurants) {
            initializeMarker(i);
        }
    }

    private void initializeMarker(Restaurant restaurant) {
        if ((restaurant == null) || (restaurant.getLatLng() == null)) {
            Log.i("TestMarker", "MapsFragment.initializeMarker restaurant not found");
            return;
        }
        Log.i("TestMarker", "MapsFragment.initializeMarker restaurant = " + restaurant.getName() + " " + restaurant.getId());
        List<Workmate> currentWorkmateList = myViewModel.getWorkmatesByIdRestaurant(restaurant.getId());
//        float color;
        int pinRestaurant;
        if ((currentWorkmateList == null) || currentWorkmateList.isEmpty()) {
            Log.i("TestMarker", "MapsFragment.initializeMarker red " + restaurant.getName());
//            color = BitmapDescriptorFactory.HUE_RED;
            pinRestaurant = R.drawable.ic_pin_restaurant2_orange;
        } else {
            Log.i("TestMarker", "MapsFragment.initializeMarker green " + restaurant.getName());
//            color = BitmapDescriptorFactory.HUE_GREEN;
            pinRestaurant = R.drawable.ic_pin_restaurant2_green;
        }

        Marker marker = map.addMarker(new MarkerOptions()
                    .position(restaurant.getLatLng())
                    .title(restaurant.getName())
// TODO Find the good pin dimensions
  .icon(BitmapDescriptorFactory.fromResource(pinRestaurant)));
//                    .icon(BitmapDescriptorFactory.defaultMarker(color)));
        // To refresh the display of the marker
        if (marker != null) {
            marker.hideInfoWindow();
            marker.showInfoWindow();
        }

        Log.i("TestMarker","MapsFragment.initializeMarker end");
    }
}