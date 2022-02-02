package com.openclassrooms.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.events.DisplayRestaurantEvent;
import com.openclassrooms.go4lunch.model.Restaurant;
import com.openclassrooms.go4lunch.model.Workmate;
import com.openclassrooms.go4lunch.viewmodel.MyViewModel;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final ArrayList<String> data = new ArrayList<>();
    final int[] tabIcons = {R.drawable.ic_baseline_map_24, R.drawable.ic_baseline_view_list_24, R.drawable.ic_baseline_people_24};

    private DrawerLayout drawer;

    private ImageView userPhoto;
    private TextView userName;
    private TextView userEmail;
    FirebaseUser myself;

    MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        initializeNotification();

        data.add(getApplicationContext().getString(R.string.map_view));
        data.add(getApplicationContext().getString(R.string.list_view));
        data.add(getApplicationContext().getString(R.string.workmates));
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        drawer = findViewById(R.id.drawer_layout);
        userPhoto = findViewById(R.id.user_photo);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        if (userPhoto==null) {
            Log.w("TestMyself", "MainActivity.onCreate userPhoto null");
        }
        if (userEmail==null) {
            Log.w("TestMyself", "MainActivity.onCreate userEmail null");
        }
        if (userName==null) {
            Log.w("TestMyself", "MainActivity.onCreate userName null");
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        Log.i("TestPlace", "startSignInActivity()");
        startSignInActivity();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_yourLunch) {
            // User chose the "Your Lunch" item, show the current restaurant chosen
            if (myViewModel == null) {
                Log.i("TestYourLunch", "myViewModel null");
                return true;
            }
            myViewModel.getWorkmates();
            myViewModel.getRestaurants();
            Workmate w = myViewModel.getMyself();
            if (w == null) {
                Log.i("TestYourLunch", "myViewModel.getMyself() null");
                return true;
            }
            String i = w.getIdRestaurant();
            if (i == null) {
                Log.i("TestYourLunch", "no restaurant joined");
                return true;
            }
            Restaurant restaurant = myViewModel.getRestaurantById(i);
            if (restaurant == null) {
                Log.i("TestYourLunch", "restaurant not founded");
                return true;
            }

            drawer.closeDrawer(GravityCompat.START);

            DisplayRestaurantActivity.navigate(this, restaurant);
            return true;

        } else if (item.getItemId() == R.id.action_settings) {
            // User chose the "Settings" item, show the app settings UI...
            // TODO
            return true;

        } else if (item.getItemId() == R.id.action_logout) {
            // User chose the "Logout" action, restart the authentification
            drawer.closeDrawer(GravityCompat.START);

            startSignOutActivity();
            return true;

        } else {
            // If we got here, the user's action was not recognized.
            // TODO ? Invoke the superclass to handle it.
            return true;

        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

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
            } else {
                Log.i("TestMySelf", "MainActivity.onSignInResult name = " + user.getDisplayName());
                Log.i("TestMySelf", "MainActivity.onSignInResult email = " + user.getEmail());
                if (user.getPhotoUrl() == null) {
                    Log.i("TestMySelf", "MainActivity.onSignInResult myself.getPhotoUrl() null");
                } else {
                    Log.i("TestMySelf", "MainActivity.onSignInResult myself.getPhotoUrl()" + user.getPhotoUrl().toString());
                    if (user.getPhotoUrl() != null) {
                        userPhoto = findViewById(R.id.user_photo);
                        Picasso.with(this).load(user.getPhotoUrl()).into(userPhoto);
                    }
                    userName = findViewById(R.id.user_name);
                    userEmail = findViewById(R.id.user_email);
                    if ((userName==null) || (userEmail==null)) {
                        Log.w("TestMyself", "init pas faite!");
                    } else {
                        Log.w("TestMyself", "init pas faite!");
                        userName.setText(user.getDisplayName());
                        userEmail.setText(user.getEmail());
                    }
                }
 //               initializeNotification();
            }

            //3 - Configure ViewPager
            this.configureViewPager();
            myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        } else {
            finish();
        }
    }
/*
    private void initializeNotification() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TestNotification", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("TestNotification", msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("TestNotification", "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        binding.subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TestNotification", "Subscribing to weather topic");
                // [START subscribe_topics]
                FirebaseMessaging.getInstance().subscribeToTopic("weather")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String msg = getString(R.string.msg_subscribed);
                                if (!task.isSuccessful()) {
                                    msg = getString(R.string.msg_subscribe_failed);
                                }
                                Log.d("TestNotification", msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                // [END subscribe_topics]
            }
        });

        binding.logTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get token
                // [START log_reg_token]
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w("TestNotification", "Fetching FCM registration token failed", task.getException());
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();

                                // Log and toast
                                String msg = getString(R.string.msg_token_fmt, token);
                                Log.d("TestNotification", msg);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                // [END log_reg_token]
            }
        });
    }

    private void sendRegistrationToServer(String token) {}
*/
    private void configureViewPager(){
        ViewPager2 page = findViewById(R.id.activity_main_viewpager);
        TabLayout blankTabLayout = findViewById(R.id.blank_tabLayout);
        // 1 - Get ViewPager from layout
        page.setAdapter(
                new PageAdapter(this)
        );
        page.setUserInputEnabled(false);
        new TabLayoutMediator(
                blankTabLayout,
                page,
                (tab, position) -> {
                    tab.setText(data.get(position));
                    tab.setIcon(tabIcons[position]);
//                    tab.setIcon(R.drawable.ic_launcher_foreground);
                }
        ).attach();

        setTitle(getString(R.string.map_fragment_title));
        blankTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (Objects.equals(tab.getText(), data.get(0))) {
                    setTitle(getString(R.string.map_fragment_title));
                } else if (Objects.equals(tab.getText(), data.get(1))) {
                    setTitle(getString(R.string.restaurant_fragment_title));
                } else if (Objects.equals(tab.getText(), data.get(2))) {
                    setTitle(getString(R.string.workmate_fragment_title));
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
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