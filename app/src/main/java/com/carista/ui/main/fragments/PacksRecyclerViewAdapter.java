package com.carista.ui.main.fragments;

import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.view.ViewGroup;

import android.view.View;
import android.widget.TextView;


import com.carista.R;

import com.carista.data.realtimedb.models.PackModel;
import com.carista.ui.main.PackDetails;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

public class PacksRecyclerViewAdapter extends RecyclerView.Adapter<PacksRecyclerViewAdapter.ViewHolder> {

    private final List<PackModel> items;
    AlertDialog.Builder alertTitle;
    public PacksRecyclerViewAdapter() {
        this.items = new ArrayList<>();
    }

    public void addPack(PackModel... packModel) {
        this.items.addAll(Arrays.asList(packModel));
        notifyDataSetChanged();
    }

    @Override
    public PacksRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_pack_item, parent, false);
        return new PacksRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public void onBindViewHolder(final PacksRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = this.items.get(position);
        holder.mTitleView.setText(this.items.get(position).title);
        Picasso.get().load(items.get(position).icon).fit().centerCrop().into(holder.mImageView);
        holder.mImageView.setOnClickListener(view -> {
            //send packId to PackDetails
            Intent intent = new Intent(view.getContext(), PackDetails.class);
            intent.putExtra("packId",this.items.get(position).id);
            view.getContext().startActivity(intent);
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public PackModel mItem;
        public final ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.post_title);
            mImageView = view.findViewById(R.id.post_image);
        }
    }
    public void clearData() {
        this.items.clear();
        notifyDataSetChanged();
    }
}
