package com.carista.ui.main.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carista.R;
import com.carista.ui.main.UserProfileActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FollowUserFragment extends Fragment {

    private FollowUserRecyclerViewAdapter adapter;


    public FollowUserFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_follow_user_list, container, false);
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new FollowUserRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int viewType = getActivity().getIntent().getExtras().getInt("ViewType");
        String userId = getActivity().getIntent().getExtras().getString("userId");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if(viewType == UserProfileActivity.FOLLOWERS_VIEW){
            firestore.collection("users").whereEqualTo("id", userId).get().addOnCompleteListener(task -> {
               if(task.isSuccessful()){
                   adapter.clearData();
                   for(DocumentSnapshot documentSnapshot:task.getResult()){
                       ArrayList<String> followers = (ArrayList<String>) documentSnapshot.get("followers");
                       if(followers == null)
                           return;
                       for(int i=0; i<followers.size(); i++){
                           adapter.addUser(followers.get(i));
                       }
                   }
               }
            });
        }
        else{
            firestore.collection("users").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    adapter.clearData();
                    for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
                        String id = (String) documentSnapshot.get("id");
                        ArrayList<String> followers;
                        followers = (ArrayList<String>) documentSnapshot.get("followers");
                        if (followers == null) {
                            continue;
                        }
                        if(followers.contains(userId)){
                            adapter.addUser(id);
                        }
                    }
                }
            });
        }


    }
}