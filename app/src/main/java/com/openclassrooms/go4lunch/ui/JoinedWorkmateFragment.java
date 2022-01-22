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

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.viewmodel.MyViewModel;

import java.util.List;

public class JoinedWorkmateFragment extends Fragment {

//    private WorkmateRepository workmateRepository;

    //    private RestaurantApiService mApiService;
    private List<Workmate> workmates;
    private List<Restaurant> restaurants;

    private double mLatitude;
    private double mLongitude;
    private LatLng mLatLng;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public JoinedWorkmateFragment() {
    }

    // TODO: Customize parameter initialization
    // As each 3 tabs are specific, this parameter might be irrelevant


    public static JoinedWorkmateFragment newInstance(LatLng latLng) {
        JoinedWorkmateFragment fragment = new JoinedWorkmateFragment();
        Bundle args = new Bundle();
        args.putDouble("keyLat", latLng.latitude);
        args.putDouble("keyLng", latLng.longitude);
        fragment.setArguments(args);
        return fragment;
    }

    MyViewModel myViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.init(getContext());
        myViewModel.getWorkmates().observe(this, this::updateWorkmatesList);
        if (getArguments() != null) {
            mLatitude = getArguments().getDouble("keyLat");
            mLongitude = getArguments().getDouble("keyLng");
            mLatLng = new LatLng(mLatitude, mLongitude);
        }
    }

    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_joined_workmate_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        return view;
    }
    private void updateWorkmatesList(List<Workmate> workmates) {
        Log.i("TestJoinedList", "WorkmateFragment: updateWorkmatesList");
        this.workmates = workmates;
        for (Workmate workmate : workmates) {
            Log.i("TestPlace", "location list retrieved = " + workmate.getName());
        }
        this.refresh();
    }
    private void refresh() {
        if ((workmates != null) && (myViewModel != null)) {
            Log.i("TestJoinedList", "call recyclerView.setAdapter LatLng = (" + mLatLng.latitude + "," + mLatLng.longitude);
            List<Workmate> joinedWorkmates = myViewModel.getWorkmatesByLatLng(workmates,mLatLng);
            recyclerView.setAdapter(new MyJoinedWorkmateRecyclerViewAdapter(joinedWorkmates));
        }
    }
}
