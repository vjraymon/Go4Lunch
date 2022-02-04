package com.openclassrooms.go4lunch.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.repository.RestaurantLikeRepository;
import com.openclassrooms.go4lunch.repository.RestaurantRepository;
import com.openclassrooms.go4lunch.repository.WorkmateRepository;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
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

}