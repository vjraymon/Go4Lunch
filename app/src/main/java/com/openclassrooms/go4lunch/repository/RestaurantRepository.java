package com.openclassrooms.go4lunch.repository;

import static com.google.android.libraries.places.api.model.Place.Type.RESTAURANT;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.model.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class RestaurantRepository {
    private final String TAG_PLACE = "TestPlace";

    private static RestaurantRepository service;

    /**
     * Get an instance on RestaurantRepository
     */
    public static RestaurantRepository getRestaurantRepository(Context context) {
        if (service == null) {
            service = new RestaurantRepository(context);
        }
        return service;
    }

    private static final int INIT_TODO = 0;
    private static final int INIT_ONGOING = 1;
    private static final int INIT_FAILED = 2;
    private static final int INIT_DONE = 3;
    private static int initRestaurantsStatus = INIT_TODO;
    private static int counter_request_ongoing = 0;

    private final PlacesClient placesClient;
    private static final int M_MAX_ENTRIES = 20;

    public RestaurantRepository(@NonNull Context context) {
        Log.i(TAG_PLACE, "RestaurantRepository.RestaurantRepository Places.initialize");
        Places.initialize(context, context.getString(R.string.google_maps_key));
        Log.i(TAG_PLACE, "RestaurantRepository.RestaurantRepository Places.createClient");
        placesClient = Places.createClient(Objects.requireNonNull(context));
        getRestaurantsFromGooglePlace(context);
    }

    private final MutableLiveData<List<Restaurant>> restaurants = new MutableLiveData<>();

    public void select(List<Restaurant> item) { restaurants.setValue(item); }

    public LiveData<List<Restaurant>> getRestaurants(Context context) {
        Log.i(TAG_PLACE, "RestaurantRepository.getRestaurants retry getRestaurantsFromGooglePlace");
        getRestaurantsFromGooglePlace(context);
        return this.restaurants;
    }

    List<Restaurant> restaurantList = new ArrayList<>();

    private void getRestaurantByIdFromGooglePlace(String placeId) {
        // Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.OPENING_HOURS,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI,
                Place.Field.PHOTO_METADATAS,
                Place.Field.LAT_LNG
        );

        // Construct a request object, passing the place ID and fields array.
        counter_request_ongoing = counter_request_ongoing +1;
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            final Place place = response.getPlace();

            Log.i("TestDetailedPlace", "Place NAME found: " + place.getName());
            Log.i("TestDetailedPlace", "Place ADDRESS found: " + place.getAddress());
            Log.i("TestDetailedPlace", "Place OPENING_HOURS found: " + place.getOpeningHours());
            Log.i("TestDetailedPlace", "Place PHONE_NUMBER found: " + place.getPhoneNumber());
            Log.i("TestDetailedPlace", "Place WEBSITE_URI found: " + place.getWebsiteUri());
            Log.i("TestDetailedPlace", "Place PLUS_CODE found: " + place.getPlusCode());

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            Log.i("TestDetailedPlace", "Place DAY found: " + day);
            int index = 0;
            switch (day) {
                case Calendar.SUNDAY:    index = 6; break;
                case Calendar.MONDAY:    index = 0; break;
                case Calendar.TUESDAY:   index = 1; break;
                case Calendar.WEDNESDAY: index = 2; break;
                case Calendar.THURSDAY:  index = 3; break;
                case Calendar.FRIDAY:    index = 4; break;
                case Calendar.SATURDAY:  index = 5; break;
            }
            final String oh = (place.getOpeningHours()==null) ? null : place.getOpeningHours().getWeekdayText().get(index);

            final String ws = (place.getWebsiteUri()==null) ? null : place.getWebsiteUri().toString();

            final String address = (place.getAddress()==null) ? null : place.getAddress().split(",")[0];

            // Get the photo metadata.
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w("TestDetailedPlace", "No photo metadata.");
                Restaurant restaurant = new Restaurant(
                        placeId,
                        place.getName(),
                        address,
                        place.getLatLng(),
                        oh,
                        ws,
                        null,
                        place.getPhoneNumber()
                );
                restaurantList.add(restaurant);
                counter_request_ongoing = counter_request_ongoing - 1;
                if (counter_request_ongoing <= 0) initRestaurantsStatus = INIT_DONE;
                select(restaurantList);
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);
            // Create a FetchPhotoRequest.
            counter_request_ongoing = counter_request_ongoing + 1;
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                Restaurant restaurant = new Restaurant(
                    placeId,
                    place.getName(),
                    address,
                    place.getLatLng(),
                    oh,
                    ws,
                    bitmap,
                    place.getPhoneNumber()
                );
                restaurantList.add(restaurant);
                counter_request_ongoing = counter_request_ongoing - 1;
                if (counter_request_ongoing <= 0) initRestaurantsStatus = INIT_DONE;
                select(restaurantList);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG_PLACE, "RestaurantRepository.getRestaurantByIdFromGooglePlace Photo not found: " + exception.getMessage());
                    initRestaurantsStatus = INIT_FAILED;
                }
            });
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG_PLACE, "RestaurantRepository.getRestaurantByIdFromGooglePlace Place not found: " + exception.getMessage());
                initRestaurantsStatus = INIT_FAILED;
            }
        });
    }


    private void getRestaurantsFromGooglePlace(Context context) {
        Log.i(TAG_PLACE, "RestaurantRepository.getRestaurantsFromGooglePlace");
        if ((initRestaurantsStatus == INIT_ONGOING) || (initRestaurantsStatus == INIT_DONE)) return;

        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.TYPES
        );

        restaurantList = new ArrayList<>();

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG_PLACE, "RestaurantRepository.getRestaurants location not permitted");
            return;
        }
        Log.i(TAG_PLACE, "RestaurantRepository.getRestaurantsFromGooglePlace: INIT_ONGOING");
        initRestaurantsStatus = INIT_ONGOING;
        final Task<FindCurrentPlaceResponse> placeResult = placesClient.findCurrentPlace(request);
        placeResult.addOnCompleteListener(
                (response) -> {
                    if (response.isSuccessful() && response.getResult() != null) {
                        FindCurrentPlaceResponse likelyPlaces = response.getResult();
                        int count = Math.min(likelyPlaces.getPlaceLikelihoods().size(), M_MAX_ENTRIES);
                        int i=0;
                        counter_request_ongoing = 0;
                        for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                            if ((placeLikelihood.getPlace().getTypes() != null) &&(placeLikelihood.getPlace().getTypes().contains(RESTAURANT))) {
                                i++;
                                if (i>count) {
                                    break;
                                }
                                Log.i("TestDetailedPlace", "RestaurantRepository.getRestaurantsFromGooglePlace location i = " + i + " : " + placeLikelihood.getPlace().getId());
                                getRestaurantByIdFromGooglePlace(placeLikelihood.getPlace().getId());
                                Log.i("TestDetailedPlace", "Place TYPES found: " + placeLikelihood.getPlace().getTypes());
                            }
                        }
                        if (i == 0) {
                            Log.i(TAG_PLACE, "RestaurantRepository.getRestaurantsFromGooglePlace: no restaurant found");
                            initRestaurantsStatus = INIT_DONE;
                            select(restaurantList); //no restaurant found
                        }
                    } else {
                        Log.i(TAG_PLACE, "RestaurantRepository.getRestaurantsFromGooglePlace incorrect response on FindCurrentPlaceResponse");
                        initRestaurantsStatus = INIT_FAILED;
                    }
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG_PLACE, "RestaurantRepository.getRestaurantsFromGooglePlace Error on FindCurrentPlaceResponse" + exception.getMessage());
                        final int statusCode = apiException.getStatusCode();
                        initRestaurantsStatus = INIT_FAILED;
                    }
                });
    }
}
