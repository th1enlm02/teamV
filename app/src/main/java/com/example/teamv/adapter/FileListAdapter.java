package com.example.teamv.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamv.my_interface.ItemClickListerner;

public class FileListAdapter extends RecyclerView.ViewHolder implements View.OnClickListener{
    public ItemClickListerner listerner;
    private final Context context;
    public TextView pdftitle;
    public FileListAdapter(@NonNull View itemView) {
        super(itemView);
        context=itemView.getContext();
//        pdftitle=itemView.findViewById(itemView.getId());

    }

    @Override
    public void onClick(View v) {
        listerner.onClick(v,getAdapterPosition(),false);
    }
}
