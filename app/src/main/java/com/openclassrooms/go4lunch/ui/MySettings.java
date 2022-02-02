package com.openclassrooms.go4lunch.ui;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.viewmodel.MyViewModel;

import java.util.List;

public class MySettings {
    private final static String TAG = "Fire";

    private static MySettings service;

    /**
     * Get an instance on RestaurantRepository
     */
    public static MySettings getMySettings() {
        if (service == null) {
            service = new MySettings();
        }
        return service;
    }

    private static Restaurant restaurant;
    private static String myself;
    private static List<Workmate> attendees;
    private MyViewModel myViewModel;

    private void setMyself() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) myself = null;
        else myself = user.getDisplayName();
    }

    public void setRestaurant(Restaurant restaurant) {
        MySettings.restaurant = restaurant;
    }

    public void setAttendees(List<Workmate> attendees) {
        MySettings.attendees = attendees;
    }

    public void setMyViewModel(MyViewModel myViewModel) {
        this.myViewModel = myViewModel;
    }

    public String getTitle() {
        setMyself();
        if (myself == null) return null;
        return String.format("Dear %s it's time to lunch!", myself);
    }

    public String getBody() {
        setMyself();
        if (myself == null) return null;
        if (myViewModel == null) return null;
        myViewModel.initializationNotification();
        Log.i(TAG, "MySettings.getBody: " + myself
        + " restaurant = " + ((restaurant == null) ? "null" : restaurant.getName())
        + " attendees = " + ((attendees == null) ? "null" : attendees.size()));
        String restaurantText;
        StringBuilder attendeesText = null;
        if (restaurant == null) return null;

        restaurantText = String.format("You are attended to the restaurant %s", restaurant.getName() );
        if (attendees !=null) {
            int sum = 0;
            for (Workmate w : attendees) {
                if ((w != null) && !w.getName().equals(myself)) sum = sum + 1;
            }
            if (sum > 0) {
                attendeesText = new StringBuilder("\n Other attendees:");
                for (Workmate w : attendees) {
                    if ((w != null) && !w.getName().equals(myself)) {
                        attendeesText.append(String.format("\n    %s", w.getName()));
                    }
                }
            }
        }
        return restaurantText + attendeesText;
    }
}
