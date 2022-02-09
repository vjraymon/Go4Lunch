package com.openclassrooms.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openclassrooms.go4lunch.model.RestaurantLike;

import java.util.ArrayList;
import java.util.List;


public class RestaurantLikeRepository {
    private final String TAG = "TestLike";

    private static RestaurantLikeRepository service;
    /**
     * Get an instance on WorkmateRepository
     */
    public static RestaurantLikeRepository getRestaurantLikeRepository(FirebaseFirestore firestore) {
        if (service == null) {
            service = new RestaurantLikeRepository(firestore);
        }
        return service;
    }

//    private FirebaseFirestore db;
    private final CollectionReference restaurantLikesRef;

    public RestaurantLikeRepository(FirebaseFirestore firestore) {
//        db = firestore;
        restaurantLikesRef = firestore.collection("restaurants");
        initializeSnapshot();
    }

    private final MutableLiveData<List<RestaurantLike>> restaurantLikes = new MutableLiveData<>();

    ArrayList<RestaurantLike> freelances;

    public LiveData<List<RestaurantLike>> getRestaurantLikes() {
        Log.d(TAG, "RestaurantLikeRepository.getRestaurantLikes");
        restaurantLikesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<RestaurantLike> freelances = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    freelances.add(document.toObject(RestaurantLike.class));
                }
                this.restaurantLikes.setValue(freelances);
            } else {
                Log.e(TAG, "RestaurantLikeRepository.getRestaurantLikes Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            //handle error
            Log.e(TAG, "RestaurantLikeRepository.getRestaurantLikes Error failure listener ", e);
            this.restaurantLikes.setValue(null);
        });
        return this.restaurantLikes;
    }

    public void addRestaurantLike(RestaurantLike myself) {
        restaurantLikesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean notAlreadyRegistered = true;
                freelances = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    RestaurantLike i = document.toObject(RestaurantLike.class);
                    if ((i != null) && (i.getId() != null)) {
                        freelances.add(i);
                        if (myself.getId().equals(i.getId())) {
                            notAlreadyRegistered = false;
                        }
                    }
                }
                if (notAlreadyRegistered) {
                    // Add a new document with email as ID
                    freelances.add(myself);
                    restaurantLikesRef
                            .document(myself.getId())
                            .set(myself)
                            .addOnSuccessListener(unused -> {
                                Log.d(TAG, "RestaurantLikeRepository.addWorkmate successful");
                                this.restaurantLikes.setValue(this.freelances);
                                if (freelances== null) {
                                    Log.i(TAG, "RestaurantLikeRepository.addWorkmate freelances null");
                                    return;
                                }
                                for (RestaurantLike i : freelances) {
                                    Log.i(TAG, "RestaurantLikeRepository.addWorkmate freelances = " + i.getName() + " " + i.getId());
                                }
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "RestaurantLikeRepository.addWorkmate exception", e));
                } else {
                    this.restaurantLikes.setValue(this.freelances);
                }
            } else {
                Log.e(TAG, "RestaurantLikeRepository.addWorkmate Error getting documents: ", task.getException());

            }
        }).addOnFailureListener(e -> {
            //handle error
            Log.e(TAG, "RestaurantLikeRepository.addWorkmate Error failure listener ", e);
            this.restaurantLikes.postValue(null);
        });
    }

    public void updateLike(RestaurantLike restaurantLike, int like) {
        if (restaurantLike == null) {
            Log.i(TAG, "RestaurantLikeRepository.updateLike workmate null");
            return;
        }
        restaurantLikesRef.get().addOnCompleteListener(task -> {
            Log.i(TAG, "RestaurantLikeRepository.updateLike");
            if (task.isSuccessful()) {
                freelances = new ArrayList<>();
                RestaurantLike w = null;
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    RestaurantLike i = document.toObject(RestaurantLike.class);
                    if ((i != null) && (i.getId() != null)) {
                        if (restaurantLike.getId().equals(i.getId())) {
                            i.setLike(like);
                            w = i;
                        }
                        freelances.add(i);
                    }
                }
                if (w != null) {
                    Log.i(TAG, "RestaurantLikeRepository.updateLike");
                    Log.i(TAG, "RestaurantLikeRepository.updateLike name " + restaurantLike.getName() + " " + like);
                    restaurantLikesRef.document(restaurantLike.getId()).update(
                            "like", like)
                            .addOnSuccessListener(unused -> {
                                Log.d(TAG, "RestaurantLikeRepository.updateLike successful");
                                this.restaurantLikes.setValue(this.freelances);
                                if (freelances== null) {
                                    Log.i(TAG, "RestaurantLikeRepository.updateLike freelances null");
                                    return;
                                }
                                for (RestaurantLike i : freelances) {
                                    Log.i(TAG, "RestaurantLikeRepository.updateLike freelances = " + i.getName() + i.getLike());
                                }
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "RestaurantLikeRepository.updateLike exception", e));
                    Log.i(TAG, "RestaurantLikeRepository.updateLike done");
                }

            } else {
                Log.e(TAG, "RestaurantLikeRepository.updateLike Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            //handle error
            Log.e(TAG, "RestaurantLikeRepository.updateLike Error failure listener ", e);
        });
    }

    private void initializeSnapshot() {
        Log.i(TAG, "WorkmateRepository.initializeSnapshot");
        restaurantLikesRef.addSnapshotListener((documentSnapshot, e) -> {
            Log.i(TAG, "WorkmateRepository.initializeSnapshot onEvent");
            if (e != null) {
                Log.e(TAG, "WorkmateRepository.initializeSnapshot Listen failed.", e);
                return;
            }

            freelances = new ArrayList<>();
            assert documentSnapshot != null;
            for (DocumentSnapshot snapshot : documentSnapshot.getDocuments()) {
                // Snapshot of the changed document
                RestaurantLike i = snapshot.toObject(RestaurantLike.class);
                if (i != null) {
                    freelances.add(i);
                    Log.i(TAG,"WorkmateRepository.initializeSnapshot name = " + i.getName() + " idRestaurant = " + i.getId());
                }
            }
            restaurantLikes.setValue(freelances);
        });
    }
}
