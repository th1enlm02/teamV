package com.example.teamv.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamv.R;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<String> daysOfMonth;
    private final ArrayList<String> deadlineInMonth;


    private final OnItemListener onItemListener;



    public CalendarAdapter(ArrayList<String> daysOfMonth, ArrayList<String> daysOfDeadline, OnItemListener onItemListener)
    {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        // Kiểm tra daysOfDeadline không phải là null
        if (daysOfDeadline == null) {
            // Xử lý khi daysOfDeadline là null, ví dụ: tạo một ArrayList mới
            this.deadlineInMonth = new ArrayList<>();
        } else {
            this.deadlineInMonth = daysOfDeadline;
        }
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_calendar_cell, parent, false);
//        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//        //layoutParams.height = (int) (parent.getHeight() * 0.166666666);
//        layoutParams.height = (int) (parent.getHeight() *0.1);

        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        String dayText = daysOfMonth.get(position);
        //viết code so sánh dayText với mảng deadline in Month
        if (isDayInDeadline(dayText)) {
            // Nếu có, thay đổi màu sắc của item
            holder.tv_DayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        } else {
            // Nếu không, đặt lại màu sắc mặc định
            //holder.itemView.setBackgroundResource(android.R.color.transparent);
            holder.tv_DayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
        }
        // holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.gray));
        //holder.tv_DayOfMonth.setBackgroundColor(holder.itemView.getResources().getColor(R.color.gray));

        holder.tv_DayOfMonth.setText(dayText);
    }

    private boolean isDayInDeadline(String dayText) {
        return deadlineInMonth.contains(dayText);
    }


    @Override
    public int getItemCount()
    {
        return daysOfMonth.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(View view,int position, String dayText);
    }
}
