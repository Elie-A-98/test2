package com.carista.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.carista.R;

public class FollowUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_users);

        int viewType = getIntent().getExtras().getInt("ViewType");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(viewType == UserProfileActivity.FOLLOWERS_VIEW)
            getSupportActionBar().setTitle("Followers");
        else
            getSupportActionBar().setTitle("Following");

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}