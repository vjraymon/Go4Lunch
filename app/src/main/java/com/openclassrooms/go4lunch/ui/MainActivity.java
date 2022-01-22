package com.openclassrooms.go4lunch.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.events.DisplayRestaurantEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data.add(getApplicationContext().getString(R.string.map_view));
        data.add(getApplicationContext().getString(R.string.list_view));
        data.add(getApplicationContext().getString(R.string.workmates));
        setContentView(R.layout.activity_main);
        Log.i("TestPlace", "startSignInActivity()");
        startSignInActivity();
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private void startSignInActivity() {
        // Choose authentification providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        // Launch the activity
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
//                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
//                .setIsSmartLockEnabled(false, true)
//                .setLogo(R.drawable.ic_logo_auth)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
//      IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
//          FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            //3 - Configure ViewPager
            this.configureViewPager();
        } else {
            finish();
        }

    }

    private void configureViewPager(){
        // 1 - Get ViewPager from layout
        ViewPager2 page = findViewById(R.id.activity_main_viewpager);
        page.setAdapter(
                new PageAdapter(this)
        );
        page.setUserInputEnabled(false);
        TabLayout blankTabLayout = findViewById(R.id.blank_tabLayout);
        new TabLayoutMediator(
                blankTabLayout,
                page,
                (tab, position) -> {
                    tab.setText(data.get(position));
//                    tab.setIcon(R.drawable.ic_launcher_foreground);
                }
        ).attach();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Subscribe
    public void onDisplayRestaurant(DisplayRestaurantEvent event) {
        if (event.restaurant != null)
        {
            Log.i("TestPlace", "id onDisplayRestaurant = (" + event.restaurant.getLatLng().latitude + "," + event.restaurant.getLatLng().longitude + ")");
            DisplayRestaurantActivity.navigate(this, event.restaurant);
        }
    }
}