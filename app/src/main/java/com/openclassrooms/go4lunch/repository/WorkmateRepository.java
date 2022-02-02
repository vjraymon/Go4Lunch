package com.openclassrooms.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
                    freelances.add(myself);
                    db.collection("workmates")
                            .document(myself.getEmail())
                            .set(myself)
                            .addOnSuccessListener(unused -> {
                                Log.d("TestWork", "FirebaseHelper.addWorkmate successfull");
                                this.workmates.setValue(this.freelances);
                                if (freelances== null) {
                                    Log.i("TestWork", "FirebaseHelper.addWorkmate freelances null");
                                    return;
                                }
                                for (Workmate i : freelances) {
                                    Log.i("TestWork", "FirebaseHelper.addWorkmate freelances = " + i.getName() + " " + i.getIdRestaurant());
                                }
                            })
                            .addOnFailureListener(e -> Log.e("TestWork", "FirebaseHelper.addWorkmate exception", e));
                }
            } else {
                Log.d("Error", "FirebaseHelper.addWorkmate Error getting documents: ", task.getException());

            }
        }).addOnFailureListener(e -> {
            //handle error
            Log.i("TestWork", "FirebaseHelper.addWorkmate Error failure listener ", e);
            this.workmates.postValue(null);
        });
    }

    public void updateIdRestaurant(Workmate workmate, String idRestaurant) {
        if (workmate == null) {
            Log.i("TestWork", "FirebaseHelper.updateIdRestaurant workmate null");
            return;
        }
        Log.i("TestWork", "FirebaseHelper.updateIdRestaurant");
            Log.i("TestWork", "FirebaseHelper.updateIdRestaurant name " + workmate.getName() + idRestaurant);
            db.collection("workmates").document(workmate.getEmail()).update(
                "idRestaurant", idRestaurant)
                .addOnSuccessListener(unused -> {
                    Log.d("TestWork", "FirebaseHelper.updateIdRestaurant successfull");
                    this.workmates.setValue(this.freelances);
                    if (freelances== null) {
                        Log.i("TestWork", "FirebaseHelper.updateIdRestaurant freelances null");
                        return;
                    }
                    for (Workmate i : freelances) {
                        Log.i("TestWork", "FirebaseHelper.updateIdRestaurant freelances = " + i.getName() + i.getIdRestaurant());
                    }
                })
                .addOnFailureListener(e -> Log.e("TestWork", "FirebaseHelper.updateIdRestaurant exception", e));
    }

    public void setRestaurant(Workmate workmate, Restaurant restaurant) {
        String idRestaurant;
        Log.i("TestWork", "WorkmateRepository.setRestaurant");
        if (workmate == null) {
            Log.i("TestWork", "WorkmateRepository.setRestaurant : workmate null");
            return;
        }
        if (restaurant != null) idRestaurant = restaurant.getId();
        else idRestaurant = null;

        workmatesRef.get().addOnCompleteListener(task -> {
            Log.i("TestWork", "WorkmateRepository.OnCompleteListener");
            if (task.isSuccessful()) {
                freelances = new ArrayList<>();
                Workmate w = null;
                boolean notAlreadyRegistered = true;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Workmate i = document.toObject(Workmate.class);
                    if (workmate.getEmail().equals(document.toObject(Workmate.class).getEmail())) {
                        i.setIdRestaurant(idRestaurant);
                        w = i;
                    }
                    freelances.add(i);
                }
                if (w != null) {
                    this.updateIdRestaurant(workmate, idRestaurant);
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
