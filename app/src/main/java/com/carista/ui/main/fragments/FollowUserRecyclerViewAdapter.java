package com.carista.ui.main.fragments;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carista.R;
import com.carista.data.realtimedb.models.UserModel;
import com.carista.ui.main.UserProfileActivity;
import com.carista.utils.FirestoreData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class FollowUserRecyclerViewAdapter extends RecyclerView.Adapter<FollowUserRecyclerViewAdapter.ViewHolder> {

    private final List<String> mItems;

    public FollowUserRecyclerViewAdapter() {
        this.mItems = new ArrayList<>();
    }

    public void addUser(String userId){
        this.mItems.add(userId);
        notifyDataSetChanged();
    }

    public void clearData(){
        this.mItems.clear();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_follow_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = this.mItems.get(position);

        FirestoreData.setPostUserIconUsername(holder.mItem, holder.mImageView, holder.mNickname);

        holder.mNickname.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), UserProfileActivity.class);
            intent.putExtra("userId", holder.mItem);
            intent.putExtra("nickname", holder.mNickname.getText().toString());
            view.getContext().startActivity(intent);
        });

        holder.mImageView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), UserProfileActivity.class);
            intent.putExtra("userId", holder.mItem);
            intent.putExtra("nickname", holder.mNickname.getText().toString());
            view.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public String mItem;
        public final CircleImageView mImageView;
        public final TextView mNickname;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.follow_user_avatar);
            mNickname = view.findViewById(R.id.follow_user_nickname);
        }

    }
}