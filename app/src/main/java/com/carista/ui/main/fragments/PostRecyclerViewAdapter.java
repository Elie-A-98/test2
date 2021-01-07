package com.carista.ui.main.fragments;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carista.R;
import com.carista.data.realtimedb.models.PostModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a.
 * TODO: Replace the implementation with code for your data type.
 */
public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder> {

    private final List<PostModel> items;

    public PostRecyclerViewAdapter() {
        this.items=new ArrayList<>();
    }

    public void addPost(PostModel postModel){
        this.items.add(postModel);
        notifyItemChanged(this.items.size() + 1);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = items.get(position);
        holder.mTitleView.setText(items.get(position).title);
        Picasso.get().load(items.get(position).image).resize(holder.mCardView.getWidth(),600).centerCrop().into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final ImageView mImageView;
        public final CardView mCardView;
        public PostModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.post_title);
            mImageView = view.findViewById(R.id.post_image);
            mCardView = view.findViewById(R.id.post_card);
        }
    }

    public void clearData(){
        this.items.clear();
        notifyDataSetChanged();
    }
}