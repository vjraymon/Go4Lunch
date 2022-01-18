package com.openclassrooms.go4lunch.ui;

import static android.content.Context.LOCATION_SERVICE;
import static com.google.android.libraries.places.api.model.Place.Field.NAME;
import static com.google.android.libraries.places.api.model.Place.Type.RESTAURANT;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.repositoy.RestaurantRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// Adaptation from https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial?hl=fr
public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
//    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;

    private Location lastKnownLocation;

    private static final int DEFAULT_ZOOM = 15;

    private boolean locationPermissionGranted = false;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

    private RestaurantRepository restaurantRepository;

    private LocationManager objgps;
    private LocationListener objlistener;

    /** Called when the activity is first created. */
    private void initGps() {
        //---utilisation  de la class LocationManager pour le gps---
        objgps = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(LOCATION_SERVICE);
        //*************ecouteur ou listener*********************
        objlistener = new Myobjlistener();
    }

    private class Myobjlistener implements LocationListener {
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

            //affichage des valeurs dans la les zone de saisie
            //           mTxtViewlat.setText(" "+location.getLatitude());
            //           mTxtViewlong.setText(" "+location.getLongitude());
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
            if (locationPermissionGranted)  {
 /*
                Log.i("TestPlace", "locationPermissionGranted");
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                Log.i("TestPlace", "addOnCompleteListener call");
                locationResult.addOnCompleteListener(Objects.requireNonNull(getActivity()), location -> {
                    Log.i("TestPlace", "onComplete");
                    if (locationResult.isSuccessful()) {
                        Log.i("TestPlace", "task.isSuccessful()");
                        lastKnownLocation = location.getResult();
                        if (lastKnownLocation != null) {
                            Log.i("TestPlace", "map.moveCamera");
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()),
                                    DEFAULT_ZOOM
                                    )
                            );
                            showCurrentPlace();
                        } else {
                            Log.i("TestPlace", "Current location is null");
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
*/
                objgps.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        10*1000,
                        5.0F,
                        objlistener);
            }
        } catch (SecurityException e) {
            Log.i("TestPlace", "Exception");
            Log.e("Exception: %s", e.getMessage(), e);
        }

    }
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity().setContentView(R.id.map);
        Log.i("TestPlace", "Places.initialize");
        Places.initialize(getContext(), getString(R.string.google_maps_key));
        Log.i("TestPlace", "Places.createClient");
        placesClient = Places.createClient(getActivity());
        Log.i("TestPlace", "getDeviceLocation");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        Log.i("TestPlace", "add fragment");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
*/

    private ActivityResultLauncher<String> permissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
//                Log.i("TestPlace", "ActivityResult " + result);
                locationPermissionGranted = result;
//                updateLocationUI();
            });

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
//            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            permissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }
*/
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
        restaurantRepository = new RestaurantRepository(getContext());
        //       Log.i("TestPlace", "getFusedLocationProviderClient");
        //       fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        Log.i("TestPlace", "add fragment");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
/*
    private void setRestaurantStyle() {
        Log.i("TestPlace", "setRestaurantStyle");
        try {
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json)
            );
            if (!success) {
                Log.e("TestPlace", "Style parsing failed");
            }
            Log.i("TestPlace", "end ok setRestaurantStyle");
        } catch (Resources.NotFoundException e) {
            Log.e("TestPlace", "Can't find style error : ", e);
        }
    }
*/
    List<Restaurant> restaurants = new ArrayList<>();
    private void showCurrentPlace() {
        if (map == null) {
            return;
        }
        if (locationPermissionGranted) {
            restaurantRepository.getRestaurants().observe(this, this::updateRestaurantsList);
        }
    }
    private void updateRestaurantsList(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        for (Restaurant restaurant : restaurants) {
            Log.i("TestPlace", "location retrieved = " + restaurant.getName());
        }
        Log.i("TestPlace", "end of location retrieved");
    }


}