package com.carista.ui.main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carista.R;
import com.carista.data.realtimedb.models.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserPostsFragment extends Fragment {
    private UserPostsRecyclerViewAdapter adapter;
    private boolean isUserLikesOnly;

    public UserPostsFragment() {
        // Required empty public constructor
    }

    public UserPostsFragment(boolean isUserLikesOnly) {

        this.isUserLikesOnly = isUserLikesOnly;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_posts_list, container, false);
        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
        int mColumnCount = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        adapter = new UserPostsRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isUserLikesOnly) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Query q = firestore.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING);
            q.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    adapter.clearData();
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        if (documentSnapshot.get("userId").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            String id = String.valueOf(documentSnapshot.get("id"));
                            adapter.addPost(new PostModel(id, documentSnapshot.getData()));
                        }
                    }
                }
            });
        } else {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    String id = "";
                    adapter.clearData();
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        ArrayList<String> likes = (ArrayList<String>) documentSnapshot.get("likes");
                        if (likes.contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            id = String.valueOf(documentSnapshot.get("id"));
                            adapter.addPost(new PostModel(id, documentSnapshot.getData()));
                        }

                    }
                }
            });
        }
    }
}