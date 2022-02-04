package com.openclassrooms.go4lunch.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ApplicationProvider;

import com.google.android.gms.maps.model.LatLng;
import com.google.errorprone.annotations.DoNotMock;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.RestaurantLike;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.repository.RestaurantLikeRepository;
import com.openclassrooms.go4lunch.repository.RestaurantRepository;
import com.openclassrooms.go4lunch.repository.WorkmateRepository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class MyViewModelTest {
    Application myApplication;
    MyViewModel t;

    @Rule // -> allows liveData to work on different thread besides main, must be public!
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Mock
    FirebaseUser firebaseUser;
    @Mock
    RestaurantRepository restaurantRepository;
    @Mock
    WorkmateRepository workmateRepository;
    @Mock
    RestaurantLikeRepository restaurantLikeRepository;

    //
    // tests related to user
    //
    @Test
    public void AuthentificationNotGranted() {
        myApplication = new Application();
        t = new MyViewModel(myApplication, null, restaurantRepository, workmateRepository, restaurantLikeRepository);
        assertNotNull(t);
        assertNull(t.getMyself());
    }

    @Captor
    ArgumentCaptor<Workmate> workmateCaptor;

    @Test
    public void AuthentificationGranted() {
        myApplication = new Application();
        when(firebaseUser.getEmail()).thenReturn("vjraymon@gmail.com");
        when(firebaseUser.getDisplayName()).thenReturn("Jean-Raymond Vieux");
        when(firebaseUser.getPhotoUrl()).thenReturn(null);
        t = new MyViewModel(myApplication, firebaseUser, restaurantRepository, workmateRepository, restaurantLikeRepository);
        assertNotNull(t);
        Workmate myself = t.getMyself();
        assertNotNull(myself);
        assertEquals("vjraymon@gmail.com", myself.getEmail());
        assertEquals("Jean-Raymond Vieux", myself.getName());
        assertNull(myself.getPhotoUrl());
        verify(workmateRepository).addWorkmate(workmateCaptor.capture());
        assertEquals("vjraymon@gmail.com", workmateCaptor.getValue().getEmail());
        assertEquals("Jean-Raymond Vieux", workmateCaptor.getValue().getName());
        assertNull(workmateCaptor.getValue().getPhotoUrl());
        assertNull(workmateCaptor.getValue().getIdRestaurant());
    }

    //
    // Tests related to Workmates
    //
    @Test
    public void GetWorkmatesNotInitialized() {
        AuthentificationGranted();
        when(workmateRepository.getWorkmates()).thenReturn(new MutableLiveData<>());
        LiveData<List<Workmate>> workmates = t.getWorkmates();
        assertNotNull(workmates);
        assertNull(workmates.getValue());
    }

    @Test
    public void GetWorkmatesEmpty() {
        AuthentificationGranted();
        when(workmateRepository.getWorkmates()).thenReturn(new MutableLiveData<>());
        List<Workmate> workmateList = new ArrayList<>();
        MutableLiveData<List<Workmate>> workmateReceived = new MutableLiveData<>();
        workmateReceived.setValue(workmateList);
        when(workmateRepository.getWorkmates()).thenReturn(workmateReceived);
        LiveData<List<Workmate>> workmates = t.getWorkmates();
        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertTrue(workmates.getValue().isEmpty());
    }

    @Test
    public void GetWorkmates1Record() {
        AuthentificationGranted();
        when(workmateRepository.getWorkmates()).thenReturn(new MutableLiveData<>());
        List<Workmate> workmateList = new ArrayList<>();
        workmateList.add(new Workmate("vjraymon@gmail.com", "Jean-Raymond Vieux", null, null));
        MutableLiveData<List<Workmate>> workmateReceived = new MutableLiveData<>();
        assertNotNull(workmateList);
        assertNotNull(workmateList);
        assertEquals(1, workmateList.size());
        workmateReceived.setValue(workmateList);
        when(workmateRepository.getWorkmates()).thenReturn(workmateReceived);
        LiveData<List<Workmate>> workmates = t.getWorkmates();
        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(1, workmates.getValue().size());
        assertEquals("vjraymon@gmail.com", workmates.getValue().get(0).getEmail());
        assertEquals("Jean-Raymond Vieux", workmates.getValue().get(0).getName());
        assertNull(workmates.getValue().get(0).getPhotoUrl());
        assertNull(workmates.getValue().get(0).getIdRestaurant());
    }

    @Test
    public void GetWorkmates2Records() {
        AuthentificationGranted();
        when(workmateRepository.getWorkmates()).thenReturn(new MutableLiveData<>());
        List<Workmate> workmateList = new ArrayList<>();
        workmateList.add(new Workmate("vjraymon@gmail.com","Jean-Raymond Vieux",  null, null));
        workmateList.add(new Workmate("vagnes@gmail.com", "Agnes Vieux", null, "IdMyRestaurant"));
        MutableLiveData<List<Workmate>> workmateReceived = new MutableLiveData<>();
        workmateReceived.setValue(workmateList);
        when(workmateRepository.getWorkmates()).thenReturn(workmateReceived);
        LiveData<List<Workmate>> workmates = t.getWorkmates();
        assertNotNull(workmates);
        assertNotNull(workmates.getValue());
        assertEquals(2, workmates.getValue().size());
        assertEquals("vjraymon@gmail.com", workmates.getValue().get(0).getEmail());
        assertEquals("Jean-Raymond Vieux", workmates.getValue().get(0).getName());
        assertNull(workmates.getValue().get(0).getPhotoUrl());
        assertNull(workmates.getValue().get(0).getIdRestaurant());
        assertEquals("vagnes@gmail.com", workmates.getValue().get(1).getEmail());
        assertEquals("Agnes Vieux", workmates.getValue().get(1).getName());
        assertNull(workmates.getValue().get(1).getPhotoUrl());
        assertEquals("IdMyRestaurant", workmates.getValue().get(1).getIdRestaurant());
    }

    @Captor
    ArgumentCaptor<Restaurant> restaurantCaptor;

    @Test
    public void WorkmateJoiningRestaurant() {
        GetWorkmates2Records();
        Restaurant restaurant = new Restaurant(
                "IdGoogleMap",
                "La Scala",
                "9 rue du general Leclerc",
                new LatLng(10,10),
                "Until 2.00 AM",
                "www.vjraymon.com",
                null,
                "01 77 46 51 77"
                );
        t.joinRestaurant(restaurant);
        verify(workmateRepository).setRestaurant(workmateCaptor.capture(),restaurantCaptor.capture());
        assertEquals("vjraymon@gmail.com", workmateCaptor.getValue().getEmail());
        assertEquals("Jean-Raymond Vieux", workmateCaptor.getValue().getName());
        assertNull(workmateCaptor.getValue().getPhotoUrl());
        assertEquals("IdGoogleMap", restaurantCaptor.getValue().getId());
    }

    @Test
    public void GetWorkmatesByIdNotFound() {
        GetWorkmates2Records();
        List<Workmate> workmates = t.getWorkmatesByIdRestaurant("unknownId");
        assertNotNull(workmates);
        assertTrue(workmates.isEmpty());
    }

    @Test
    public void GetWorkmatesById1Record() {
        GetWorkmates2Records();
        List<Workmate> workmates = t.getWorkmatesByIdRestaurant("IdMyRestaurant");
        assertNotNull(workmates);
        assertEquals(1, workmates.size());
        assertEquals("vagnes@gmail.com", workmates.get(0).getEmail());
        assertEquals("Agnes Vieux", workmates.get(0).getName());
        assertNull(workmates.get(0).getPhotoUrl());
        assertEquals("IdMyRestaurant", workmates.get(0).getIdRestaurant());
    }

    @Test
    public void GetWorkmatesById2Record() {
        AuthentificationGranted();
        when(workmateRepository.getWorkmates()).thenReturn(new MutableLiveData<>());
        List<Workmate> workmateList = new ArrayList<>();
        workmateList.add(new Workmate("vjraymon@gmail.com","Jean-Raymond Vieux",  null, "IdMyRestaurant"));
        workmateList.add(new Workmate("vagnes@gmail.com", "Agnes Vieux", null, "IdMyRestaurant"));
        MutableLiveData<List<Workmate>> workmateReceived = new MutableLiveData<>();
        workmateReceived.setValue(workmateList);
        when(workmateRepository.getWorkmates()).thenReturn(workmateReceived);
        LiveData<List<Workmate>> workmates = t.getWorkmates();
        workmateList = t.getWorkmatesByIdRestaurant("IdMyRestaurant");
        assertNotNull(workmateList);
        assertEquals(2, workmateList.size());
        assertEquals("vjraymon@gmail.com", workmateList.get(0).getEmail());
        assertEquals("Jean-Raymond Vieux", workmateList.get(0).getName());
        assertNull(workmateList.get(0).getPhotoUrl());
        assertEquals("IdMyRestaurant", workmateList.get(0).getIdRestaurant());
        assertEquals("vagnes@gmail.com", workmateList.get(1).getEmail());
        assertEquals("Agnes Vieux", workmateList.get(1).getName());
        assertNull(workmateList.get(1).getPhotoUrl());
        assertEquals("IdMyRestaurant", workmateList.get(1).getIdRestaurant());
    }

    //
    // Tests related to Restaurants
    //
    @Test
    public void GetRestaurantsNotInitialized() {
        AuthentificationGranted();
        when(restaurantRepository.getRestaurants(myApplication.getApplicationContext())).thenReturn(new MutableLiveData<>());
        LiveData<List<Restaurant>> restaurants = t.getRestaurants();
        assertNotNull(restaurants);
        assertNull(restaurants.getValue());
    }

    @Test
    public void GetRestaurantsEmpty() {
        AuthentificationGranted();
        when(restaurantRepository.getRestaurants(myApplication.getApplicationContext())).thenReturn(new MutableLiveData<>());
        List<Restaurant> restaurantList = new ArrayList<>();
        MutableLiveData<List<Restaurant>> restaurantReceived = new MutableLiveData<>();
        restaurantReceived.setValue(restaurantList);
        when(restaurantRepository.getRestaurants(myApplication.getApplicationContext())).thenReturn(restaurantReceived);
        LiveData<List<Restaurant>> restaurants = t.getRestaurants();
        assertNotNull(restaurants);
        assertNotNull(restaurants.getValue());
        assertTrue(restaurants.getValue().isEmpty());
    }

    @Test
    public void GetRestaurants1Record() {
        AuthentificationGranted();
        when(restaurantRepository.getRestaurants(myApplication.getApplicationContext())).thenReturn(new MutableLiveData<>());
        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.add(new Restaurant(
                "IdGoogleMap0",
                "La Scala",
                "9 rue du general Leclerc",
                new LatLng(10.1,12.2),
                "Until 2.00 AM",
                "www.vjraymon.com",
                null,
                "01 77 46 51 77"
        ));
        MutableLiveData<List<Restaurant>> restaurantReceived = new MutableLiveData<>();
        assertNotNull(restaurantList);
        assertNotNull(restaurantList);
        assertEquals(1, restaurantList.size());
        restaurantReceived.setValue(restaurantList);
        when(restaurantRepository.getRestaurants(myApplication.getApplicationContext())).thenReturn(restaurantReceived);
        LiveData<List<Restaurant>> restaurants = t.getRestaurants();
        assertNotNull(restaurants);
        assertNotNull(restaurants.getValue());
        assertEquals(1, restaurants.getValue().size());
        assertEquals("IdGoogleMap0", restaurants.getValue().get(0).getId());
        assertEquals("La Scala", restaurants.getValue().get(0).getName());
        assertEquals("9 rue du general Leclerc", restaurants.getValue().get(0).getAddress());
        assertNotNull(restaurants.getValue().get(0).getLatLng());
        assertEquals(10.1, restaurants.getValue().get(0).getLatLng().latitude, 0.0001);
        assertEquals(12.2, restaurants.getValue().get(0).getLatLng().longitude, 0.0001);
        assertEquals("Until 2.00 AM", restaurants.getValue().get(0).getOpeningHours());
        assertEquals("www.vjraymon.com", restaurants.getValue().get(0).getWebsiteUri());
        assertNull(restaurants.getValue().get(0).getBitmap());
        assertEquals("01 77 46 51 77", restaurants.getValue().get(0).getPhoneNumber());
    }

    @Test
    public void GetRestaurants2Records() {
        AuthentificationGranted();
        when(restaurantRepository.getRestaurants(myApplication.getApplicationContext())).thenReturn(new MutableLiveData<>());
        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.add(new Restaurant(
                "IdGoogleMap0",
                "La Scala",
                "9 rue du general Leclerc",
                new LatLng(10.1,12.2),
                "Until 2.00 AM",
                "www.vjraymon.com",
                null,
                "01 77 46 51 77"
        ));
        restaurantList.add(new Restaurant(
                "IdGoogleMap1",
                "Pizza Hut",
                "9 rue du general Leclerc",
                new LatLng(13.3,14.4),
                "Until 2.00 AM",
                "www.vjraymon.com",
                null,
                "01 77 46 51 77"
        ));
        MutableLiveData<List<Restaurant>> restaurantReceived = new MutableLiveData<>();
        restaurantReceived.setValue(restaurantList);
        when(restaurantRepository.getRestaurants(myApplication.getApplicationContext())).thenReturn(restaurantReceived);
        LiveData<List<Restaurant>> restaurants = t.getRestaurants();
        assertNotNull(restaurants);
        assertNotNull(restaurants.getValue());
        assertEquals(2, restaurants.getValue().size());
        assertEquals("IdGoogleMap0", restaurants.getValue().get(0).getId());
        assertEquals("La Scala", restaurants.getValue().get(0).getName());
        assertEquals("9 rue du general Leclerc", restaurants.getValue().get(0).getAddress());
        assertNotNull(restaurants.getValue().get(0).getLatLng());
        assertEquals(10.1, restaurants.getValue().get(0).getLatLng().latitude, 0.0001);
        assertEquals(12.2, restaurants.getValue().get(0).getLatLng().longitude, 0.0001);
        assertEquals("Until 2.00 AM", restaurants.getValue().get(0).getOpeningHours());
        assertEquals("www.vjraymon.com", restaurants.getValue().get(0).getWebsiteUri());
        assertNull(restaurants.getValue().get(0).getBitmap());
        assertEquals("01 77 46 51 77", restaurants.getValue().get(0).getPhoneNumber());
        assertEquals("IdGoogleMap1", restaurants.getValue().get(1).getId());
        assertEquals("Pizza Hut", restaurants.getValue().get(1).getName());
        assertEquals("9 rue du general Leclerc", restaurants.getValue().get(1).getAddress());
        assertNotNull(restaurants.getValue().get(1).getLatLng());
        assertEquals(13.3, restaurants.getValue().get(1).getLatLng().latitude, 0.0001);
        assertEquals(14.4, restaurants.getValue().get(1).getLatLng().longitude, 0.0001);
        assertEquals("Until 2.00 AM", restaurants.getValue().get(1).getOpeningHours());
        assertEquals("www.vjraymon.com", restaurants.getValue().get(1).getWebsiteUri());
        assertNull(restaurants.getValue().get(1).getBitmap());
        assertEquals("01 77 46 51 77", restaurants.getValue().get(1).getPhoneNumber());
    }

    @Test
    public void GetRestaurantByIdUnknown(){
        GetRestaurants2Records();
        Restaurant restaurant = t.getRestaurantById("unknown");
        assertNull(restaurant);
    }

    @Test
    public void GetRestaurantById(){
        GetRestaurants2Records();
        Restaurant restaurant = t.getRestaurantById("IdGoogleMap0");
        assertNotNull(restaurant);
        assertEquals("IdGoogleMap0", restaurant.getId());
        assertEquals("La Scala", restaurant.getName());
        assertEquals("9 rue du general Leclerc", restaurant.getAddress());
        assertNotNull(restaurant.getLatLng());
        assertEquals(10.1, restaurant.getLatLng().latitude, 0.0001);
        assertEquals(12.2, restaurant.getLatLng().longitude, 0.0001);
        assertEquals("Until 2.00 AM", restaurant.getOpeningHours());
        assertEquals("www.vjraymon.com", restaurant.getWebsiteUri());
        assertNull(restaurant.getBitmap());
        assertEquals("01 77 46 51 77", restaurant.getPhoneNumber());
    }

    @Test
    public void GetRestaurantByLatLngUnknown(){
        GetRestaurants2Records();
        Restaurant restaurant = t.getRestaurantByLatLng(new LatLng(15.5,16.6));
        assertNull(restaurant);
    }

    @Test
    public void GetRestaurantByLatLng(){
        GetRestaurants2Records();
        Restaurant restaurant = t.getRestaurantByLatLng(new LatLng(13.3, 14.4));
        assertNotNull(restaurant);
        assertEquals("IdGoogleMap1", restaurant.getId());
        assertEquals("Pizza Hut", restaurant.getName());
        assertEquals("9 rue du general Leclerc", restaurant.getAddress());
        assertNotNull(restaurant.getLatLng());
        assertEquals(13.3, restaurant.getLatLng().latitude, 0.0001);
        assertEquals(14.4, restaurant.getLatLng().longitude, 0.0001);
        assertEquals("Until 2.00 AM", restaurant.getOpeningHours());
        assertEquals("www.vjraymon.com", restaurant.getWebsiteUri());
        assertNull(restaurant.getBitmap());
        assertEquals("01 77 46 51 77", restaurant.getPhoneNumber());
    }

    @Test
    public void AddRestaurantAlreadyExisting(){
        GetRestaurants2Records();
        t.addRestaurantById("IdGoogleMap0");
        // check that getRestaurantByIdFromGooglePlace is not called
        verify(restaurantRepository, never()).getRestaurantByIdFromGooglePlace(anyString());
    }

    @Test
    public void AddRestaurantUnknown(){
        GetRestaurants2Records();
        t.addRestaurantById("Unknown");
        verify(restaurantRepository).getRestaurantByIdFromGooglePlace(ArgumentMatchers.eq("Unknown"));
    }

    //
    // Tests related to Restaurant likes
    //
    @Test
    public void GetRestaurantLikessNotInitialized() {
        AuthentificationGranted();
        when(restaurantLikeRepository.getRestaurantLikes()).thenReturn(new MutableLiveData<>());
        LiveData<List<RestaurantLike>> restaurantLikes = t.getRestaurantLikes();
        assertNotNull(restaurantLikes);
        assertNull(restaurantLikes.getValue());
    }

    @Test
    public void GetRestaurantLikesEmpty() {
        AuthentificationGranted();
        when(restaurantLikeRepository.getRestaurantLikes()).thenReturn(new MutableLiveData<>());
        List<RestaurantLike> restaurantLikeList = new ArrayList<>();
        MutableLiveData<List<RestaurantLike>> restaurantLikeReceived = new MutableLiveData<>();
        restaurantLikeReceived.setValue(restaurantLikeList);
        when(restaurantLikeRepository.getRestaurantLikes()).thenReturn(restaurantLikeReceived);
        LiveData<List<RestaurantLike>> restaurantLikes = t.getRestaurantLikes();
        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertTrue(restaurantLikes.getValue().isEmpty());
    }

    @Test
    public void GetRestaurantLikes3records() {
        AuthentificationGranted();
        when(restaurantLikeRepository.getRestaurantLikes()).thenReturn(new MutableLiveData<>());
        List<RestaurantLike> restaurantLikeList = new ArrayList<>();
        restaurantLikeList.add(new RestaurantLike(
                "IdGoogleMap0vjraymon@gmail.com",
                "La Scala",
                2
        ));
        restaurantLikeList.add(new RestaurantLike(
                "IdGoogleMap1vjraymon@gmail.com",
                "Pizza Hut",
                1
        ));
        restaurantLikeList.add(new RestaurantLike(
                "IdGoogleMap1vagnes@gmail.com",
                "Pizza Hut",
                4
        ));
        MutableLiveData<List<RestaurantLike>> restaurantLikeReceived = new MutableLiveData<>();
        restaurantLikeReceived.setValue(restaurantLikeList);
        when(restaurantLikeRepository.getRestaurantLikes()).thenReturn(restaurantLikeReceived);
        LiveData<List<RestaurantLike>> restaurantLikes = t.getRestaurantLikes();
        assertNotNull(restaurantLikes);
        assertNotNull(restaurantLikes.getValue());
        assertEquals(3, restaurantLikes.getValue().size());
        assertEquals("IdGoogleMap0vjraymon@gmail.com", restaurantLikes.getValue().get(0).getId());
        assertEquals("La Scala", restaurantLikes.getValue().get(0).getName());
        assertEquals(2, restaurantLikes.getValue().get(0).getLike());
        assertEquals("IdGoogleMap1vjraymon@gmail.com", restaurantLikes.getValue().get(1).getId());
        assertEquals("Pizza Hut", restaurantLikes.getValue().get(1).getName());
        assertEquals(1, restaurantLikes.getValue().get(1).getLike());
        assertEquals("IdGoogleMap1vagnes@gmail.com", restaurantLikes.getValue().get(2).getId());
        assertEquals("Pizza Hut", restaurantLikes.getValue().get(2).getName());
        assertEquals(4, restaurantLikes.getValue().get(2).getLike());
    }

    @Test
    public void GetLikesByIdUnknown() {
        GetRestaurantLikes3records();
        int likeStar;
        likeStar = t.getLikeById("unknown");
        assertEquals(1, likeStar);
    }

    @Test
    public void GetLikesById() {
        GetRestaurantLikes3records();
        int likeNumber;
        likeNumber = t.getLikeById("IdGoogleMap0");
        assertEquals(1, likeNumber);
        likeNumber = t.getLikeById("IdGoogleMap1");
        assertEquals(1, likeNumber);
    }

    @Test
    public void IncLikeWithIdUnknown() {
        GetRestaurantLikes3records();
        t.incLike("unknown");
        int likeNumber;
        likeNumber = t.getLikeById("unknown");
        assertEquals(1, likeNumber);
    }

    @Captor
    ArgumentCaptor<RestaurantLike> restaurantLikeCaptor;

    @Test
    public void IncLikeWithLikeUnknownAndRestaurantKnown() {
        GetRestaurantLikes3records();
        // Set the new restaurant updated
        when(restaurantRepository.getRestaurants(myApplication.getApplicationContext())).thenReturn(new MutableLiveData<>());
        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.add(new Restaurant(
                "unknown",
                "Chez Tintin",
                "9 rue du general Leclerc",
                new LatLng(10.1,12.2),
                "Until 2.00 AM",
                "www.vjraymon.com",
                null,
                "01 77 46 51 77"
        ));
        MutableLiveData<List<Restaurant>> restaurantReceived = new MutableLiveData<>();
        restaurantReceived.setValue(restaurantList);
        when(restaurantRepository.getRestaurants(myApplication.getApplicationContext())).thenReturn(restaurantReceived);
        LiveData<List<Restaurant>> restaurants = t.getRestaurants();
        // trigger the incrementation
        t.incLike("unknown");
        verify(restaurantLikeRepository).addRestaurantLike(restaurantLikeCaptor.capture());
        assertEquals("unknownvjraymon@gmail.com", restaurantLikeCaptor.getValue().getId());
        assertEquals("Chez Tintin", restaurantLikeCaptor.getValue().getName());
        assertEquals(1, restaurantLikeCaptor.getValue().getLike());
        int likeNumber;
        likeNumber = t.getLikeById("unknown");
        assertEquals(1, likeNumber);
    }

    @Test
    public void IncLike() {
        GetRestaurantLikes3records();
        // Set the new restaurant updated
        when(restaurantRepository.getRestaurants(myApplication.getApplicationContext())).thenReturn(new MutableLiveData<>());
        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.add(new Restaurant(
                "IdGoogleMap0",
                "La Scala",
                "9 rue du general Leclerc",
                new LatLng(10.1,12.2),
                "Until 2.00 AM",
                "www.vjraymon.com",
                null,
                "01 77 46 51 77"
        ));
        MutableLiveData<List<Restaurant>> restaurantReceived = new MutableLiveData<>();
        restaurantReceived.setValue(restaurantList);
        when(restaurantRepository.getRestaurants(myApplication.getApplicationContext())).thenReturn(restaurantReceived);
        LiveData<List<Restaurant>> restaurants = t.getRestaurants();
        // trigger the incrementation
        t.incLike("IdGoogleMap0");
        verify(restaurantLikeRepository).updateLike(restaurantLikeCaptor.capture(), ArgumentMatchers.eq(3));
        assertEquals("IdGoogleMap0vjraymon@gmail.com", restaurantLikeCaptor.getValue().getId());
        assertEquals("La Scala", restaurantLikeCaptor.getValue().getName());
        assertEquals(2, restaurantLikeCaptor.getValue().getLike());
        int likeNumber;
        likeNumber = t.getLikeById("unknown");
        assertEquals(1, likeNumber);
    }

    private void OneIncrement(int receivedLike) {
        when(restaurantLikeRepository.getRestaurantLikes()).thenReturn(new MutableLiveData<>());
        List<RestaurantLike> restaurantLikeList = new ArrayList<>();
        restaurantLikeList.add(new RestaurantLike(
                "IdGoogleMap0vjraymon@gmail.com",
                "La Scala",
                receivedLike
        ));
        restaurantLikeList.add(new RestaurantLike(
                "IdGoogleMap1vjraymon@gmail.com",
                "Pizza Hut",
                1
        ));
        restaurantLikeList.add(new RestaurantLike(
                "IdGoogleMap1vagnes@gmail.com",
                "Pizza Hut",
                4
        ));
        MutableLiveData<List<RestaurantLike>> restaurantLikeReceived = new MutableLiveData<>();
        restaurantLikeReceived.setValue(restaurantLikeList);
        when(restaurantLikeRepository.getRestaurantLikes()).thenReturn(restaurantLikeReceived);
        LiveData<List<RestaurantLike>> restaurantLikes = t.getRestaurantLikes();
    }

    @Test
    public void IncLikeCheckNumberOfStars() {
        // at least one restaurant should be registered
        GetRestaurants2Records();
        // trigger the incrementation untill likeNumber reaches 2 or the number of incrementation exceed 20
        int count = 0;
        int likeNumber;
        int receivedLike = 0;
        do {
            OneIncrement(receivedLike);
            likeNumber = t.getLikeById("IdGoogleMap0");
            count = count+1;
            receivedLike = receivedLike+1;
        } while ((count <= 20) && (likeNumber < 2));
        assertEquals(2, likeNumber);
        // trigger the incrementation untill likeNumber reaches 3 or the number of incrementation exceed 20
        count = 0;
        do {
            OneIncrement(receivedLike);
            likeNumber = t.getLikeById("IdGoogleMap0");
            count = count+1;
            receivedLike = receivedLike+1;
        } while ((count <= 20) && (likeNumber < 3));
        assertEquals(3, likeNumber);
    }

}