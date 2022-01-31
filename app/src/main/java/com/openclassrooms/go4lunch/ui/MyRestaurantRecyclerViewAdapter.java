package com.openclassrooms.go4lunch.ui;

import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.TypedArrayUtils;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.databinding.FragmentRestaurantBinding;
import com.openclassrooms.go4lunch.events.DisplayRestaurantEvent;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.viewmodel.MyViewModel;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MyRestaurantRecyclerViewAdapter extends RecyclerView.Adapter<MyRestaurantRecyclerViewAdapter.ViewHolder> {

    private final List<Restaurant> restaurants;
    private MyViewModel myViewModel;

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
 /*
        if (restaurants.get(position).getIconUrl()!=null) {
            Uri uri = Uri.parse(restaurants.get(position).getIconUrl());
            Picasso.with(holder.mPhoto.getContext()).load(uri).into(holder.mPhoto);
        }
 */
        double distance = 0;
        if ((MapFragment.getLastKnownLocation() != null) && (holder.restaurant.getLatLng() != null)) {
            float[] results = new float[1];
            Location.distanceBetween(MapFragment.getLastKnownLocation().getLatitude(), MapFragment.getLastKnownLocation().getLongitude(),
                    holder.restaurant.getLatLng().latitude, holder.restaurant.getLatLng().longitude,
                    results);
            distance = results[0];
        }
        holder.mDistance.setText(String.format("%sm",Math.round(distance)));

        Log.i("TestSize", "onBindViewHolder call getWorkmatesByLatLng");
        List<Workmate> w = myViewModel.getWorkmatesByIdRestaurant(holder.restaurant.getId());
        if ((w == null) || (w.size() < 1)) {
            holder.mNumberWorkmate.setVisibility(View.INVISIBLE);
        } else {
            Log.i("TestSize", "onBindViewHolder call getWorkmatesByLatLng != null");
            holder.mNumberWorkmate.setVisibility(View.VISIBLE);
            holder.mNumberWorkmate.setText(String.format("(%s)", w.size()));
        }
    }

    @Override
    public int getItemCount() {
        if (restaurants==null) return 0;
        return restaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mName;
        public final TextView mAddress;
        public final TextView mOpeningHours;
        public final View mView;
        public final ImageView mPhoto;
        public final TextView mDistance;
        public final TextView mNumberWorkmate;
        public Restaurant restaurant;

        public ViewHolder(@NonNull FragmentRestaurantBinding binding) {
            super(binding.getRoot());
            mName = binding.restaurantName;
            mAddress = binding.restaurantAddress;
            mOpeningHours = binding.restaurantOpeningHours;
            mPhoto = binding.restaurantBitmap;
            mDistance = binding.restaurantDistance;
            mNumberWorkmate = binding.restaurantNumberWorkmate;
            mView = binding.getRoot();
            mView.setOnClickListener(v -> {
                Log.i("TestPlace", "click on an element");
//                    v.setEnabled(false);
                    EventBus.getDefault().post(new DisplayRestaurantEvent(restaurant));
                Log.i("TestPlace", "id = (" + restaurant.getLatLng().latitude + "," + restaurant.getLatLng().longitude + ")");
            });
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mAddress.getText() + "'";
        }
    }
}