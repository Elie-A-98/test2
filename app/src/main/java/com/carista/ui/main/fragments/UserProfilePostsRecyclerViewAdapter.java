package com.carista.ui.main.fragments;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carista.R;
import com.carista.ui.main.PostActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class UserProfilePostsRecyclerViewAdapter extends RecyclerView.Adapter<UserProfilePostsRecyclerViewAdapter.ViewHolder> {

    private final List<String> mItems;
    private final List<String> mPostIds;

    public UserProfilePostsRecyclerViewAdapter() {
        mItems = new ArrayList<>();
        mPostIds = new ArrayList<>();
    }

    public void addPost(String image, String postId){
        this.mItems.add(image);
        this.mPostIds.add(postId);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_profile_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mItems.get(position);
        holder.mPostId = mPostIds.get(position);

        Picasso.get().load(holder.mItem).into(holder.mImageView);

        holder.mImageView.setOnClickListener(view->{
            Intent intent = new Intent(view.getContext(), PostActivity.class);
            intent.putExtra("postId", holder.mPostId);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public ImageView mImageView;
        public String mItem, mPostId;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.user_profile_post);
        }
    }
}