package com.openclassrooms.go4lunch.ui;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.events.DisplayRestaurantEvent;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.viewmodel.MyViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Adaptation from https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial?hl=fr
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private PlacesClient placesClient;

    MyViewModel myViewModel;

    private Location lastKnownLocation;

    private static final int DEFAULT_ZOOM = 17;

    private boolean locationPermissionGranted = false;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

//    private RestaurantRepository restaurantRepository;

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
            Log.i("Exception: %s", e.getMessage());
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

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        Log.i("TestPlace", "add fragment");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    List<Restaurant> restaurants = new ArrayList<>();
    List<Workmate> workmates = new ArrayList<>();
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
            myViewModel.getRestaurants().observeForever(this::updateRestaurantsList);
            myViewModel.getWorkmates().observeForever(this::updateWorkmatesList);
            map.setOnMarkerClickListener(marker -> {
                Log.i("TestMarker", "MapsFragment.showCurrentPlace OnMarkerClickListener");
                Restaurant restaurant = myViewModel.getRestaurantByLatLng(marker.getPosition());
                EventBus.getDefault().post(new DisplayRestaurantEvent(restaurant));
                return false;
            });
        }
    }

    private void updateRestaurantsList(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        isRestaurantsInitialized = true;
        if (isWorkmatesInitialized) {
            initializeMarkers();
        }
    }

    private void updateWorkmatesList(List<Workmate> workmates) {
        this.workmates = workmates;
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
        Log.i("TestMarker", "MapsFragment.initializeMarker restaurant = " + restaurant.getName());
        List<Workmate> currentWorkmateList = myViewModel.getWorkmatesByLatLng(restaurant.getLatLng());
        float color;
        int pinRestaurant;
        if ((currentWorkmateList == null) || currentWorkmateList.isEmpty()) {
            color = BitmapDescriptorFactory.HUE_RED;
            pinRestaurant = R.drawable.ic_pin_restaurant;
        } else {
            color = BitmapDescriptorFactory.HUE_GREEN;
            pinRestaurant = R.drawable.ic_pin_restaurant_green;
        }

        Marker marker = map.addMarker(new MarkerOptions()
                    .position(restaurant.getLatLng())
                    .title(restaurant.getName())
// TODO Find the good pin dimensions
  .icon(BitmapDescriptorFactory.fromResource(pinRestaurant)));
//                    .icon(BitmapDescriptorFactory.defaultMarker(color)));

        Log.i("TestMarker","MapsFragment.initializeMarker end");
    }
}