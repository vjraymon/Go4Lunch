package com.openclassrooms.go4lunch.ui;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.go4lunch.databinding.FragmentWorkmateBinding;
import com.openclassrooms.go4lunch.events.DisplayRestaurantEvent;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.viewmodel.MyViewModel;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MyWorkmateRecyclerViewAdapter extends RecyclerView.Adapter<MyWorkmateRecyclerViewAdapter.ViewHolder> {

    private final List<Workmate> workmates;
    private final MyViewModel myViewModel;

    public MyWorkmateRecyclerViewAdapter(List<Workmate> workmates, MyViewModel myViewModel) {
        this.workmates = workmates;
        this.myViewModel = myViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentWorkmateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    public LatLng getRestaurant(Workmate w) {
        if (w.getHasJoined()) return new LatLng(w.getLatitude(), w.getLongitude());
        return null;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.workmate = workmates.get(position);
//        holder.mName.setText(workmates.get(position).getName());
//        holder.mEmail.setText(workmates.get(position).getEmail());
        Uri uri = Uri.parse(workmates.get(position).getPhotoUrl());
        Picasso.with(holder.mPhoto.getContext()).load(uri).into(holder.mPhoto);

        LatLng id = getRestaurant(workmates.get(position));
        Restaurant restaurant = null;
        String s;
        if (id == null) {
            s = String.format("%s haven't chosen yet", workmates.get(position).getName());
        } else {
            Log.i("TestWork", "MyWorkmateRecyclerViewAdapter: onBindViewHolder: ");
            restaurant = myViewModel.getRestaurantByLatLng(id);
            if (restaurant == null) {
                s = "unknown";
            } else {
                s = String.format("%s have chosen %s", workmates.get(position).getName(), restaurant.getName());
            }
        }
        holder.restaurant = restaurant;
        holder.mName.setText(s);
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
        public final View mView;
        public Restaurant restaurant;
        public final ImageView mPhoto;

        public ViewHolder(@NonNull FragmentWorkmateBinding binding) {
            super(binding.getRoot());
            mName = binding.workmateName;
            mEmail = binding.workmateEmail;
            mRestaurant = binding.workmateRestaurant;
            mPhoto = binding.workmatePhoto;
            mView = binding.getRoot();
            mView.setOnClickListener(v -> {
                Log.i("TestPlace", "click on an element");
//                v.setEnabled(false);
                EventBus.getDefault().post(new DisplayRestaurantEvent(restaurant));
            });
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mEmail.getText() + "'";
        }
    }
}
