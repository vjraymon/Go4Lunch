package com.openclassrooms.go4lunch.ui;

import static com.google.android.libraries.places.api.model.Place.Field.NAME;

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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    private Location lastKnownLocation;
    private LocationCallback locationCallback;

    private static final int DEFAULT_ZOOM = 15;
    private CameraPosition cameraPosition;

    private View mapView;

    private boolean locationPermissionGranted = false;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int M_MAX_ENTRIES = 5;

    // Gets the current location of the device
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                Log.i("TestPlace", "locationPermissionGranted");
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                Log.i("TestPlace", "addOnCompleteListener call");
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> location) {
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
                    }
                });

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
    private void getLocationPermission() {
        Log.i("TestPlace", "ActivityCompat.checkSelfPermission");
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            Log.i("TestPlace", "ActivityCompat.requestPermissions");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_FINE_LOCATION);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSION_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        getLocationPermission();
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
        View mapView = inflater.inflate(R.layout.fragment_maps, container, false);
        return mapView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
    private void showCurrentPlace() {
        if (map == null) {
            return;
        }
        if (locationPermissionGranted) {
            List<Place.Field> placeFields = Arrays.asList(
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG
            );
            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
            Log.i("TestPlace", "placesClient.findCurrentPlace");
            @SuppressLint("MissingPermission") final Task<FindCurrentPlaceResponse> placeResult = placesClient.findCurrentPlace(request);
            placeResult.addOnCompleteListener(
                    (response) -> {
                        if (response.isSuccessful() && response.getResult() != null) {
                            FindCurrentPlaceResponse likelyPlaces = response.getResult();
                            int count;
                            if (likelyPlaces.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                                count = likelyPlaces.getPlaceLikelihoods().size();
                            } else {
                                count = M_MAX_ENTRIES;
                            }
                            for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                                Log.i("TestPlace", "location = " + placeLikelihood.getPlace().getName());
                            }
                        } else {
                            Log.i("TestPlace", "incorrect response");
                        }
                    }
            );
        }
    }

}