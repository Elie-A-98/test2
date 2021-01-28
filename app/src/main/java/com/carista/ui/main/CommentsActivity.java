package com.carista.ui.main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

import com.carista.R;
import com.carista.data.realtimedb.models.CommentModel;
import com.carista.utils.Data;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.carista.photoeditor.photoeditor.TextEditorDialogFragment.TAG;

public class CommentsActivity extends AppCompatActivity {

    private CircleImageView myCommentAvatar;
    private EditText myComment;
    private CheckBox postComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.comments_title);

        myCommentAvatar=findViewById(R.id.my_comment_user_avatar);
        myComment=findViewById(R.id.my_comment);
        postComment=findViewById(R.id.comment_post_button);

        String postId=getIntent().getExtras().getString("postId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference posts = db.collection("users").document(user.getUid());
        posts.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                String avatar=snapshot.get("avatar").toString();
                if (avatar != null)
                    Picasso.get().load(avatar.toString()).into(myCommentAvatar);
            }
        });

        postComment.setOnClickListener(view -> {
            String comment=myComment.getText().toString();
            if(comment.isEmpty())
                return;
            CommentModel commentModel=new CommentModel(comment, FirebaseAuth.getInstance().getCurrentUser().getUid());
            Data.addComment(postId,commentModel);
            myComment.setText("");
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}