package com.openclassrooms.go4lunch.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.go4lunch.repository.RestaurantLikeRepository;
import com.openclassrooms.go4lunch.repository.RestaurantRepository;
import com.openclassrooms.go4lunch.repository.WorkmateRepository;

public class MyViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private FirebaseUser mFirebaseUser;
    private RestaurantRepository restaurantRepository;
    private WorkmateRepository workmateRepository;
    private RestaurantLikeRepository restaurantLikeRepository;

    public MyViewModelFactory(Application application) {
        mApplication = application;
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        restaurantRepository = RestaurantRepository.getRestaurantRepository(application.getApplicationContext());
        workmateRepository = WorkmateRepository.getWorkmateRepository();
        restaurantLikeRepository = RestaurantLikeRepository.getRestaurantLikeRepository();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MyViewModel(mApplication, mFirebaseUser, restaurantRepository, workmateRepository, restaurantLikeRepository);
    }
}