package com.example.teamv.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamv.R;
import com.example.teamv.object.AttachedFile;

import java.util.List;

public class AttachedFileAdapter extends RecyclerView.Adapter<AttachedFileAdapter.AttachedFileViewHolder> {
    private List<AttachedFile> attachedFileList;

    public AttachedFileAdapter(List<AttachedFile> attachedFileList) {
        this.attachedFileList = attachedFileList;
    }

    @NonNull
    @Override
    public AttachedFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attached_file, parent, false);
        return new AttachedFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachedFileViewHolder holder, int position) {
        AttachedFile attachedFile = attachedFileList.get(position);

        if (attachedFile == null)
            return;

        holder.tvAttachedFileName.setText(attachedFile.getName());
        holder.tvAttachedFileCreatedAt.setText(attachedFile.getCreated_at());
        holder.tvAttachedFileSize.setText(attachedFile.getSize() + " MB");

        // click vào file sẽ dẫn đến link download
        holder.rlAttachedFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType("*/*");
                intent.setData(Uri.parse(attachedFile.getUrl()));
                v.getContext().startActivity(intent);
            }
        });

        // xử lý sự kiện nút option

    }

    @Override
    public int getItemCount() {
        if (attachedFileList == null)
            return 0;
        return attachedFileList.size();
    }

    public class AttachedFileViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAttachedFileName, tvAttachedFileCreatedAt, tvAttachedFileSize;
        private ImageView ivAttachedFileOption;
        private RelativeLayout rlAttachedFile;

        public AttachedFileViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAttachedFileName = itemView.findViewById(R.id.tv_attached_file_name);
            tvAttachedFileCreatedAt = itemView.findViewById(R.id.tv_attached_file_created_at);
            tvAttachedFileSize = itemView.findViewById(R.id.tv_attached_file_size);

            ivAttachedFileOption = itemView.findViewById(R.id.iv_attached_file_option);

            rlAttachedFile = itemView.findViewById(R.id.rl_attached_file);
        }
    }
}
