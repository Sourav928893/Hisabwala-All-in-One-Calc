package com.example.hisabwalaallinonecalc.main.toolbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hisabwalaallinonecalc.R;
import com.example.hisabwalaallinonecalc.main.ItemClick;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

/**
 * Toolbox Adapter for RecyclerView (supports both Grid & List layouts).
 */
public class ToolBoxAdapter extends RecyclerView.Adapter<ToolBoxAdapter.ViewHolder> {
    private final List<ToolBoxItem> list;
    private final ItemClick itemClick;
    private final boolean isGrid;

    public ToolBoxAdapter(List<ToolBoxItem> list, boolean isGrid, ItemClick itemClick) {
        this.list = list;
        this.itemClick = itemClick;
        this.isGrid = isGrid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isGrid) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_item_toolbox, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_toolbox, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToolBoxItem item = list.get(position);

        holder.textView.setText(item.title());
        holder.imageView.setImageDrawable(item.drawable());

        holder.itemView.setOnClickListener(v -> {
            if (itemClick != null) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    itemClick.onClick(list.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;
        final ShapeableImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
