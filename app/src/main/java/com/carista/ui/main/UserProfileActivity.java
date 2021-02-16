package com.carista.ui.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.widget.TextView;

import com.carista.R;
import com.carista.data.realtimedb.models.PostModel;
import com.carista.ui.main.fragments.PostRecyclerViewAdapter;
import com.carista.utils.Device;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private CircleImageView UserProfilePicture;
    private TextView UserProfileName;
    private String UserId;
    private PostRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        UserProfilePicture=findViewById(R.id.user_profile);
        UserProfileName=findViewById(R.id.user_nickname);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PostRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        String postId=getIntent().getExtras().getString("postId");
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("posts").whereEqualTo("id",postId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot documentSnapshot: value.getDocuments()){
                    UserId= String.valueOf(documentSnapshot.get("userId"));
                    if(UserId!=null) {
                        firestore.collection("users").whereEqualTo("id",UserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                for(DocumentSnapshot documentSnapshot: value.getDocuments()){
                                    String userProfile= String.valueOf(documentSnapshot.get("avatar"));
                                    String userNickname= String.valueOf(documentSnapshot.get("nickname"));
                                    if(userProfile!=null && userNickname!=null && !userProfile.isEmpty()) {
                                        UserProfileName.setText(userNickname);
                                        Picasso.get().load(userProfile).into(UserProfilePicture);
                                        getSupportActionBar().setTitle(userNickname);

                                    }
                                }
                            }
                        });

                        firestore.collection("posts").whereEqualTo("userId",UserId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    adapter.clearData();
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        String id = String.valueOf(documentSnapshot.get("id"));
                                        PostModel postModel = new PostModel(id, documentSnapshot.getData());
                                        adapter.addPost(postModel);
                                    }
                                }
                            }
                        });
                        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.semi_black_transparent, R.color.violet_color_picker);

                        swipeRefreshLayout.setOnRefreshListener(() -> {

                            if (!Device.isNetworkAvailable(swipeRefreshLayout.getContext())) {
                                Snackbar.make(findViewById(R.id.list),
                                        R.string.network_unavailable,
                                        Snackbar.LENGTH_SHORT).show();
                                swipeRefreshLayout.setRefreshing(false);
                                return;
                            }

                            new Handler().postDelayed(() -> firestore.collection("posts").whereEqualTo("userId",UserId).get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    adapter.clearData();
                                    for (QueryDocumentSnapshot documentSnapshot2 : task.getResult()) {
                                        String id = String.valueOf(documentSnapshot2.get("id"));
                                        PostModel postModel = new PostModel(id, documentSnapshot2.getData());
                                        adapter.addPost(postModel);
                                    }
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }), 2000);
                        });
                    }
                }
            }
        });
    }
}