package com.carista.ui.main.fragments;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.carista.R;
import com.carista.data.realtimedb.models.PostModel;
import com.carista.ui.main.CommentsActivity;
import com.carista.utils.FirestoreData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.ViewHolder> {

    private final List<PostModel> items;

    public PostRecyclerViewAdapter() {
        this.items = new ArrayList<>();
    }

    public void addPost(PostModel postModel) {
        this.items.add(postModel);
        notifyDataSetChanged();
    }

    public void addPost(List<PostModel> postModels) {
        this.items.addAll(postModels);
        notifyDataSetChanged();
    }

    public void removePost(int position) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference("posts");

        PostModel m = this.items.get(position);
        StorageReference imageRef = storageRef.child(m.id + ".jpg");

        imageRef.delete().addOnSuccessListener(aVoid -> FirestoreData.removePost(m.id));
        this.items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);

        return new ViewHolder(view);
    }

//    @Override
//    public void onClick(View v) {
//        if(v.equals(imgViewRemoveIcon)) {
//            removePost(getAdapterPosition());
//        }
//    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = this.items.get(position);
        holder.mLikeCheckbox.setChecked(false);
        FirestoreData.setPostNicknameTitle(this.items.get(position).userId, this.items.get(position).title, holder.mTitleView);
        FirestoreData.getLikesCount(this.items.get(position).id, holder.mLikeCounterView);
        FirestoreData.isLikedByUser(this.items.get(position).id, holder.mLikeCheckbox, holder.mLikeCounterView);

        holder.mimgViewRemoveIcon.setOnClickListener(v -> {
            int position1 = holder.getAdapterPosition();
            if (this.items.get(position1).userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                removePost(position1);
        });

        holder.mLikeCheckbox.setOnClickListener(view -> {
            int likePosition = holder.getAdapterPosition();
            if (holder.mLikeCheckbox.isChecked()) {
                FirestoreData.addLike(this.items.get(likePosition).id);
            } else {
                FirestoreData.removeLike(this.items.get(likePosition).id);
            }
        });

        holder.mCommentCheckbox.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), CommentsActivity.class);
            intent.putExtra("postId", this.items.get(position).id);
            view.getContext().startActivity(intent);
        });

        Picasso.get().load(items.get(position).image).resize(holder.mCardView.getWidth(), 600).centerCrop().into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final ImageView mImageView;
        public ImageView mimgViewRemoveIcon;
        public final CardView mCardView;
        public PostModel mItem;
        public TextView mLikeCounterView;
        public CheckBox mLikeCheckbox, mCommentCheckbox;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.post_title);
            mImageView = view.findViewById(R.id.post_image);
            mimgViewRemoveIcon = view.findViewById(R.id.crossButton);
            mCardView = view.findViewById(R.id.post_card);
            mLikeCheckbox = view.findViewById(R.id.like_checkbox);
            mLikeCounterView = view.findViewById(R.id.likes_counter);
            mCommentCheckbox = view.findViewById(R.id.comment_checkbox);
        }
    }

    public void clearData() {
        this.items.clear();
        notifyDataSetChanged();
    }
}
