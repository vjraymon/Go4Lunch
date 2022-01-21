package com.openclassrooms.go4lunch.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WorkmateRepository {
    private static WorkmateRepository service;

    /**
     * Get an instance on WorkmateRepository
     */
    public static WorkmateRepository getWorkmateRepository(Context context) {
        if (service == null) {
            service = new WorkmateRepository(context);
        }
        return service;
    }

    private List<Workmate> workmatesList = new ArrayList<>(
            Arrays.asList(
                    new Workmate("Caroline@gmail.com", "Caroline", null),
                    new Workmate("Jack@gmail.com", "Jack", null),
                    new Workmate("Emilie@gmail.com", "Emilie", null),
                    new Workmate("Albert@gmail.com", "Albert", null)
            )
    );

    public WorkmateRepository(Context context) {
        // Default Workmate list for test

        select(this.workmatesList);
    }

    private final MutableLiveData<List<Workmate>> workmates = new MutableLiveData<>();

    public void select(List<Workmate> item) {
        workmates.setValue(item);
    }

    public LiveData<List<Workmate>> getWorkmates() {
        return this.workmates;
    }

    public void addWorkmate(Workmate myself) {
        List<Workmate> workmates = this.workmates.getValue();
        if (workmates == null)
        {
            workmates = new ArrayList<>();
        }
        for (Workmate i: workmates) {
            if (myself.getEmail().equals(i.getEmail())) {
                // in this first step, there is no possible modification of an already registered user
                return;
            }
        }
        workmates.add(myself);
        select(workmates);
    }

    public Boolean setRestaurant(Workmate workmate, Restaurant restaurant) {
 //       List<Workmate> workmates = this.workmates.getValue();
        if (this.workmatesList == null)
        {
            this.workmatesList = new ArrayList<>();
        }
        for (Workmate i: this.workmatesList) {
            if (workmate.getEmail().equals(i.getEmail())) {
                Log.i("TestJoin", "WorkmateRepository: setRestaurant: emails equals ");
                if (i.getRestaurant() == null){
                    if ((restaurant == null) || (restaurant.getLatLng() == null)) {
                        Log.i("TestJoin", "WorkmateRepository: setRestaurant: target = source = null");
                        return true;
                    }
                    Log.i("TestJoin", "WorkmateRepository: setRestaurant: target = null source = (" + restaurant.getLatLng().latitude + "," + restaurant.getLatLng().longitude + ")");
                    i.setRestaurant(restaurant.getLatLng());
                } else {
                    if (restaurant == null) {
                        Log.i("TestJoin", "WorkmateRepository: setRestaurant: target != source = null");
                        i.setRestaurant(null);
                    } else if (i.getRestaurant().equals(restaurant.getLatLng())) {
                        Log.i("TestJoin", "WorkmateRepository: setRestaurant: null != target = source");
                        return true;
                    }
                    Log.i("TestJoin", "WorkmateRepository: setRestaurant: null != target != source = (" + Objects.requireNonNull(restaurant).getLatLng().latitude + "," + restaurant.getLatLng().longitude + ")");
                    i.setRestaurant(restaurant.getLatLng());
                }
                select(this.workmatesList);
                return true;
            }
        }
        return false;
    }
}
