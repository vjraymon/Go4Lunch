package com.openClassrooms.go4lunch.repository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.openClassrooms.go4lunch.model.Restaurant;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantRepositoryTest {
    Application myApplication;
    RestaurantRepository t;

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    LiveData<List<Restaurant>> restaurants;

    @Mock
    PlacesClient placesClient;

    @Captor
    ArgumentCaptor<FindCurrentPlaceRequest> request;

    @Test
    public void constructorAccessFineLocationNotGranted() {
        myApplication = new Application();
        t = new RestaurantRepository(myApplication.getApplicationContext(), placesClient, false);

        // Check if findCurrentPlace not called
        verify(placesClient, never()).findCurrentPlace(any());
        assertNotNull(t); // Mandatory to avoid crash at the start
        RestaurantRepository.InitRestaurantStatus status = t.getInitRestaurantsStatus();
        assertEquals(RestaurantRepository.InitRestaurantStatus.INIT_TODO, status);
    }

    @Mock
    Task<FindCurrentPlaceResponse> task;
    @Mock
    FindCurrentPlaceResponse placeResult;
    @Mock
    PlaceLikelihood placeLikelihood1, placeLikelihood2;
    @Mock
    Place place1, place2;
    @Captor
    ArgumentCaptor<OnCompleteListener<FindCurrentPlaceResponse>> eventOnCompleteListener;
    @Captor
    ArgumentCaptor<OnSuccessListener<FetchPlaceResponse>> eventOnSuccessListener, eventOnSuccessListener2;
    @Captor
    ArgumentCaptor<OnFailureListener> eventOnFailureListener, eventOnFailureListener2;

    @Test
    public void constructorFindCurrentPlaceReturnException() {
        myApplication = new Application();
        when(placesClient.findCurrentPlace(any())).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        t = new RestaurantRepository(myApplication.getApplicationContext(), placesClient, true);

        assertNotNull(t);

        RestaurantRepository.InitRestaurantStatus status;
            verify(placesClient).findCurrentPlace(request.capture());
            assertNotNull(request);
            assertNotNull(request.getValue());
            assertNotNull(request.getValue().getPlaceFields());
            assertEquals(2, request.getValue().getPlaceFields().size());
            assertEquals(Place.Field.ID, request.getValue().getPlaceFields().get(0));
            assertEquals(Place.Field.TYPES, request.getValue().getPlaceFields().get(1));
            verify(task).addOnCompleteListener(eventOnCompleteListener.capture());
            verify(task).addOnFailureListener(eventOnFailureListener.capture());
            assertNotNull(eventOnCompleteListener.getValue());
            assertNotNull(eventOnFailureListener.getValue());
            when(placesClient.findCurrentPlace(any())).thenReturn(task);
            when(task.addOnCompleteListener(any())).thenReturn(task);
            when(task.addOnFailureListener(any())).thenReturn(task);
            eventOnFailureListener.getValue().onFailure(new Exception("Exception"));

            status = t.getInitRestaurantsStatus();
        assertEquals(RestaurantRepository.InitRestaurantStatus.INIT_ONGOING, status);

        verify(placesClient, times(2)).findCurrentPlace(request.capture());
        assertNotNull(request);
        assertNotNull(request.getValue());
        assertNotNull(request.getValue().getPlaceFields());
        assertEquals(2, request.getValue().getPlaceFields().size());
        assertEquals(Place.Field.ID, request.getValue().getPlaceFields().get(0));
        assertEquals(Place.Field.TYPES, request.getValue().getPlaceFields().get(1));
        verify(task, times(2)).addOnCompleteListener(eventOnCompleteListener.capture());
        verify(task, times(2)).addOnFailureListener(eventOnFailureListener.capture());
        assertNotNull(eventOnCompleteListener.getValue());
        assertNotNull(eventOnFailureListener.getValue());
        when(placesClient.findCurrentPlace(any())).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        eventOnFailureListener.getValue().onFailure(new Exception("Exception"));

        status = t.getInitRestaurantsStatus();
        assertEquals(RestaurantRepository.InitRestaurantStatus.INIT_ONGOING, status);

        verify(placesClient, times(3)).findCurrentPlace(request.capture());
        assertNotNull(request);
        assertNotNull(request.getValue());
        assertNotNull(request.getValue().getPlaceFields());
        assertEquals(2, request.getValue().getPlaceFields().size());
        assertEquals(Place.Field.ID, request.getValue().getPlaceFields().get(0));
        assertEquals(Place.Field.TYPES, request.getValue().getPlaceFields().get(1));
        verify(task, times(3)).addOnCompleteListener(eventOnCompleteListener.capture());
        verify(task, times(3)).addOnFailureListener(eventOnFailureListener.capture());
        assertNotNull(eventOnCompleteListener.getValue());
        assertNotNull(eventOnFailureListener.getValue());
        eventOnFailureListener.getValue().onFailure(new Exception("Exception"));

        status = t.getInitRestaurantsStatus();

        assertEquals(RestaurantRepository.InitRestaurantStatus.INIT_FAILED, status);
    }

    @Test
    public void getRestaurantsException() {
        constructorAccessFineLocationNotGranted();

        when(placesClient.findCurrentPlace(any())).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        restaurants = t.getRestaurants(myApplication.getApplicationContext());

        assertNotNull(restaurants);

        RestaurantRepository.InitRestaurantStatus status;
        verify(placesClient).findCurrentPlace(request.capture());
        assertNotNull(request);
        assertNotNull(request.getValue());
        assertNotNull(request.getValue().getPlaceFields());
        assertEquals(2, request.getValue().getPlaceFields().size());
        assertEquals(Place.Field.ID, request.getValue().getPlaceFields().get(0));
        assertEquals(Place.Field.TYPES, request.getValue().getPlaceFields().get(1));
        verify(task).addOnCompleteListener(eventOnCompleteListener.capture());
        verify(task).addOnFailureListener(eventOnFailureListener.capture());
        assertNotNull(eventOnCompleteListener.getValue());
        assertNotNull(eventOnFailureListener.getValue());
        when(placesClient.findCurrentPlace(any())).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        eventOnFailureListener.getValue().onFailure(new Exception("Exception"));

        status = t.getInitRestaurantsStatus();
        assertEquals(RestaurantRepository.InitRestaurantStatus.INIT_ONGOING, status);

        verify(placesClient, times(2)).findCurrentPlace(request.capture());
        assertNotNull(request);
        assertNotNull(request.getValue());
        assertNotNull(request.getValue().getPlaceFields());
        assertEquals(2, request.getValue().getPlaceFields().size());
        assertEquals(Place.Field.ID, request.getValue().getPlaceFields().get(0));
        assertEquals(Place.Field.TYPES, request.getValue().getPlaceFields().get(1));
        verify(task, times(2)).addOnCompleteListener(eventOnCompleteListener.capture());
        verify(task, times(2)).addOnFailureListener(eventOnFailureListener.capture());
        assertNotNull(eventOnCompleteListener.getValue());
        assertNotNull(eventOnFailureListener.getValue());
        when(placesClient.findCurrentPlace(any())).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        eventOnFailureListener.getValue().onFailure(new Exception("Exception"));

        status = t.getInitRestaurantsStatus();
        assertEquals(RestaurantRepository.InitRestaurantStatus.INIT_ONGOING, status);

        verify(placesClient, times(3)).findCurrentPlace(request.capture());
        assertNotNull(request);
        assertNotNull(request.getValue());
        assertNotNull(request.getValue().getPlaceFields());
        assertEquals(2, request.getValue().getPlaceFields().size());
        assertEquals(Place.Field.ID, request.getValue().getPlaceFields().get(0));
        assertEquals(Place.Field.TYPES, request.getValue().getPlaceFields().get(1));
        verify(task, times(3)).addOnCompleteListener(eventOnCompleteListener.capture());
        verify(task, times(3)).addOnFailureListener(eventOnFailureListener.capture());
        assertNotNull(eventOnCompleteListener.getValue());
        assertNotNull(eventOnFailureListener.getValue());

        eventOnFailureListener.getValue().onFailure(new Exception("Exception"));

        status = t.getInitRestaurantsStatus();

        assertEquals(RestaurantRepository.InitRestaurantStatus.INIT_FAILED, status);
        assertNotNull(restaurants);
        assertNull(restaurants.getValue());
    }

    @Test
    public void getRestaurantsError() {
        constructorAccessFineLocationNotGranted();

        when(placesClient.findCurrentPlace(any())).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        restaurants = t.getRestaurants(myApplication.getApplicationContext());

        assertNotNull(restaurants);

        RestaurantRepository.InitRestaurantStatus status;
        verify(placesClient).findCurrentPlace(request.capture());
        assertNotNull(request);
        assertNotNull(request.getValue());
        assertNotNull(request.getValue().getPlaceFields());
        assertEquals(2, request.getValue().getPlaceFields().size());
        assertEquals(Place.Field.ID, request.getValue().getPlaceFields().get(0));
        assertEquals(Place.Field.TYPES, request.getValue().getPlaceFields().get(1));
        verify(task).addOnCompleteListener(eventOnCompleteListener.capture());
        verify(task).addOnFailureListener(eventOnFailureListener.capture());
        assertNotNull(eventOnCompleteListener.getValue());
        assertNotNull(eventOnFailureListener.getValue());
        when(task.isSuccessful()).thenReturn(false);
        eventOnCompleteListener.getValue().onComplete(task);

        status = t.getInitRestaurantsStatus();

        assertEquals(RestaurantRepository.InitRestaurantStatus.INIT_FAILED, status);
        assertNotNull(restaurants);
        assertNull(restaurants.getValue());
    }

    @Test
    public void getRestaurantsNoResult() {
        constructorAccessFineLocationNotGranted();

        when(placesClient.findCurrentPlace(any())).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        restaurants = t.getRestaurants(myApplication.getApplicationContext());

        assertNotNull(restaurants);

        RestaurantRepository.InitRestaurantStatus status;
        verify(placesClient).findCurrentPlace(request.capture());
        assertNotNull(request);
        assertNotNull(request.getValue());
        assertNotNull(request.getValue().getPlaceFields());
        assertEquals(2, request.getValue().getPlaceFields().size());
        assertEquals(Place.Field.ID, request.getValue().getPlaceFields().get(0));
        assertEquals(Place.Field.TYPES, request.getValue().getPlaceFields().get(1));
        verify(task).addOnCompleteListener(eventOnCompleteListener.capture());
        verify(task).addOnFailureListener(eventOnFailureListener.capture());
        assertNotNull(eventOnCompleteListener.getValue());
        assertNotNull(eventOnFailureListener.getValue());
        when(task.isSuccessful()).thenReturn(true);
        when(task.getResult()).thenReturn(null);
        eventOnCompleteListener.getValue().onComplete(task);

        status = t.getInitRestaurantsStatus();

        assertEquals(RestaurantRepository.InitRestaurantStatus.INIT_FAILED, status);
        assertNotNull(restaurants);
        assertNull(restaurants.getValue());
    }

    @Test
    public void getRestaurantsNoPlace() {
        constructorAccessFineLocationNotGranted();

        when(placesClient.findCurrentPlace(any())).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        restaurants = t.getRestaurants(myApplication.getApplicationContext());

        assertNotNull(restaurants);

        RestaurantRepository.InitRestaurantStatus status;
        verify(placesClient).findCurrentPlace(request.capture());
        assertNotNull(request);
        assertNotNull(request.getValue());
        assertNotNull(request.getValue().getPlaceFields());
        assertEquals(2, request.getValue().getPlaceFields().size());
        assertEquals(Place.Field.ID, request.getValue().getPlaceFields().get(0));
        assertEquals(Place.Field.TYPES, request.getValue().getPlaceFields().get(1));
        verify(task).addOnCompleteListener(eventOnCompleteListener.capture());
        verify(task).addOnFailureListener(eventOnFailureListener.capture());
        assertNotNull(eventOnCompleteListener.getValue());
        assertNotNull(eventOnFailureListener.getValue());
        when(task.isSuccessful()).thenReturn(true);
        when(task.getResult()).thenReturn(placeResult);
        List<PlaceLikelihood> placesList = new ArrayList<>();
        when(placeResult.getPlaceLikelihoods()).thenReturn(placesList);
        eventOnCompleteListener.getValue().onComplete(task);

        status = t.getInitRestaurantsStatus();

        assertEquals(RestaurantRepository.InitRestaurantStatus.INIT_DONE, status);
        assertNotNull(restaurants);
        assertNotNull(restaurants.getValue());
        assertTrue(restaurants.getValue().isEmpty());
    }

    @Mock
    Task<FetchPlaceResponse> taskFetch, taskFetch2;
    @Mock
    FetchPlaceResponse fetchResponse1, fetchResponse2;

    @Captor
    ArgumentCaptor<FetchPlaceRequest> fetchRequest1;

    @Test
    public void getRestaurants1RecordWithExceptionOnFetch() {
        constructorAccessFineLocationNotGranted();

        when(placesClient.findCurrentPlace(any())).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        restaurants = t.getRestaurants(myApplication.getApplicationContext());

        assertNotNull(restaurants);

        verify(placesClient).findCurrentPlace(request.capture());
        assertNotNull(request);
        assertNotNull(request.getValue());
        assertNotNull(request.getValue().getPlaceFields());
        assertEquals(2, request.getValue().getPlaceFields().size());
        assertEquals(Place.Field.ID, request.getValue().getPlaceFields().get(0));
        assertEquals(Place.Field.TYPES, request.getValue().getPlaceFields().get(1));
        verify(task).addOnCompleteListener(eventOnCompleteListener.capture());
        verify(task).addOnFailureListener(eventOnFailureListener.capture());
        assertNotNull(eventOnCompleteListener.getValue());
        assertNotNull(eventOnFailureListener.getValue());
        when(task.isSuccessful()).thenReturn(true);
        when(task.getResult()).thenReturn(placeResult);
        List<PlaceLikelihood> placesList = new ArrayList<>();
        placesList.add(placeLikelihood1);
        when(place1.getTypes()).thenReturn(Collections.singletonList(Place.Type.RESTAURANT));
        when(place1.getId()).thenReturn("LaScalaReference");
        when(placeLikelihood1.getPlace()).thenReturn(place1);
        when(placeResult.getPlaceLikelihoods()).thenReturn(placesList);

        // getRestaurantByIdFromGooglePlace parts
        when(placesClient.fetchPlace(any())).thenReturn(taskFetch);
        when(taskFetch.addOnSuccessListener(any())).thenReturn(taskFetch);
        when(taskFetch.addOnFailureListener(any())).thenReturn(taskFetch);
        //trigger from getRestaurantFromGooglePlace
        eventOnCompleteListener.getValue().onComplete(task);

        // check getRestaurantByIdFromGooglePlace until next trigger
        verify(placesClient).fetchPlace(fetchRequest1.capture());
        assertNotNull(fetchRequest1);
        assertNotNull(fetchRequest1.getValue());
        assertNotNull(fetchRequest1.getValue().getPlaceId());
        assertEquals("LaScalaReference", fetchRequest1.getValue().getPlaceId());
        assertNotNull(fetchRequest1.getValue().getPlaceFields());
        assertEquals(7, fetchRequest1.getValue().getPlaceFields().size());
        assertEquals(Place.Field.NAME, fetchRequest1.getValue().getPlaceFields().get(0));
        assertEquals(Place.Field.ADDRESS, fetchRequest1.getValue().getPlaceFields().get(1));
        assertEquals(Place.Field.OPENING_HOURS, fetchRequest1.getValue().getPlaceFields().get(2));
        assertEquals(Place.Field.PHONE_NUMBER, fetchRequest1.getValue().getPlaceFields().get(3));
        assertEquals(Place.Field.WEBSITE_URI, fetchRequest1.getValue().getPlaceFields().get(4));
        assertEquals(Place.Field.PHOTO_METADATAS, fetchRequest1.getValue().getPlaceFields().get(5));
        assertEquals(Place.Field.LAT_LNG, fetchRequest1.getValue().getPlaceFields().get(6));
        verify(taskFetch).addOnSuccessListener(eventOnSuccessListener.capture());
        verify(taskFetch).addOnFailureListener(eventOnFailureListener.capture());
        assertNotNull(eventOnSuccessListener.getValue());
        assertNotNull(eventOnFailureListener.getValue());
        eventOnFailureListener.getValue().onFailure(new Exception("exception"));

        RestaurantRepository.InitRestaurantStatus status = t.getInitRestaurantsStatus();

        assertEquals(RestaurantRepository.InitRestaurantStatus.INIT_FAILED, status);
        assertNotNull(restaurants);
        assertNull(restaurants.getValue());
    }

    @Test
    public void getRestaurants1RecordAndFetch() {
        constructorAccessFineLocationNotGranted();

        when(placesClient.findCurrentPlace(any())).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        restaurants = t.getRestaurants(myApplication.getApplicationContext());

        assertNotNull(restaurants);

        verify(placesClient).findCurrentPlace(request.capture());
        assertNotNull(request);
        assertNotNull(request.getValue());
        assertNotNull(request.getValue().getPlaceFields());
        assertEquals(2, request.getValue().getPlaceFields().size());
        assertEquals(Place.Field.ID, request.getValue().getPlaceFields().get(0));
        assertEquals(Place.Field.TYPES, request.getValue().getPlaceFields().get(1));
        verify(task).addOnCompleteListener(eventOnCompleteListener.capture());
        verify(task).addOnFailureListener(eventOnFailureListener.capture());
        assertNotNull(eventOnCompleteListener.getValue());
        assertNotNull(eventOnFailureListener.getValue());
        when(task.isSuccessful()).thenReturn(true);
        when(task.getResult()).thenReturn(placeResult);
        List<PlaceLikelihood> placesList = new ArrayList<>();
        placesList.add(placeLikelihood1);
        when(place1.getTypes()).thenReturn(Collections.singletonList(Place.Type.RESTAURANT));
        when(place1.getId()).thenReturn("LaScalaReference");
        when(placeLikelihood1.getPlace()).thenReturn(place1);
        when(placeResult.getPlaceLikelihoods()).thenReturn(placesList);

        // getRestaurantByIdFromGooglePlace parts
        when(placesClient.fetchPlace(any())).thenReturn(taskFetch);
        when(taskFetch.addOnSuccessListener(any())).thenReturn(taskFetch);
        when(taskFetch.addOnFailureListener(any())).thenReturn(taskFetch);
        //trigger from getRestaurantFromGooglePlace
        eventOnCompleteListener.getValue().onComplete(task);

        // check getRestaurantByIdFromGooglePlace until next trigger
        verify(placesClient).fetchPlace(fetchRequest1.capture());
        assertNotNull(fetchRequest1);
        assertNotNull(fetchRequest1.getValue());
        assertNotNull(fetchRequest1.getValue().getPlaceId());
        assertEquals("LaScalaReference", fetchRequest1.getValue().getPlaceId());
        assertNotNull(fetchRequest1.getValue().getPlaceFields());
        assertEquals(7, fetchRequest1.getValue().getPlaceFields().size());
        assertEquals(Place.Field.NAME, fetchRequest1.getValue().getPlaceFields().get(0));
        assertEquals(Place.Field.ADDRESS, fetchRequest1.getValue().getPlaceFields().get(1));
        assertEquals(Place.Field.OPENING_HOURS, fetchRequest1.getValue().getPlaceFields().get(2));
        assertEquals(Place.Field.PHONE_NUMBER, fetchRequest1.getValue().getPlaceFields().get(3));
        assertEquals(Place.Field.WEBSITE_URI, fetchRequest1.getValue().getPlaceFields().get(4));
        assertEquals(Place.Field.PHOTO_METADATAS, fetchRequest1.getValue().getPlaceFields().get(5));
        assertEquals(Place.Field.LAT_LNG, fetchRequest1.getValue().getPlaceFields().get(6));
        verify(taskFetch).addOnSuccessListener(eventOnSuccessListener.capture());
        verify(taskFetch).addOnFailureListener(eventOnFailureListener.capture());
        assertNotNull(eventOnSuccessListener.getValue());
        assertNotNull(eventOnFailureListener.getValue());
        when (place1.getName()).thenReturn("La Scala");
        when (place1.getAddress()).thenReturn(null);  // TODO to mock
        when (place1.getOpeningHours()).thenReturn(null); // TODO to mock
        when (place1.getPhoneNumber()).thenReturn("01 77 46 51 77");
        when (place1.getWebsiteUri()).thenReturn(null); // TODO to mock
        when(fetchResponse1.getPlace()).thenReturn(place1);
        eventOnSuccessListener.getValue().onSuccess(fetchResponse1);

        RestaurantRepository.InitRestaurantStatus status = t.getInitRestaurantsStatus();

        assertEquals(RestaurantRepository.InitRestaurantStatus.INIT_DONE, status);
        assertNotNull(restaurants);
        assertNotNull(restaurants.getValue());
        assertEquals(1, restaurants.getValue().size());
        assertEquals("LaScalaReference", restaurants.getValue().get(0).getId());
        assertEquals("La Scala", restaurants.getValue().get(0).getName());
        assertEquals("01 77 46 51 77", restaurants.getValue().get(0).getPhoneNumber());
    }

    @Test
    public void getRestaurants2RecordsAndFetch() {
        constructorAccessFineLocationNotGranted();

        when(placesClient.findCurrentPlace(any())).thenReturn(task);
        when(task.addOnCompleteListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        restaurants = t.getRestaurants(myApplication.getApplicationContext());

        assertNotNull(restaurants);

        verify(placesClient).findCurrentPlace(request.capture());
        assertNotNull(request);
        assertNotNull(request.getValue());
        assertNotNull(request.getValue().getPlaceFields());
        assertEquals(2, request.getValue().getPlaceFields().size());
        assertEquals(Place.Field.ID, request.getValue().getPlaceFields().get(0));
        assertEquals(Place.Field.TYPES, request.getValue().getPlaceFields().get(1));
        verify(task).addOnCompleteListener(eventOnCompleteListener.capture());
        verify(task).addOnFailureListener(eventOnFailureListener.capture());
        assertNotNull(eventOnCompleteListener.getValue());
        assertNotNull(eventOnFailureListener.getValue());
        when(task.isSuccessful()).thenReturn(true);
        when(task.getResult()).thenReturn(placeResult);
        List<PlaceLikelihood> placesList = new ArrayList<>();
        placesList.add(placeLikelihood1);
        placesList.add(placeLikelihood2);
        when(place1.getTypes()).thenReturn(Collections.singletonList(Place.Type.RESTAURANT));
        when(place1.getId()).thenReturn("LaScalaReference");
        when(placeLikelihood1.getPlace()).thenReturn(place1);
        List<Place.Type> collection = new ArrayList<>();
        collection.add(Place.Type.CAFE);
        collection.add(Place.Type.RESTAURANT);
        collection.add(Place.Type.BAKERY);
        when(place2.getTypes()).thenReturn(collection);
        when(place2.getId()).thenReturn("ChezTintinReference");
        when(placeLikelihood2.getPlace()).thenReturn(place2);
        when(placeResult.getPlaceLikelihoods()).thenReturn(placesList);

        // getRestaurantByIdFromGooglePlace parts
        when(placesClient.fetchPlace(any(FetchPlaceRequest.class)))
                .thenAnswer(i -> {
                    FetchPlaceRequest request = i.getArgument(0);
                    if (request.getPlaceId().equals("LaScalaReference") ) return taskFetch;
                    else return taskFetch2;
                });
        when(taskFetch.addOnSuccessListener(any())).thenReturn(taskFetch);
        when(taskFetch.addOnFailureListener(any())).thenReturn(taskFetch);
        when(taskFetch2.addOnSuccessListener(any())).thenReturn(taskFetch2);
        when(taskFetch2.addOnFailureListener(any())).thenReturn(taskFetch2);
        //trigger from getRestaurantFromGooglePlace
        eventOnCompleteListener.getValue().onComplete(task);

        // check getRestaurantByIdFromGooglePlace until next trigger

        verify(taskFetch).addOnSuccessListener(eventOnSuccessListener.capture());
        verify(taskFetch).addOnFailureListener(eventOnFailureListener.capture());
        assertNotNull(eventOnSuccessListener.getValue());
        assertNotNull(eventOnFailureListener.getValue());

        when (place1.getName()).thenReturn("La Scala");
        when (place1.getAddress()).thenReturn(null);  // TODO to mock
        when (place1.getOpeningHours()).thenReturn(null); // TODO to mock
        when (place1.getPhoneNumber()).thenReturn("01 77 46 51 77");
        when (place1.getWebsiteUri()).thenReturn(null); // TODO to mock
        when(fetchResponse1.getPlace()).thenReturn(place1);

        verify(taskFetch2).addOnSuccessListener(eventOnSuccessListener2.capture());
        verify(taskFetch2).addOnFailureListener(eventOnFailureListener2.capture());
        assertNotNull(eventOnSuccessListener2.getValue());
        assertNotNull(eventOnFailureListener2.getValue());

        when (place2.getName()).thenReturn("Chez Tintin");
        when (place2.getAddress()).thenReturn(null);  // TODO to mock
        when (place2.getOpeningHours()).thenReturn(null); // TODO to mock
        when (place2.getPhoneNumber()).thenReturn("06 26 60 02 69");
        when (place2.getWebsiteUri()).thenReturn(null); // TODO to mock
        when(fetchResponse2.getPlace()).thenReturn(place2);

        // triggers of the 2 fetch
        eventOnSuccessListener.getValue().onSuccess(fetchResponse1);
        eventOnSuccessListener2.getValue().onSuccess(fetchResponse2);

        RestaurantRepository.InitRestaurantStatus status = t.getInitRestaurantsStatus();

        assertEquals(RestaurantRepository.InitRestaurantStatus.INIT_DONE, status);
        assertNotNull(restaurants);
        assertNotNull(restaurants.getValue());
        assertEquals(2, restaurants.getValue().size());
        assertEquals("LaScalaReference", restaurants.getValue().get(0).getId());
        assertEquals("La Scala", restaurants.getValue().get(0).getName());
        assertEquals("01 77 46 51 77", restaurants.getValue().get(0).getPhoneNumber());
        assertEquals("ChezTintinReference", restaurants.getValue().get(1).getId());
        assertEquals("Chez Tintin", restaurants.getValue().get(1).getName());
        assertEquals("06 26 60 02 69", restaurants.getValue().get(1).getPhoneNumber());
    }
}