package com.carista;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {
    public static final int RC_SIGN_IN = 100;
    private static final int SPLASH_TIME_OUT = 1000;
    private SplashScreen me = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        new Handler().postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
                finish();
            } else {
                // Choose authentication providers
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.PhoneBuilder().build()
                );

                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.auth_theme)
                                .build(),
                        RC_SIGN_IN);
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("users").whereEqualTo("id", FirebaseAuth.getInstance().getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.isEmpty()) {
                            Map<String, Object> user = new HashMap<>();
                            user.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            user.put("nickname", "Anonymous");
                            user.put("avatar", "");
                            firestore.collection("users").add(user);
                            startActivity(new Intent(me, MainActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(me, MainActivity.class));
                            finish();
                        }
                    }
                });

            } else {
                if (response != null && response.getError() != null)
                    Snackbar.make(findViewById(R.id.rootView), getString(R.string.login_error) + "\n Error code: " + response.getError().getErrorCode(), Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
