package com.openclassrooms.go4lunch.ui;

import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.go4lunch.R;
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

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.workmate = workmates.get(position);
        String p = workmates.get(position).getPhotoUrl();
        if (p != null) {
            Uri uri = Uri.parse(p);
            Picasso.with(holder.mPhoto.getContext()).load(uri).into(holder.mPhoto);
        }

        String id = workmates.get(position).getIdRestaurant();
        Restaurant restaurant = null;
        String s;
        if (id == null) {
            s = String.format(holder.mName.getResources().getString(R.string.has_not_decided_yet), workmates.get(position).getName());
            holder.mName.setText(s);
            holder.mName.setTypeface(holder.mName.getTypeface(), Typeface.ITALIC);
            holder.mName.setTextColor(ContextCompat.getColor(holder.mName.getContext(),R.color.quantum_grey));
        } else {
            Log.i("TestWork", "MyWorkmateRecyclerViewAdapter: onBindViewHolder: ");
            restaurant = myViewModel.getRestaurantById(id);
            if (restaurant == null) {
                s = holder.mName.getResources().getString(R.string.restaurant_unknown);
                holder.mName.setText(s);
                holder.mName.setTypeface(holder.mName.getTypeface(), Typeface.ITALIC);
                holder.mName.setTextColor(ContextCompat.getColor(holder.mName.getContext(),R.color.quantum_grey));
            } else {
                s = String.format(holder.mName.getResources().getString(R.string.is_eating_in), workmates.get(position).getName(), restaurant.getName());
                holder.mName.setText(s);
                holder.mName.setTypeface(holder.mName.getTypeface(), Typeface.NORMAL);
                holder.mName.setTextColor(ContextCompat.getColor(holder.mName.getContext(),R.color.black));
            }
        }
        holder.restaurant = restaurant;
    }

    @Override
    public int getItemCount() {
        return workmates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mName;
        public Workmate workmate;
        public final View mView;
        public Restaurant restaurant;
        public final ImageView mPhoto;

        public ViewHolder(@NonNull FragmentWorkmateBinding binding) {
            super(binding.getRoot());
            mName = binding.workmateName;
            mPhoto = binding.workmatePhoto;
            mView = binding.getRoot();
            mView.setOnClickListener(v -> {
                Log.i("TestPlace", "click on an element");
                v.setEnabled(false);
                EventBus.getDefault().post(new DisplayRestaurantEvent(restaurant));
            });
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }
}
