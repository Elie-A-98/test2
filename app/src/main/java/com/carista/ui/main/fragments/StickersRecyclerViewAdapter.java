package com.carista.ui.main.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;


import android.view.View;

import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import com.carista.R;
import com.carista.data.realtimedb.models.StickerModel;

import com.carista.ui.main.StickerDetails;

import com.squareup.picasso.Picasso;


public class StickersRecyclerViewAdapter extends RecyclerView.Adapter<StickersRecyclerViewAdapter.ViewHolder> {

    private final List<StickerModel> items;
    public String packId;
    AlertDialog.Builder alertTitle;
    public StickersRecyclerViewAdapter() {
        this.items = new ArrayList<>();
    }

    public void setPackId(String packId){
        this.packId = packId;
    }

    public void addSticker(StickerModel... stickerModel) {
        this.items.addAll(Arrays.asList(stickerModel));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public StickersRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_sticker_item, parent, false);
        return new StickersRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StickersRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = this.items.get(position);
        holder.mNameView.setText(this.items.get(position).name);
        Picasso.get().load(items.get(position).image).fit().centerCrop().into(holder.mImageView);

        holder.mImageView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), StickerDetails.class);
            intent.putExtra("packId", packId);
            intent.putExtra("stickerPosition", position);
            view.getContext().startActivity(intent);

        });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public StickerModel mItem;
        public final ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.sticker_title);
            mImageView = view.findViewById(R.id.sticker_image);
        }
    }
    public void clearData() {
        this.items.clear();
        notifyDataSetChanged();
    }
}
