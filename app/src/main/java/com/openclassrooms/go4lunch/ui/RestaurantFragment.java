package com.openclassrooms.go4lunch.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.RestaurantLike;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.viewmodel.MyViewModel;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class RestaurantFragment extends Fragment {

    List<Restaurant> restaurants;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RestaurantFragment() {
    }

    public static RestaurantFragment newInstance() {
        return new RestaurantFragment();
    }
    private MyViewModel myViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
    }

    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            myViewModel.getWorkmates().observe(getViewLifecycleOwner(), this::updateWorkmatesList);
            myViewModel.getRestaurants().observe(getViewLifecycleOwner(), this::updateRestaurantsList);
            myViewModel.getRestaurantLikes().observe(getViewLifecycleOwner(), this::updateRestaurantLikesList);
        }
        return view;
    }
    private void updateRestaurantsList(List<Restaurant> restaurants) {
        Log.i("TestJoin", "RestaurantFragment: updateRestaurantsList");
        this.restaurants = restaurants;
        if (restaurants == null) return;
            for (Restaurant restaurant : restaurants) {
            Log.i("TestPlace", "RestaurantFragment: updateRestaurantsList location list retrieved = " + restaurant.getName());
        }
        Log.i("TestPlace", "RestaurantFragment: updateRestaurantsList end of location list retrieved");
        recyclerView.setAdapter(new MyRestaurantRecyclerViewAdapter(restaurants, myViewModel));
    }
    private void updateWorkmatesList(List<Workmate> workmates) {
        Log.i("TestWork", "RestaurantFragment: updateWorkmatesList");
        recyclerView.setAdapter(new MyRestaurantRecyclerViewAdapter(restaurants, myViewModel));
    }
    private void updateRestaurantLikesList(List<RestaurantLike> unused) {
        Log.i("TestLike", "RestaurantFragment: updateRestaurantLikesList");
        recyclerView.setAdapter(new MyRestaurantRecyclerViewAdapter(restaurants, myViewModel));
    }

}