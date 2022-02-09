package com.openclassrooms.go4lunch.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.Places;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.viewmodel.MyViewModel;
import com.openclassrooms.go4lunch.viewmodel.MyViewModelFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class WorkmateFragment extends Fragment {

//    private WorkmateRepository workmateRepository;

    private List<Workmate> workmates;
    private List<Restaurant> restaurants;


    private enum SortMode { NONE, NAME, FREE }
    private SortMode sortMode = SortMode.NONE;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkmateFragment() {
    }

    public static WorkmateFragment newInstance() {
        return new WorkmateFragment();
    }

    MyViewModel myViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        if ((getContext() != null) && (!Places.isInitialized())) Places.initialize(getContext(), getString(R.string.google_maps_key));
        myViewModel = new ViewModelProvider(this, new MyViewModelFactory(Objects.requireNonNull(this.getActivity()).getApplication())).get(MyViewModel.class);
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_sort_workmate, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.workmate_search) {
            // research on workmate not implemented
            return true;
        }
        if (item.getItemId() == R.id.workmate_alphabetical_sort) {
            sortMode = refresh(SortMode.NAME);
            return true;
        }
        if (item.getItemId() == R.id.workmate_free_sort) {
            sortMode = refresh(SortMode.FREE);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) getActivity().finish();
            return true;
        }
        return false;
    }

    private static class CustomComparatorName implements Comparator<Workmate> {
        @Override
        public int compare(Workmate o1, Workmate o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    private String getRestaurant(Workmate o) {
        if ((o == null) || (o.getIdRestaurant() == null) || (myViewModel.getRestaurantById(o.getIdRestaurant()) == null)) return null;
        return myViewModel.getRestaurantById(o.getIdRestaurant()).getName();
    }

    private class CustomComparatorFree implements Comparator<Workmate> {
        @Override
        public int compare(Workmate o1, Workmate o2) {
            if (getRestaurant(o1) == null) {
                if (getRestaurant(o2) == null) return 0;
                return -1;
            }
            if (getRestaurant(o2) == null) return 1;
            return getRestaurant(o1).compareTo(getRestaurant(o2));
        }
    }

    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmate_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            myViewModel.getWorkmates().observe(getViewLifecycleOwner(), this::updateWorkmatesList);
            myViewModel.getRestaurants().observe(getViewLifecycleOwner(), this::updateRestaurantsList);
        }
        return view;
    }
    private void updateWorkmatesList(List<Workmate> workmates) {
        Log.i("TestWork", "WorkmateFragment: updateWorkmatesList");
        this.workmates = workmates;
        for (Workmate workmate : workmates) {
            Log.i("TestPlace", "location list retrieved = " + workmate.getName());
        }
        Log.i("TestWork", "call recyclerView.setAdapter");
        this.refresh(sortMode);
    }
    private void updateRestaurantsList(List<Restaurant> restaurants) {
        Log.i("TestWork", "WorkmateFragment: updateRestaurantsList");
        this.restaurants = restaurants;
        this.refresh(sortMode);
    }

    private SortMode refresh(SortMode s) {
        if ((workmates != null) && (restaurants != null) && (myViewModel != null)) {
            Log.i("TestWork", "call recyclerView.setAdapter");
            switch (s) {
                case NONE:
                    break;
                case NAME:
                    Collections.sort(workmates, new CustomComparatorName());
                    break;
                case FREE:
                    Collections.sort(workmates, new CustomComparatorFree());
                    break;
            }
            recyclerView.setAdapter(new MyWorkmateRecyclerViewAdapter(workmates, myViewModel));
        }
        return s;
    }
}