package com.openclassrooms.go4lunch.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;

import java.util.ArrayList;
import java.util.List;

public class WorkmateRepository {
    public final static String TAG = "TestWork";

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
        initializeSnapshot();
    }

    private final MutableLiveData<List<Workmate>> workmates = new MutableLiveData<>();

    ArrayList<Workmate> freelances;

    public LiveData<List<Workmate>> getWorkmates() {
        workmatesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                freelances = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    freelances.add(document.toObject(Workmate.class));
                }
                this.workmates.setValue(freelances);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            //handle error
            Log.i(TAG, "Error failure listener ", e);
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
                                Log.d(TAG, "FirebaseHelper.addWorkmate successfull");
                                this.workmates.setValue(this.freelances);
                                if (freelances== null) {
                                    Log.i(TAG, "FirebaseHelper.addWorkmate freelances null");
                                    return;
                                }
                                for (Workmate i : freelances) {
                                    Log.i(TAG, "FirebaseHelper.addWorkmate freelances = " + i.getName() + " " + i.getIdRestaurant());
                                }
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "FirebaseHelper.addWorkmate exception", e));
                }
            } else {
                Log.d(TAG, "FirebaseHelper.addWorkmate Error getting documents: ", task.getException());

            }
        }).addOnFailureListener(e -> {
            //handle error
            Log.i(TAG, "FirebaseHelper.addWorkmate Error failure listener ", e);
            this.workmates.postValue(null);
        });
    }

    public void updateIdRestaurant(Workmate workmate, String idRestaurant) {
        if (workmate == null) {
            Log.i(TAG, "FirebaseHelper.updateIdRestaurant workmate null");
            return;
        }
        Log.i(TAG, "FirebaseHelper.updateIdRestaurant");
            Log.i(TAG, "FirebaseHelper.updateIdRestaurant name " + workmate.getName() + idRestaurant);
            db.collection("workmates").document(workmate.getEmail()).update(
                "idRestaurant", idRestaurant)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "FirebaseHelper.updateIdRestaurant successfull");
                    this.workmates.setValue(this.freelances);
                    if (freelances== null) {
                        Log.i(TAG, "FirebaseHelper.updateIdRestaurant freelances null");
                        return;
                    }
                    for (Workmate i : freelances) {
                        Log.i(TAG, "FirebaseHelper.updateIdRestaurant freelances = " + i.getName() + i.getIdRestaurant());
                    }
                })
                .addOnFailureListener(e -> Log.e("TestWork", "FirebaseHelper.updateIdRestaurant exception", e));
    }

    public void setRestaurant(Workmate workmate, Restaurant restaurant) {
        String idRestaurant;
        Log.i(TAG, "WorkmateRepository.setRestaurant");
        if (workmate == null) {
            Log.i(TAG, "WorkmateRepository.setRestaurant : workmate null");
            return;
        }
        if (restaurant != null) idRestaurant = restaurant.getId();
        else idRestaurant = null;

        workmatesRef.get().addOnCompleteListener(task -> {
            Log.i(TAG, "WorkmateRepository.OnCompleteListener");
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
                    Log.i(TAG, "WorkmateRepository.setRestaurant done");
                }

            } else {
                Log.i(TAG, "WorkmateRepository.setRestaurant Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            //handle error
            Log.i(TAG, "WorkmateRepository.setRestaurant Error failure listener ", e);
//            this.workmates.setValue(null);
        });
    }

    private void initializeSnapshot() {
        Log.i(TAG, "WorkmateRepository.initializeSnapshot");
        workmatesRef.addSnapshotListener((documentSnapshot, e) -> {
            Log.i(TAG, "WorkmateRepository.initializeSnapshot onEvent");
            if (e != null) {
                Log.w(TAG, "WorkmateRepository.initializeSnapshot Listen failed.", e);
                return;
            }

            freelances = new ArrayList<>();
            assert documentSnapshot != null;
            for (DocumentSnapshot snapshot : documentSnapshot.getDocuments()) {
                // Snapshot of the changed document
                Workmate i = snapshot.toObject(Workmate.class);
                if (i != null) {
                    freelances.add(i);
                    Log.i(TAG, "WorkmateRepository.initializeSnapshot name = " + i.getName() + " idRestaurant = " + i.getIdRestaurant());
                }
            }
            workmates.setValue(freelances);
            });
    }
}
