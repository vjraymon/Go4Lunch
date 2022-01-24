package com.openclassrooms.go4lunch.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.go4lunch.databinding.FragmentWorkmateBinding;
import com.openclassrooms.go4lunch.model.Workmate;

import java.util.List;

public class MyJoinedWorkmateRecyclerViewAdapter extends RecyclerView.Adapter<MyJoinedWorkmateRecyclerViewAdapter.ViewHolder> {

    private final List<Workmate> workmates;

    public MyJoinedWorkmateRecyclerViewAdapter(List<Workmate> workmates) {
        this.workmates = workmates;
        for (Workmate i : workmates) {
            Log.i("TestJoinedList", "MyJoinedWorkmateRecyclerViewAdapter workmate " + i.getName());
        }
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
    }

    @Override
    public int getItemCount() {
        return workmates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mName;
        public final TextView mEmail;
        public Workmate workmate;

        public ViewHolder(@NonNull FragmentWorkmateBinding binding) {
            super(binding.getRoot());
            mName = binding.workmateName;
            mEmail = binding.workmateEmail;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mEmail.getText() + "'";
        }
    }
}