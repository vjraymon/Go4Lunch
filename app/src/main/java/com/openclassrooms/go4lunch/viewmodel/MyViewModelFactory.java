package com.openclassrooms.go4lunch.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openclassrooms.go4lunch.repository.RestaurantLikeRepository;
import com.openclassrooms.go4lunch.repository.RestaurantRepository;
import com.openclassrooms.go4lunch.repository.WorkmateRepository;
import com.openclassrooms.go4lunch.ui.MyselfStorage;

public class MyViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final FirebaseUser mFirebaseUser;
    private final RestaurantRepository restaurantRepository;
    private final WorkmateRepository workmateRepository;
    private final RestaurantLikeRepository restaurantLikeRepository;
    private final String mEMail;

    public MyViewModelFactory(Application application) {
        mApplication = application;
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        restaurantRepository = RestaurantRepository.getRestaurantRepository(application.getApplicationContext());
        workmateRepository = WorkmateRepository.getWorkmateRepository(FirebaseFirestore.getInstance());
        restaurantLikeRepository = RestaurantLikeRepository.getRestaurantLikeRepository(FirebaseFirestore.getInstance());
        mEMail = MyselfStorage.getEMail();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MyViewModel(mApplication, mFirebaseUser, restaurantRepository, workmateRepository, restaurantLikeRepository, mEMail);
    }
}