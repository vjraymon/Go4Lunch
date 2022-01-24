package com.openclassrooms.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;

import java.util.ArrayList;
import java.util.List;

public class WorkmateRepository {
    private static WorkmateRepository service;
    /**
     * Get an instance on WorkmateRepository
     */
    public static WorkmateRepository getWorkmateRepository() {
        if (service == null) {
            service = new WorkmateRepository();
        }
        return service;
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public final CollectionReference workmatesRef = db.collection("workmates");

    public WorkmateRepository() {
        // Default Workmate list for test
    }

    private final MutableLiveData<List<Workmate>> workmates = new MutableLiveData<>();

    ArrayList<Workmate> freelances;

    public LiveData<List<Workmate>> getWorkmates() {
        workmatesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Workmate> freelances = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    freelances.add(document.toObject(Workmate.class));
                }
                this.workmates.setValue(freelances);
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            //handle error
            Log.i("TestWork", "Error failure listener ", e);
            this.workmates.setValue(null);
        });
        return this.workmates;
    }

    public void addWorkmate(Workmate myself) {
        workmatesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean notAlreadyRegistered = true;
                freelances = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    freelances.add(document.toObject(Workmate.class));
                    if (myself.getEmail().equals(document.toObject(Workmate.class).getEmail())) {
                        notAlreadyRegistered = false;
                    }
                }
                if (notAlreadyRegistered) {
                    // Add a new document with email as ID
                    db.collection("workmates")
                            .document(myself.getEmail())
                            .set(myself);
                    freelances.add(myself);
                    this.workmates.setValue(freelances);
                }
            } else {
                Log.d("Error", "Error getting documents: ", task.getException());

            }
        }).addOnFailureListener(e -> {
            //handle error
            Log.i("TestWork", "Error failure listener ", e);
            this.workmates.postValue(null);
        });
    }

 //   LatLng latLng;

    private void setLatLng(Workmate w, LatLng latLng) {
        w.setHasJoined((latLng != null));
        if (latLng != null) {
            w.setLatitude(latLng.latitude);
            w.setLongitude(latLng.longitude);
        }
    }

    public void updateLatLng(Workmate workmate, LatLng latLng) {
        if (workmate == null) {
            Log.i("TestWork", "FirebaseHelper.updateLatLng workmate null");
            return;
        }
        Log.i("TestWork", "FirebaseHelper.updateLatLng");
        if (latLng == null) {
            Log.i("TestWork", "FirebaseHelper.updateLatLng latlng null");
            db.collection("workmates").document(workmate.getEmail()).update("hasJoined", false);
        } else {
            Log.i("TestWork", "FirebaseHelper.updateLatLng name " + workmate.getName() + " (" + latLng.latitude + "," + latLng.longitude + ")");
            db.collection("workmates").document(workmate.getEmail()).update(
                    "hasJoined", true,
                    "latitude", latLng.latitude,
                    "longitude", latLng.longitude)
                    .addOnSuccessListener(unused -> {
                        Log.d("TestWork", "FirebaseHelper.updateLatLng successfull");
                        this.workmates.setValue(this.freelances);
                        if (freelances== null) {
                            Log.i("TestWork", "FirebaseHelper.updateLatLng freelances null");
                            return;
                        }
                        for (Workmate i : freelances) {
                            if (i.getHasJoined()) {
                                Log.i("TestWork", "FirebaseHelper.updateLatLng freelances = " + i.getName() + " (" + i.getLatitude() + "," + i.getLongitude() + ")");
                            } else {
                                Log.i("TestWork", "FirebaseHelper.updateLatLng freelances = " + i.getName() + " none");
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("TestWork", "FirebaseHelper.updateLatLng exception", e));
       }
    }

    public void setRestaurant(Workmate workmate, Restaurant restaurant) {
        LatLng latLng;
        Log.i("TestWork", "WorkmateRepository.setRestaurant");
        if (workmate == null) {
            Log.i("TestWork", "WorkmateRepository.setRestaurant : workmate null");
            return;
        }
        if (restaurant != null) latLng = restaurant.getLatLng();
        else latLng = null;

        workmatesRef.get().addOnCompleteListener(task -> {
            Log.i("TestWork", "WorkmateRepository.OnCompleteListener");
            if (task.isSuccessful()) {
                freelances = new ArrayList<>();
                Workmate w = null;
                boolean notAlreadyRegistered = true;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Workmate i = document.toObject(Workmate.class);
                    if (workmate.getEmail().equals(document.toObject(Workmate.class).getEmail())) {
                        setLatLng(i, latLng);
                        w = i;
                    }
                    freelances.add(i);
                }
                if (w != null) {
                    this.updateLatLng(workmate, latLng);
//                    this.workmates.setValue(this.freelances);
                    Log.i("TestWork", "WorkmateRepository.setRestaurant done");
                }

            } else {
                Log.i("TestWork", "WorkmateRepository.setRestaurant Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            //handle error
            Log.i("TestWork", "WorkmateRepository.setRestaurant Error failure listener ", e);
//            this.workmates.setValue(null);
        });
    }
}
