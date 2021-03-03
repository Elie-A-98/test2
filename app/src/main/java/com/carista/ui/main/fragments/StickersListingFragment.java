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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.carista.R;
import com.carista.data.realtimedb.models.StickerModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.carista.ui.main.PackDetails;

import static java.lang.String.valueOf;

public class StickersListingFragment extends Fragment {

    private StickersRecyclerViewAdapter adapter;
    private String packId;

    public StickersListingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stickers_list, container, false);
        PackDetails packDet = (PackDetails)getActivity();
        packId = packDet.packId;
        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);
        int mColumnCount = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        adapter = new StickersRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("stickers").whereEqualTo("id", packId).addSnapshotListener((value, error) -> {
            String image,name;
            adapter.clearData();
            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                List<Map<String, String>> items = new ArrayList<Map<String, String>>();
                items = (List<Map<String, String>>)documentSnapshot.get("items");
                for(int i=0; i < items.size(); i++){
                    image = valueOf(items.get(i).get("image"));
                    name = valueOf(items.get(i).get("name"));
                    adapter.addSticker(new StickerModel(image, name));
                }
                adapter.setPackId(packId);
            }
        });
    }

}