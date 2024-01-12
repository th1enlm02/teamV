package com.example.teamv.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamv.R;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public final TextView tv_DayOfMonth;
    private final CalendarAdapter.OnItemListener onItemListener;


    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener)
    {
        super(itemView);
        tv_DayOfMonth = itemView.findViewById(R.id.tv_CellDay);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        onItemListener.onItemClick(view,getAdapterPosition(), (String) tv_DayOfMonth.getText());

    }
}
