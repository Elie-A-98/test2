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
import com.carista.ui.main.fragments.dummy.DummyContent;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A fragment representing a list of Items.
 */
public class UserProfilePostsFragment extends Fragment {

    private UserProfilePostsRecyclerViewAdapter adapter;
    private String userId;

    public UserProfilePostsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_list, container, false);
        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
        int mColumnCount = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        adapter = new UserProfilePostsRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userId = getActivity().getIntent().getExtras().getString("userId");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("posts").whereEqualTo("userId",userId).addSnapshotListener((value, error) -> {
            for(DocumentSnapshot documentSnapshot : value.getDocuments()){
                String imageUrl = (String) documentSnapshot.get("image");
                adapter.addPost(imageUrl);
            }
        });
    }


}























