package com.carista.ui.main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.carista.R;
import com.carista.data.realtimedb.models.PostModel;
import com.carista.utils.Device;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PostFragment extends Fragment {

    private PostRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private DocumentSnapshot lastLazyItem;
    private SwipeRefreshLayout swipeRefreshLayout;

    public PostFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new PostRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    adapter.clearData();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String id = String.valueOf(documentSnapshot.get("id"));
                        PostModel postModel = new PostModel(id, documentSnapshot.getData());
                        adapter.addPost(postModel);
                        lastLazyItem = documentSnapshot;
                    }
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1) && recyclerView.canScrollVertically(-1)) {
                    if (lastLazyItem != null) {
                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        firestore.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastLazyItem).limit(5).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    String id = String.valueOf(documentSnapshot.get("id"));
                                    PostModel postModel = new PostModel(id, documentSnapshot.getData());
                                    adapter.addPost(postModel);
                                    lastLazyItem = documentSnapshot;
                                }
                            }
                        });
                    }
                }
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.semi_black_transparent, R.color.violet_color_picker);

        swipeRefreshLayout.setOnRefreshListener(() -> {

            if (!Device.isNetworkAvailable(getContext())) {
                Snackbar.make(getView().findViewById(R.id.list),
                        R.string.network_unavailable,
                        Snackbar.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                return;
            }

            new Handler().postDelayed(() -> firestore.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(5).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    adapter.clearData();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String id = String.valueOf(documentSnapshot.get("id"));
                        PostModel postModel = new PostModel(id, documentSnapshot.getData());
                        adapter.addPost(postModel);
                        lastLazyItem = documentSnapshot;
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
            }), 2000);
        });


    }
}