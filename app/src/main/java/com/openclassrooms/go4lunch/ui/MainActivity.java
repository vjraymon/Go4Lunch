package com.openclassrooms.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.events.DisplayRestaurantEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data.add(getApplicationContext().getString(R.string.map_view));
        data.add(getApplicationContext().getString(R.string.list_view));
        data.add(getApplicationContext().getString(R.string.workmates));
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Log.i("TestPlace", "startSignInActivity()");
        startSignInActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Types the name of the restaurant");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_yourLunch:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_logout:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...

                startSignOutActivity();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private void startSignOutActivity() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    // Restart
                    startSignInActivity();
                });
    }

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
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                Log.i("TestMySelf", "MainActivity.onSignInResult user null");
            }else {
                Log.i("TestMySelf", "MainActivity.onSignInResult name = " + user.getDisplayName());
                Log.i("TestMySelf", "MainActivity.onSignInResult email = " + user.getEmail());
            }
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

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe
    public void onDisplayRestaurant(DisplayRestaurantEvent event) {
        if (event.restaurant != null)
        {
            Log.i("TestPlace", "MainActivity.onDisplayRestaurant id = (" + event.restaurant.getLatLng().latitude + "," + event.restaurant.getLatLng().longitude + ")");
            DisplayRestaurantActivity.navigate(this, event.restaurant);
        }
    }
}