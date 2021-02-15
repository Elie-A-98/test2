package com.carista.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.carista.R;
import com.carista.utils.FirestoreData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class UserProfileActivity extends AppCompatActivity {

    private CircleImageView userAvatar;
    private TextView postsCount, followersCount, followingCount, userNickname;
    private Button followButton;

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
        followersCount = findViewById(R.id.followers_counter);
        followingCount = findViewById(R.id.following_counter);
        userNickname = findViewById(R.id.user_profile_nickname);
        followButton = findViewById(R.id.follow_button);

        FirestoreData.setPostUserIconUsername(userId, userAvatar, userNickname);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("posts").whereEqualTo("userId", userId).addSnapshotListener((value, error)->{
            String count = String.valueOf(value.getDocuments().size());
            postsCount.setText(count);
        });

        if(userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            followButton.setEnabled(false);

        firestore.collection("users").whereEqualTo("id", userId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                    HashMap<String, Object> updates = new HashMap<>();
                    ArrayList<String> followers;
                    if(followButton.getText().toString().equals("Follow")) {
                        followers = (ArrayList<String>) documentSnapshot.get("followers");
                        if (followers == null) {
                            followers = new ArrayList<>();
                            return;
                        }
                        if(followers.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            followButton.setText("Followed");
                        }
                        followersCount.setText(String.valueOf(followers.size()));
                    }
                }
            }
        });


        followButton.setOnClickListener(view -> {
            if(userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                return;

            firestore.collection("users").whereEqualTo("id", userId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                            HashMap<String, Object> updates = new HashMap<>();
                            ArrayList<String> followers;
                            if(followButton.getText().toString().equals("Follow")){
                                followers = (ArrayList<String>) documentSnapshot.get("followers");
                                if(followers==null){
                                    followers = new ArrayList<>();
                                }
                                followers.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                updates.put("followers", followers);
                                firestore.collection("users").document(documentSnapshot.getId()).update(updates);
                                followButton.setText("Followed");
                                followersCount.setText(String.valueOf(followers.size()));
                            }
                            else{
                                followers = (ArrayList<String>) documentSnapshot.get("followers");
                                followers.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                updates.put("followers", followers);
                                firestore.collection("users").document(documentSnapshot.getId()).update(updates);
                                followButton.setText("Follow");
                                followersCount.setText(String.valueOf(followers.size()));
                            }
                        }
                    }
                }
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}








