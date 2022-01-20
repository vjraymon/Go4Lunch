package com.openclassrooms.go4lunch.ui;

import static com.openclassrooms.go4lunch.repository.WorkmateRepository.getWorkmateRepository;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.repository.WorkmateRepository;

import java.util.List;

public class WorkmateFragment extends Fragment {

    private WorkmateRepository workmateRepository;

    //    private RestaurantApiService mApiService;
    private List<Workmate> workmates;

    // TODO: Customize parameter argument names
    // As each 3 tabs are specific, these parameters might be irrelevant
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkmateFragment() {
    }

    // TODO: Customize parameter initialization
    // As each 3 tabs are specific, this parameter might be irrelevant


    public static WorkmateFragment newInstance(int columnCount) {
        WorkmateFragment fragment = new WorkmateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //       mApiService = DiRestaurant.getRestaurantApiService();
        workmateRepository = getWorkmateRepository(getContext());
        Workmate myself = new Workmate("vjraymon@gmail.com", "Myself", null);
        workmateRepository.addWorkmate(myself);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmate_list, container, false);

//        restaurants = mApiService.getRestaurants();
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            workmateRepository.getWorkmates().observe(this, this::updateWorkmatesList);
        }
        return view;
    }
    private void updateWorkmatesList(List<Workmate> workmates) {
        this.workmates = workmates;
        for (Workmate workmate : workmates) {
            Log.i("TestPlace", "location list retrieved = " + workmate.getName());
        }
        Log.i("TestPlace", "end of location list retrieved");
        recyclerView.setAdapter(new MyWorkmateRecyclerViewAdapter(workmates));
    }
}