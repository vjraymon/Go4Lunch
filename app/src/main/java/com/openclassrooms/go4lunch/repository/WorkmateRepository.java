package com.openclassrooms.go4lunch.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public WorkmateRepository(Context context) {
        // Default Workmate list for test
        List<Workmate> workmates = new ArrayList<>(
                Arrays.asList(
                        new Workmate("Caroline@gmail.com", "Caroline", null),
                        new Workmate("Jack@gmail.com", "Jack", null),
                        new Workmate("Emilie@gmail.com", "Emilie", null)
                )
        );

        select(workmates);
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
        List<Workmate> workmates = this.workmates.getValue();
        if (workmates == null)
        {
            workmates = new ArrayList<>();
        }
        for (Workmate i: workmates) {
            if (workmate.getEmail().equals(i.getEmail())) {
                if (restaurant == null) {
                    i.setRestaurant(null);
                } else {
                    i.setRestaurant(restaurant.getLatLng());
                }
                select(workmates);
                return true;
            }
        }
        return false;
    }
}
