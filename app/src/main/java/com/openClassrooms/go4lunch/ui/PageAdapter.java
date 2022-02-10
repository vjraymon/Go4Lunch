package com.openClassrooms.go4lunch.ui;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class
PageAdapter extends FragmentStateAdapter {

    private final String [] data = {"A", "B", "C"};

    public PageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public int getItemCount() {
        return(3); // 3 - Number of page to show
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.i("TestPlace", "createFragment" + position);

        switch (position) {
            case 0:
                Log.i("TestMenu", "return MapsFragment");
                return new MapFragment();

            case 1:
                Log.i("TestMenu", "return RestaurantFragment");
                return RestaurantFragment.newInstance();

            case 2:
                Log.i("TestMenu", "return WorkmateFragment");
                return WorkmateFragment.newInstance();
        }

        return new BlankFragment(data[position]);
    }
}
