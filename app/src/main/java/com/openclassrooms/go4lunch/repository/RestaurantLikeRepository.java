package com.openclassrooms.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.go4lunch.model.RestaurantLike;

import java.util.ArrayList;
import java.util.List;


public class RestaurantLikeRepository {
    private final String TAG = "TestLike";

    private static RestaurantLikeRepository service;
    /**
     * Get an instance on WorkmateRepository
     */
    public static RestaurantLikeRepository getRestaurantLikeRepository() {
        if (service == null) {
            service = new RestaurantLikeRepository();
        }
        return service;
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public final CollectionReference restaurantLikesRef = db.collection("restaurants");

    public RestaurantLikeRepository() {
        // Default Restaurant like list for test
    }

    private final MutableLiveData<List<RestaurantLike>> restaurantLikes = new MutableLiveData<>();

    ArrayList<RestaurantLike> freelances;

    public LiveData<List<RestaurantLike>> getRestaurantLikes() {
        Log.d(TAG, "RestaurantLikeRepository.getRestaurantLikes");
        restaurantLikesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<RestaurantLike> freelances = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    freelances.add(document.toObject(RestaurantLike.class));
                }
                this.restaurantLikes.setValue(freelances);
            } else {
                Log.d(TAG, "RestaurantLikeRepository.getRestaurantLikes Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            //handle error
            Log.i(TAG, "RestaurantLikeRepository.getRestaurantLikes Error failure listener ", e);
            this.restaurantLikes.setValue(null);
        });
        return this.restaurantLikes;
    }

    public void addRestaurantLike(RestaurantLike myself) {
        restaurantLikesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean notAlreadyRegistered = true;
                freelances = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    freelances.add(document.toObject(RestaurantLike.class));
                    if (myself.getId().equals(document.toObject(RestaurantLike.class).getId())) {
                        notAlreadyRegistered = false;
                    }
                }
                if (notAlreadyRegistered) {
                    // Add a new document with email as ID
                    freelances.add(myself);
                    db.collection("restaurants")
                            .document(myself.getId())
                            .set(myself)
                            .addOnSuccessListener(unused -> {
                                Log.d(TAG, "RestaurantLikeRepository.addWorkmate successfull");
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
                }
            } else {
                Log.d(TAG, "RestaurantLikeRepository.addWorkmate Error getting documents: ", task.getException());

            }
        }).addOnFailureListener(e -> {
            //handle error
            Log.i(TAG, "RestaurantLikeRepository.addWorkmate Error failure listener ", e);
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
                boolean notAlreadyRegistered = true;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    RestaurantLike i = document.toObject(RestaurantLike.class);
                    if (restaurantLike.getId().equals(document.toObject(RestaurantLike.class).getId())) {
                        i.setLike(i.getLike()+1);
                        w = i;
                    }
                    freelances.add(i);
                }
                if (w != null) {
                    Log.i(TAG, "RestaurantLikeRepository.updateLike");
                    Log.i(TAG, "RestaurantLikeRepository.updateLike name " + restaurantLike.getName() + " " + like);
                    db.collection("restaurants").document(restaurantLike.getId()).update(
                            "like", like)
                            .addOnSuccessListener(unused -> {
                                Log.d(TAG, "RestaurantLikeRepository.updateLike successfull");
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
                Log.i(TAG, "RestaurantLikeRepository.updateLike Error getting documents: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            //handle error
            Log.i(TAG, "RestaurantLikeRepository.updateLike Error failure listener ", e);
        });
    }
}
