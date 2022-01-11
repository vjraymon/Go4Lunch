package com.openclassrooms.go4lunch.ui;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PageAdapter extends FragmentStateAdapter {

    private String [] data = {"A", "B", "C"};

    public PageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public PageAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public PageAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @Override
    public int getItemCount() {
        return(3); // 3 - Number of page to show
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.i("TestPlace", "createFragment" + position);

        if (position == 0) {
            Log.i("TestPlace", "return MapsFragment");
            return new MapsFragment();
        }

        return new BlankFragment(data[position]);
    }
}
