package com.example.teamv.adapter;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamv.R;
import com.example.teamv.object.ToDoListTask;

import java.util.List;

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ToDoListViewHolder> {
    private List<ToDoListTask> toDoListTasks;

    public ToDoListAdapter(List<ToDoListTask> toDoListTasks) {
        this.toDoListTasks = toDoListTasks;
    }

    @NonNull
    @Override
    public ToDoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_to_do_list, parent, false);
        return new ToDoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoListViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ToDoListTask toDoListTask = toDoListTasks.get(position);
        if (toDoListTask == null)
            return;

        holder.tvToDoListTaskName.setText(toDoListTask.getName());

        if (toDoListTask.isIs_checked()) {
            holder.ivToDoListCheckbox.setImageResource(R.drawable.ic_checked);
            holder.tvToDoListTaskName.setPaintFlags(holder.tvToDoListTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.ivToDoListCheckbox.setImageResource(R.drawable.ic_checkbox);
            holder.tvToDoListTaskName.setPaintFlags(holder.tvToDoListTaskName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        holder.ivToDoListCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toDoListTask.isIs_checked())
                    toDoListTasks.get(position).setIs_checked(false);
                else
                    toDoListTasks.get(position).setIs_checked(true);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (toDoListTasks == null)
            return 0;
        return toDoListTasks.size();
    }

    public class ToDoListViewHolder extends RecyclerView.ViewHolder {
        ImageView ivToDoListCheckbox;
        TextView tvToDoListTaskName;

        public ToDoListViewHolder(@NonNull View itemView) {
            super(itemView);

            ivToDoListCheckbox = itemView.findViewById(R.id.iv_to_do_list_checkbox);
            tvToDoListTaskName = itemView.findViewById(R.id.tv_to_do_list_task_name);
        }
    }
}
