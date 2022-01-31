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

import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.databinding.FragmentWorkmateBinding;
import com.openclassrooms.go4lunch.databinding.JoinedWorkmateBinding;
import com.openclassrooms.go4lunch.model.Workmate;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyJoinedWorkmateRecyclerViewAdapter extends RecyclerView.Adapter<MyJoinedWorkmateRecyclerViewAdapter.ViewHolder> {

    private final List<Workmate> workmates;

    public MyJoinedWorkmateRecyclerViewAdapter(List<Workmate> workmates) {
        this.workmates = workmates;
        for (Workmate i : workmates) {
            Log.i("TestJoinedList", "MyJoinedWorkmateRecyclerViewAdapter workmate " + i.getName());
        }
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.joined_workmate, viewGroup, false);

        return new ViewHolder(v);
    }
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mPhoto;
        public final TextView mName;
        public final TextView mRestaurant;
        public Workmate workmate;

        public ViewHolder(View v) {
            super(v);
            mPhoto = v.findViewById(R.id.joined_workmate_photo);
            mName = v.findViewById(R.id.joined_workmate_name);
            mRestaurant = v.findViewById(R.id.joined_workmate_restaurant);
        }
        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mRestaurant.getText() + "'";
        }
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.i("TestJoinedList", "MyJoinedWorkmateRecyclerViewAdapter.onBindViewHolder position = " + position + " : " + workmates.get(position).getName());
        holder.workmate = workmates.get(position);
        holder.mName.setText(workmates.get(position).getName());
        holder.mRestaurant.setText(R.string.has_joined);

        String p = workmates.get(position).getPhotoUrl();
        if (p != null) {
            Uri uri = Uri.parse(p);
            Picasso.with(holder.mPhoto.getContext()).load(uri).into(holder.mPhoto);
        }
    }

    @Override
    public int getItemCount() {
        return workmates.size();
    }


}