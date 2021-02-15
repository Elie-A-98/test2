package com.carista.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.widget.TextView;

import com.carista.R;
import com.carista.utils.FirestoreData;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {

    private CircleImageView userAvatar;
    private TextView postsCount, userNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        String userId = getIntent().getExtras().getString("userId");
        String nickname = getIntent().getExtras().getString("nickname");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(nickname);

        userAvatar = findViewById(R.id.user_profile_avatar);
        postsCount = findViewById(R.id.posts_counter);
        userNickname = findViewById(R.id.user_profile_nickname);

        FirestoreData.setPostUserIconUsername(userId, userAvatar, userNickname);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("posts").whereEqualTo("userId", userId).addSnapshotListener((value, error)->{
            String count = String.valueOf(value.getDocuments().size());
            postsCount.setText(count);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}