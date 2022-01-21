package com.openclassrooms.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.go4lunch.databinding.FragmentWorkmateBinding;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.viewmodel.MyViewModel;

import java.util.List;

public class MyWorkmateRecyclerViewAdapter extends RecyclerView.Adapter<MyWorkmateRecyclerViewAdapter.ViewHolder> {

    private final List<Workmate> workmates;
    private final List<Restaurant> restaurants;
    private final MyViewModel myViewModel;

    public MyWorkmateRecyclerViewAdapter(List<Workmate> workmates, List<Restaurant> restaurants, MyViewModel myViewModel) {
        this.workmates = workmates;
        this.myViewModel = myViewModel;
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentWorkmateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.workmate = workmates.get(position);
        holder.mName.setText(workmates.get(position).getName());
        holder.mEmail.setText(workmates.get(position).getEmail());
        LatLng id = workmates.get(position).getRestaurant();
        String s;
        if (id == null) {
            s = "none";
        } else {
            Log.i("TestWork", "MyWorkmateRecyclerViewAdapter: onBindViewHolder: ");
            Restaurant restaurant = myViewModel.getRestaurantByLatLng(this.restaurants, id);
            if (restaurant == null) {
                s = "unknown";
            } else {
                s = restaurant.getName();
            }
        }
        holder.mRestaurant.setText(s);
    }

    @Override
    public int getItemCount() {
        return workmates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mName;
        public final TextView mEmail;
        public final TextView mRestaurant;
        public Workmate workmate;

        public ViewHolder(@NonNull FragmentWorkmateBinding binding) {
            super(binding.getRoot());
            mName = binding.workmateName;
            mEmail = binding.workmateEmail;
            mRestaurant = binding.workmateRestaurant;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mEmail.getText() + "'";
        }
    }
}
