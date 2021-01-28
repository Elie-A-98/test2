package com.carista.ui.main.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carista.R;
import com.carista.data.db.AppDatabase;
import com.carista.data.realtimedb.models.PostModel;
import com.carista.utils.Device;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;
import static com.carista.photoeditor.photoeditor.TextEditorDialogFragment.TAG;

public class PostFragment extends Fragment {

    private PostRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private String lastLazyItem;
    private String lastLazyKey;
    private long oldtimestamp;

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference query = db.collection("posts");

        if (Device.isNetworkAvailable(getContext())) {

            query.orderBy("timestamp", Query.Direction.DESCENDING).limit(3).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error != null){
                        Log.w("LIST_POSTS", "listen:error", error);
                        return;
                    }
                    adapter.clearData();
                    try {
                        Thread thread = new Thread(() -> AppDatabase.getInstance().postDao().deleteAll());
                        thread.start();
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    for(DocumentSnapshot doc : value.getDocuments()){
                        String id = doc.getId();
                        PostModel postModel = new PostModel(id, doc.getData());
                        oldtimestamp = postModel.timestamp;
                        adapter.addPost(postModel);
                        AppDatabase.executeQuery(() -> AppDatabase.getInstance().postDao().insertAll(postModel));
                    }


                }
            });

        }

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                this.currentScrollState = newState;
                this.isScrollCompleted();
            }


            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }


            private void isScrollCompleted () {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {
                    query.orderBy("timestamp", Query.Direction.DESCENDING).startAt(oldtimestamp).limit(3).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(error != null){
                                Log.w("LIST_POSTS", "listen:error", error);
                                return;
                            }
                            for(DocumentSnapshot doc : value.getDocuments()){
                                String id = doc.getId();
                                PostModel postModel = new PostModel(id, doc.getData());
                                if(postModel.timestamp == oldtimestamp)
                                    continue;
                                oldtimestamp = postModel.timestamp;
                                adapter.addPost(postModel);
                                AppDatabase.executeQuery(() -> AppDatabase.getInstance().postDao().insertAll(postModel));
                            }
                        }
                    });

                }
            };
        });


        if (!Device.isNetworkAvailable(getContext())) {
            adapter.clearData();
            AppDatabase.executeQuery(() -> adapter.addPost(AppDatabase.getInstance().postDao().getAll()));
            Snackbar.make(getView().findViewById(R.id.list),
                    R.string.network_unavailable,
                    Snackbar.LENGTH_SHORT).show();
        }
    }
}
