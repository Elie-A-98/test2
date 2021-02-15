package com.carista.ui.main.fragments;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carista.R;
import com.carista.ui.main.fragments.dummy.DummyContent.DummyItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class UserProfilePostsRecyclerViewAdapter extends RecyclerView.Adapter<UserProfilePostsRecyclerViewAdapter.ViewHolder> {

    private final List<String> mItems;

    public UserProfilePostsRecyclerViewAdapter() {
        mItems = new ArrayList<>();
    }

    public void addPost(String image){
        this.mItems.add(image);
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
        Picasso.get().load(holder.mItem).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public ImageView mImageView;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.user_profile_post);
        }
    }
}