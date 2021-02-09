package com.carista.photoeditor.photoeditor.tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carista.R;
import com.carista.data.StickerPack;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.2
 * @since 5/23/2018
 */
public class EditingToolsAdapter extends RecyclerView.Adapter<EditingToolsAdapter.ViewHolder> {

    private List<ToolModel> mToolList = new ArrayList<>();
    private OnItemSelected mOnItemSelected;

    public EditingToolsAdapter(OnItemSelected onItemSelected) {
        mOnItemSelected = onItemSelected;
        mToolList.add(new ToolModel("Brush", R.drawable.ic_brush));
        mToolList.add(new ToolModel("Text", R.drawable.ic_text));
        mToolList.add(new ToolModel("Eraser", R.drawable.ic_eraser));
        mToolList.add(new ToolModel("Filter", R.drawable.ic_photo_filter));
        mToolList.add(new ToolModel("Emoji", R.drawable.ic_insert_emoticon));
        mToolList.add(new ToolModel("Sticker", R.drawable.ic_sticker));
        mToolList.add(new ToolModel("Import", R.drawable.ic_add_photo));
        mToolList.add(new ToolModel("Crop", R.drawable.ic_crop));
    }

    public interface OnItemSelected {
        void onToolSelected(int toolType);
    }

    class ToolModel {
        private String mToolName;
        private String imageURL;
        private int mToolIcon;

        ToolModel(String toolName, int toolIcon) {
            mToolName = toolName;
            mToolIcon = toolIcon;
        }

        ToolModel(String toolName, String imageURL) {
            mToolName = toolName;
            this.imageURL = imageURL;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_editing_tools, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolModel item = mToolList.get(position);
        holder.txtTool.setText(item.mToolName);
        if (item.imageURL != null)
            Picasso.get().load(item.imageURL).into(holder.imgToolIcon);
        else holder.imgToolIcon.setImageResource(item.mToolIcon);
    }

    @Override
    public int getItemCount() {
        return mToolList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgToolIcon;
        TextView txtTool;

        ViewHolder(View itemView) {
            super(itemView);
            imgToolIcon = itemView.findViewById(R.id.imgToolIcon);
            txtTool = itemView.findViewById(R.id.txtTool);
            itemView.setOnClickListener(v -> mOnItemSelected.onToolSelected(getLayoutPosition()));
        }
    }

    public void addStickerPack(StickerPack pack) {
        mToolList.add(new ToolModel(pack.title, pack.icon));
        notifyItemChanged(getItemCount() - 1);
    }
}
