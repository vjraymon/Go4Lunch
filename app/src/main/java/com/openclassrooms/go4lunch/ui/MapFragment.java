package com.openclassrooms.go4lunch.ui;

import static android.content.Context.LOCATION_SERVICE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
    //    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;

    private Location lastKnownLocation;

    private static final int DEFAULT_ZOOM = 17;

    private boolean locationPermissionGranted = false;
    private static final int PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 1;

//    private RestaurantRepository restaurantRepository;

    private LocationManager objgps;
    private LocationListener objlistener;

    /**
     * Called when the activity is first created.
     */
    private void initGps() {
        objgps = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(LOCATION_SERVICE);
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
                        60 * 1000,
                        10.0F,
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

    MyViewModel myViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        restaurantRepository = RestaurantRepository.getRestaurantRepository(getContext());
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
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
            myViewModel.getRestaurants().observe(this, this::updateRestaurantsList);
            myViewModel.getWorkmates().observe(this, this::updateWorkmatesList);
            map.setOnMarkerClickListener(marker -> {
                Log.i("TestMarker", "MapsFragment.showCurrentPlace OnMarkerClickListener");
                Restaurant restaurant = myViewModel.getRestaurantByLatLng(marker.getPosition());
                EventBus.getDefault().post(new DisplayRestaurantEvent(restaurant));
//                initializeMarker(marker);
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
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        Log.i("TestMarker", "MapsFragment.initializeMarkers");
 /*
        this.map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                Log.i("TestMarker", "MapsFragment.setInfoWindowAdapter.getInfoWindow");
                initializeMarker(arg0);
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
/*
                // Inflate the layouts for the info window, title and snippet.

                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;

                Log.i("TestMarker", "MapsFragment.setInfoWindowAdapter.getInfoContents");
                initializeMarker(marker);
                return null;
            }
        });
*/
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
        if ((currentWorkmateList == null) || currentWorkmateList.isEmpty()) {
            color = BitmapDescriptorFactory.HUE_RED;
        } else {
            color = BitmapDescriptorFactory.HUE_GREEN;
        }

        map.addMarker(new MarkerOptions()
                    .position(restaurant.getLatLng())
                    .title(restaurant.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(color)));

        Log.i("TestMarker","MapsFragment.initializeMarker end");
    }

    private void initializeMarker(Marker marker) {
        Restaurant restaurant = myViewModel.getRestaurantByLatLng(marker.getPosition());
        if ((restaurant == null) || (restaurant.getLatLng() == null)) {
            Log.i("TestMarker", "MapsFragment.initializeMarker restaurant not found");
            return;
        }
            Log.i("TestMarker", "MapsFragment.initializeMarker restaurant = " + restaurant.getName());
            List<Workmate> currentWorkmateList = myViewModel.getWorkmatesByLatLng(restaurant.getLatLng());
            float color;
            if ((currentWorkmateList == null) || currentWorkmateList.isEmpty()) {
                color = BitmapDescriptorFactory.HUE_RED;
            } else {
                color = BitmapDescriptorFactory.HUE_GREEN;
            }
/*
            Marker currentMarker = map.addMarker(new MarkerOptions()
                    .position(restaurant.getLatLng())
                    .title(restaurant.getName())
            );
*/
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(color));

        Log.i("TestMarker","MapsFragment.initializeMarker end");
    }
}