package com.carista.ui.main.fragments;

import android.content.Context;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;


import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.carista.R;

import com.carista.data.realtimedb.models.PackModel;

import com.carista.data.StickerItem;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import static java.lang.String.valueOf;

public class PacksListingFragment extends Fragment {

    private PacksRecyclerViewAdapter adapter;

    public PacksListingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_packs_list, container, false);
        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
        int mColumnCount = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        adapter = new PacksRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("stickers").addSnapshotListener((value, error) -> {
            String id,icon,title;
            adapter.clearData();
            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                List<StickerItem> items = (List<StickerItem>) documentSnapshot.get("items");
                icon = valueOf(documentSnapshot.get("icon"));
                title = valueOf(documentSnapshot.get("title"));
                id = valueOf(documentSnapshot.get("id"));
                adapter.addPack(new PackModel(id,icon, title));

            }
        });

    }
}
