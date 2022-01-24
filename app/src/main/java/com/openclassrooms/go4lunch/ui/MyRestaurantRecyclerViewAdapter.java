package com.openclassrooms.go4lunch.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.go4lunch.databinding.FragmentRestaurantBinding;
import com.openclassrooms.go4lunch.events.DisplayRestaurantEvent;
import com.openclassrooms.go4lunch.model.Restaurant;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MyRestaurantRecyclerViewAdapter extends RecyclerView.Adapter<MyRestaurantRecyclerViewAdapter.ViewHolder> {

    private final List<Restaurant> restaurants;

    public MyRestaurantRecyclerViewAdapter(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentRestaurantBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.restaurant = restaurants.get(position);
        holder.mIdView.setText(restaurants.get(position).getName());
        holder.mContentView.setText(restaurants.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public final View mView;
        public Restaurant restaurant;

        public ViewHolder(@NonNull FragmentRestaurantBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
            mView = binding.getRoot();
            mView.setOnClickListener(v -> {
                Log.i("TestPlace", "click on an element");
                    v.setEnabled(false);
                    EventBus.getDefault().post(new DisplayRestaurantEvent(restaurant));
                Log.i("TestPlace", "id = (" + restaurant.getLatLng().latitude + "," + restaurant.getLatLng().longitude + ")");
            });
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}