package com.mydocreader.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.List;

public class RecentFilesAdapter extends RecyclerView.Adapter<RecentFilesAdapter.ViewHolder> {

    private List<String> files;
    private OnFileClickListener listener;

    public interface OnFileClickListener {
        void onFileClick(String path);
    }

    public RecentFilesAdapter(List<String> files, OnFileClickListener listener) {
        this.files = files;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_recent_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String path = files.get(position);
        File file = new File(path);
        holder.fileName.setText(file.getName());
        holder.filePath.setText(path);

        String lower = path.toLowerCase();
        int iconRes;
        int colorRes;
        Context ctx = holder.itemView.getContext();
        if (lower.endsWith(".pdf")) {
            iconRes = android.R.drawable.ic_menu_agenda;
            holder.fileIcon.setColorFilter(ctx.getResources().getColor(R.color.pdf_color));
        } else if (lower.endsWith(".xls") || lower.endsWith(".xlsx")) {
            iconRes = android.R.drawable.ic_menu_sort_by_size;
            holder.fileIcon.setColorFilter(ctx.getResources().getColor(R.color.excel_color));
        } else if (lower.endsWith(".doc") || lower.endsWith(".docx")) {
            iconRes = android.R.drawable.ic_menu_edit;
            holder.fileIcon.setColorFilter(ctx.getResources().getColor(R.color.word_color));
        } else if (lower.endsWith(".txt")) {
            iconRes = android.R.drawable.ic_menu_manage;
            holder.fileIcon.setColorFilter(ctx.getResources().getColor(R.color.text_color));
        } else {
            iconRes = android.R.drawable.ic_menu_gallery;
            holder.fileIcon.setColorFilter(ctx.getResources().getColor(R.color.image_color));
        }
        holder.fileIcon.setImageResource(iconRes);

        holder.itemView.setOnClickListener(v -> listener.onFileClick(path));
    }

    @Override
    public int getItemCount() { return files.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView fileIcon;
        TextView fileName, filePath;
        ViewHolder(View itemView) {
            super(itemView);
            fileIcon = itemView.findViewById(R.id.fileIcon);
            fileName = itemView.findViewById(R.id.fileName);
            filePath = itemView.findViewById(R.id.filePath);
        }
    }
}
