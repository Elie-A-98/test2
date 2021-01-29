package com.carista.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.carista.R;
import com.carista.SplashScreen;
import com.google.firebase.auth.FirebaseAuth;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent i = new Intent(getBaseContext(), SplashScreen.class);
            startActivity(i);
            finish();
        }

        String postId = getIntent().getData().toString().split("/post/")[1];
        
    }
}
