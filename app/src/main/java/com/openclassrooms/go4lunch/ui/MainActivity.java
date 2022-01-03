package com.openclassrooms.go4lunch.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.openclassrooms.go4lunch.R;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private void onClick(View view) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            Button mDisconnectButton = findViewById(R.id.main_disconnect_button);

            extracted(mDisconnectButton);
        } else {
            finish();
        }

    }

    private void extracted(Button mDisconnectButton) {
        mDisconnectButton.setOnClickListener(v -> {

        });
    }
}