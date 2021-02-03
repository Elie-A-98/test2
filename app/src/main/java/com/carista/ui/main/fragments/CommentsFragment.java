package com.carista.ui.main.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carista.R;
import com.carista.data.realtimedb.models.CommentModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;


public class CommentsFragment extends Fragment {

    private CommentsRecyclerViewAdapter adapter;


    public CommentsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments_item_list, container, false);
        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.comments_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CommentsRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        String postId=getActivity().getIntent().getExtras().getString("postId");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("posts").whereEqualTo("id", postId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot documentSnapshot: value.getDocuments()){
                    ArrayList<HashMap<String,Object>> comments= (ArrayList<HashMap<String, Object>>) documentSnapshot.get("comments");
                    adapter.clearData();
                    for(int i=0;i<comments.size();i++){
                        adapter.addComment(new CommentModel((comments.get(i))));
                    }
                }
            }
        });
    }
}