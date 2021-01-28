package com.carista.ui.main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carista.R;
import com.carista.data.db.AppDatabase;
import com.carista.data.realtimedb.models.CommentModel;
import com.carista.data.realtimedb.models.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static com.carista.photoeditor.photoeditor.TextEditorDialogFragment.TAG;

public class UserPostsFragment extends Fragment {
    private UserPostsRecyclerViewAdapter adapter;

    public UserPostsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_user_posts_list, container, false);
        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
        int mColumnCount = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        adapter = new UserPostsRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        return view ;
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference posts = db.collection("posts");
        posts.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                adapter.clearData();
                try {
                    Thread thread = new Thread(() -> AppDatabase.getInstance().postDao().deleteAll());
                    thread.start();
                    thread.join();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                for (QueryDocumentSnapshot post : value) {
                    if(post.get("userId").toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        String id = post.getId();
                        PostModel postModel = new PostModel(id, post.getData());
                        adapter.addPost(postModel);
                    }
                    //AppDatabase.executeQuery(() -> AppDatabase.getInstance().postDao().insertAll(postModel));
                }
            }
        });
    }
}