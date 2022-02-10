package com.openClassrooms.go4lunch.ui;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openClassrooms.go4lunch.R;
import com.openClassrooms.go4lunch.model.Workmate;
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

        public ViewHolder(View v) {
            super(v);
            mPhoto = v.findViewById(R.id.joined_workmate_photo);
            mName = v.findViewById(R.id.joined_workmate_name);
        }
        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'";
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.i("TestJoinedList", "MyJoinedWorkmateRecyclerViewAdapter.onBindViewHolder position = " + position + " : " + workmates.get(position).getName());
        holder.mName.setText(String.format(holder.mName.getResources().getString(R.string.has_joined), workmates.get(position).getName()));

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