package com.openClassrooms.go4lunch.ui;

import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openClassrooms.go4lunch.databinding.FragmentRestaurantBinding;
import com.openClassrooms.go4lunch.events.DisplayRestaurantEvent;
import com.openClassrooms.go4lunch.model.Restaurant;
import com.openClassrooms.go4lunch.model.Workmate;
import com.openClassrooms.go4lunch.viewmodel.MyViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MyRestaurantRecyclerViewAdapter extends RecyclerView.Adapter<MyRestaurantRecyclerViewAdapter.ViewHolder> {

    private final List<Restaurant> restaurants;
    final private MyViewModel myViewModel;

    public MyRestaurantRecyclerViewAdapter(List<Restaurant> restaurants, MyViewModel myViewModel) {
        this.restaurants = restaurants;
        this.myViewModel = myViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentRestaurantBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.restaurant = restaurants.get(position);
        holder.mName.setText(restaurants.get(position).getName());
        holder.mAddress.setText(restaurants.get(position).getAddress());
        holder.mOpeningHours.setText(restaurants.get(position).getOpeningHours());
        if (holder.restaurant.getBitmap() != null) {
            holder.mPhoto.setImageBitmap(holder.restaurant.getBitmap());
        }

        double distance = 0;
        if ((MapFragment.getLastKnownLocation() != null) && (holder.restaurant.getLatLng() != null)) {
            float[] results = new float[1];
            Location.distanceBetween(MapFragment.getLastKnownLocation().getLatitude(), MapFragment.getLastKnownLocation().getLongitude(),
                    holder.restaurant.getLatLng().latitude, holder.restaurant.getLatLng().longitude,
                    results);
            distance = results[0];
        }
        holder.mDistance.setText(String.format("%sm",Math.round(distance)));

        Log.i("TestSize", "MyRestaurantRecyclerViewAdapter.onBindViewHolder call getWorkmatesByIdRestaurant");
        List<Workmate> w = myViewModel.getWorkmatesByIdRestaurant(holder.restaurant.getId());
        if ((w == null) || (w.size() < 1)) {
            holder.mNumberWorkmate.setVisibility(View.INVISIBLE);
        } else {
            Log.i("TestSize", "MyRestaurantRecyclerViewAdapter.onBindViewHolder call getWorkmatesByIdRestaurant != null");
            holder.mNumberWorkmate.setVisibility(View.VISIBLE);
            holder.mNumberWorkmate.setText(String.format("(%s)", w.size()));
        }

        Log.i("TestLike", "MyRestaurantRecyclerViewAdapter.onBindViewHolder call getLikeById");
        int rate = myViewModel.getLikeById(holder.restaurant.getId());
        Log.i("TestLike", "MyRestaurantRecyclerViewAdapter.onBindViewHolder display getLikeById (rate = " + rate + ")");
        holder.mStar1.setVisibility(View.VISIBLE);
        if (rate > 1) {
            holder.mStar2.setVisibility(View.VISIBLE);
        } else {
            holder.mStar2.setVisibility(View.INVISIBLE);
        }
        if (rate > 2) {
            holder.mStar3.setVisibility(View.VISIBLE);
        } else {
            holder.mStar3.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        if (restaurants==null) {
            Log.w("TestRestaurantList", "MyRestaurantRecyclerViewAdapter.getItemCount restaurants null");
            return 0;
        }
        return restaurants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mName;
        public final TextView mAddress;
        public final TextView mOpeningHours;
        public final View mView;
        public final ImageView mPhoto;
        public final TextView mDistance;
        public final TextView mNumberWorkmate;
        public final ImageView mStar1;
        public final ImageView mStar2;
        public final ImageView mStar3;
        public Restaurant restaurant;

        public ViewHolder(@NonNull FragmentRestaurantBinding binding) {
            super(binding.getRoot());
            mName = binding.restaurantName;
            mAddress = binding.restaurantAddress;
            mOpeningHours = binding.restaurantOpeningHours;
            mPhoto = binding.restaurantBitmap;
            mDistance = binding.restaurantDistance;
            mNumberWorkmate = binding.restaurantNumberWorkmate;
            mStar1 = binding.restaurantNumberStars1;
            mStar2 = binding.restaurantNumberStars2;
            mStar3 = binding.restaurantNumberStars3;
            mView = binding.getRoot();
            mView.setOnClickListener(v -> {
                Log.w("TestRestaurantList", "MyRestaurantRecyclerViewAdapter.ViewHolder click on an element");
                    v.setEnabled(false);
                    EventBus.getDefault().post(new DisplayRestaurantEvent(restaurant));
                Log.i("TestRestaurantList", "id = (" + restaurant.getLatLng().latitude + "," + restaurant.getLatLng().longitude + ")");
            });
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mAddress.getText() + "'";
        }
    }
}