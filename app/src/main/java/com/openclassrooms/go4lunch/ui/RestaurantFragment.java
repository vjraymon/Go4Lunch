package com.openclassrooms.go4lunch.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.repository.RestaurantRepository;
import com.openclassrooms.go4lunch.viewmodel.MyViewModel;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class RestaurantFragment extends Fragment {

//    private RestaurantRepository restaurantRepository;

//    private RestaurantApiService mApiService;
    private List<Restaurant> restaurants;

    // TODO: Customize parameter argument names
    // As each 3 tabs are specific, these parameters might be irrelevant
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RestaurantFragment() {
    }

    // TODO: Customize parameter initialization
    // As each 3 tabs are specific, this parameter might be irrelevant


    public static RestaurantFragment newInstance(int columnCount) {
        RestaurantFragment fragment = new RestaurantFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

 //       mApiService = DiRestaurant.getRestaurantApiService();
//        restaurantRepository = RestaurantRepository.getRestaurantRepository(getContext());
        final MyViewModel myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.init(getContext());
        myViewModel.getRestaurants().observe(this, this::updateRestaurantsList);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);

//        restaurants = mApiService.getRestaurants();
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
 //           restaurantRepository.getRestaurants().observe(this, this::updateRestaurantsList);
        }
        return view;
    }
    private void updateRestaurantsList(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        for (Restaurant restaurant : restaurants) {
            Log.i("TestPlace", "location list retrieved = " + restaurant.getName());
        }
        Log.i("TestPlace", "end of location list retrieved");
        recyclerView.setAdapter(new MyRestaurantRecyclerViewAdapter(restaurants));
    }
}