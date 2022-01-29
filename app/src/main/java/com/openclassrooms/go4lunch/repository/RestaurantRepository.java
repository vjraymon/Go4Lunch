package com.openclassrooms.go4lunch.repository;

import static com.google.android.libraries.places.api.model.Place.Type.RESTAURANT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

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

    private final PlacesClient placesClient;
    private static final int M_MAX_ENTRIES = 10;

    public RestaurantRepository(Context context) {
        Log.i("TestPlace", "Places.initialize");
        Places.initialize(Objects.requireNonNull(context), context.getString(R.string.google_maps_key));
        Log.i("TestPlace", "Places.createClient");
        placesClient = Places.createClient(Objects.requireNonNull(context));
        getRestaurantsFromGooglePlace();
    }

    private final MutableLiveData<List<Restaurant>> restaurants = new MutableLiveData<>();

    public void select(List<Restaurant> item) { restaurants.setValue(item); }

    public LiveData<List<Restaurant>> getRestaurants() {
        return this.restaurants;
    }

    List<Restaurant> restaurantList = new ArrayList<>();

    private void getRestaurantByIdFromGooglePlace(String placeId) {
        // Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.OPENING_HOURS,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI,
                Place.Field.PHOTO_METADATAS,
                Place.Field.ICON_URL,
                Place.Field.LAT_LNG
        );

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            final Place place = response.getPlace();
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

            Log.i("TestDetailedPlace", "Place NAME found: " + place.getName());
            Log.i("TestDetailedPlace", "Place ADDRESS found: " + place.getAddress());
            Log.i("TestDetailedPlace", "Place OPENING_HOURS found: " + place.getOpeningHours());
            Log.i("TestDetailedPlace", "Place PHONE_NUMBER found: " + place.getPhoneNumber());
            Log.i("TestDetailedPlace", "Place WEBSITE_URI found: " + place.getWebsiteUri());
            // Get the photo metadata.
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w("TestDetailedPlace", "No photo metadata.");
                Restaurant restaurant = new Restaurant(
                        place.getName(),
                        Objects.requireNonNull(place.getAddress()).split(",")[0],
                        place.getLatLng(),
                        oh,
                        ws,
                        null,
                        null,
                        "+33 1 77 46 51 77"
                        //             place.getPhoneNumber()
                );
                restaurantList.add(restaurant);
                select(restaurantList);
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Get the attribution text.
            final String attributions = photoMetadata.getAttributions();

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
//                    .setMaxWidth(500) // Optional.
//                    .setMaxHeight(300) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                 Bitmap bitmap = fetchPhotoResponse.getBitmap();
                 Restaurant restaurant = new Restaurant(
                        place.getName(),
                        Objects.requireNonNull(place.getAddress()).split(",")[0],
                        place.getLatLng(),
                        oh,
                        ws,
                        bitmap,
                        place.getIconUrl(),
                        "+33 1 77 46 51 77"
                        //             place.getPhoneNumber()
                );
                restaurantList.add(restaurant);
                select(restaurantList);
//                imageView.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e("TestDetailedPlace", "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    // TODO: Handle error with given status code.
                }
            });
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e("TestDetailedPlace", "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });
    }


    private void getRestaurantsFromGooglePlace() {
        List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.TYPES
        );

        restaurantList = new ArrayList<>();

        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
        Log.i("TestPlace", "placesClient.findCurrentPlace");
        @SuppressLint("MissingPermission")
        final Task<FindCurrentPlaceResponse> placeResult = placesClient.findCurrentPlace(request);
        placeResult.addOnCompleteListener(
                (response) -> {
                    if (response.isSuccessful() && response.getResult() != null) {
                        FindCurrentPlaceResponse likelyPlaces = response.getResult();
                        int count = Math.min(likelyPlaces.getPlaceLikelihoods().size(), M_MAX_ENTRIES);
                        int i=0;
                        for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                            if (Objects.requireNonNull(placeLikelihood.getPlace().getTypes()).contains(RESTAURANT)) {
                                i++;
                                if (i>count) {
                                    break;
                                }
                                Log.i("TestPlace", "location i = " + i + " : " + placeLikelihood.getPlace().getName());
                                getRestaurantByIdFromGooglePlace(placeLikelihood.getPlace().getId());
                            }
                        }
                        select(restaurantList);
                    } else {
                        Log.i("TestPlace", "incorrect response");
                    }
                }
        );
    }



}
