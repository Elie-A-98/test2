package com.carista.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import com.carista.R;
import com.carista.SplashScreen;
import com.carista.utils.FirestoreData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostActivity extends AppCompatActivity {

    private ImageView postImage;
    private CheckBox likeCheckbox, commentCheckbox;
    private TextView likesCounterText, titleText, usernameText;
    private CardView cardView;
    private CircleImageView userIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.post_view_title);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent i = new Intent(getBaseContext(), SplashScreen.class);
            startActivity(i);
            finish();
        }

        postImage = findViewById(R.id.view_post_image);
        likeCheckbox= findViewById(R.id.view_like_checkbox);
        commentCheckbox = findViewById(R.id.view_comment_checkbox);
        likesCounterText = findViewById(R.id.view_likes_counter);
        titleText = findViewById(R.id.view_post_title);
        cardView = findViewById(R.id.view_post_card);
        userIcon = findViewById(R.id.post_user_post_icon);
        usernameText = findViewById(R.id.post_top_post_username);

        String postId = null;

        if(getIntent().getData()!=null)
            postId = getIntent().getData().toString().split("/post/")[1];
        else if(getIntent().getExtras().getString("postId")!=null || !getIntent().getExtras().getString("postId").isEmpty()){
            postId=getIntent().getExtras().getString("postId");
        }

        String finalPostId = postId;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("posts").whereEqualTo("id",postId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                    String image = (String) documentSnapshot.get("image");
                    Picasso.get().load(image).resize(cardView.getWidth(), 600).centerCrop().into(postImage);

                    FirestoreData.getLikesCount(finalPostId, likesCounterText);
                    FirestoreData.isLikedByUser(finalPostId,likeCheckbox,likesCounterText);

                    String userId= (String) documentSnapshot.get("userId");
                    String title= (String) documentSnapshot.get("title");
                    FirestoreData.setPostNicknameTitle(userId, title, titleText);
                    FirestoreData.setPostUserIconUsername(userId, userIcon, usernameText);
                }
            }
        });

        postImage.setOnClickListener(view -> {
            return;
        });

        likeCheckbox.setOnClickListener(view -> {
            if(likeCheckbox.isChecked()){
                FirestoreData.addLike(finalPostId);
            }
            else{
                FirestoreData.removeLike(finalPostId);
            }
        });

        commentCheckbox.setOnClickListener(view -> {
            Intent intent = new Intent(PostActivity.this,CommentsActivity.class);
            intent.putExtra("postId",finalPostId);
            startActivity(intent);
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
