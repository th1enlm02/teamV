package com.example.teamv.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamv.R;
import com.example.teamv.my_interface.ClickCardItemInterface;
import com.example.teamv.object.Card;
import com.example.teamv.object.Task;

import java.util.List;

public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.CardListViewHolder> {
    private List<Card> cards;

    private ClickCardItemInterface clickCardItemInterface;

    public CardListAdapter(List<Card> cards, ClickCardItemInterface clickCardItemInterface) {
        this.cards = cards;
        this.clickCardItemInterface = clickCardItemInterface;
    }

    @NonNull
    @Override
    public CardListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardListViewHolder holder, int position) {
        Card card = cards.get(position);
        if (card == null)
            return;

        holder.ivCardAvt.setImageResource(card.getResource_id());
        holder.tvCardName.setText(card.getName());
        switch (card.getStatus()) {
            case "Unscheduled":
                holder.ivStatus.setImageResource(R.drawable.ic_unscheduled);
                break;
            case "In process":
                holder.ivStatus.setImageResource(R.drawable.ic_in_process);

                holder.tvDeadline.setVisibility(View.VISIBLE);
                holder.tvDeadline.setText("Deadline: " + card.getDeadline_at());
                holder.tvDeadline.setBackgroundResource(R.drawable.bg_corner_in_process);
                break;
            case "Completed":
                holder.ivStatus.setImageResource(R.drawable.ic_completed);

                holder.tvDeadline.setVisibility(View.VISIBLE);
                holder.tvDeadline.setText("Deadline: " + card.getDeadline_at());
                holder.tvDeadline.setBackgroundResource(R.drawable.bg_corner_completed);
                break;
            case "Overdue":
                holder.ivStatus.setImageResource(R.drawable.ic_overdue);

                holder.tvDeadline.setVisibility(View.VISIBLE);
                holder.tvDeadline.setText("Deadline: " + card.getDeadline_at());
                holder.tvDeadline.setBackgroundResource(R.drawable.bg_corner_overdue);
                break;
        }

        List<Task> tasks = card.getTo_do_list();
        int numberOfTask = tasks.size(), numberOfCheckedTask = getNumberOfCheckedTask(tasks);
        if (numberOfTask != 0) {
            holder.llToDoList.setVisibility(View.VISIBLE);
            holder.tvToDoList.setText(String.valueOf(numberOfCheckedTask) + "/" + String.valueOf(numberOfTask));
        }

        if (card.isIs_pinned() == true) {
            holder.ivPin.setVisibility(View.VISIBLE);
        }

        holder.layoutCardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCardItemInterface.OnClickCardItem(card);
            }
        });
    }
    public int getNumberOfCheckedTask (List<Task> tasks) {
        int numberOfCheckedTask = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isIs_checked() == true)
                numberOfCheckedTask++;
        }
        return numberOfCheckedTask;
    }

    @Override
    public int getItemCount() {
        if (cards != null)
            return cards.size();
        return 0;
    }


    public class CardListViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCardAvt, ivStatus, ivPin;
        private TextView tvCardName, tvDeadline, tvToDoList;
        private LinearLayout llToDoList;
        private CardView layoutCardItem;
        public CardListViewHolder(@NonNull View itemView) {
            super(itemView);

            // Ánh xạ view
            ivCardAvt = itemView.findViewById(R.id.iv_avt_card);
            ivStatus = itemView.findViewById(R.id.iv_status);
            ivPin = itemView.findViewById(R.id.iv_pin);

            tvCardName = itemView.findViewById(R.id.tv_card_name);
            tvDeadline = itemView.findViewById(R.id.tv_deadline);
            tvToDoList = itemView.findViewById(R.id.tv_to_do_list);

            llToDoList = itemView.findViewById(R.id.ll_to_do_list);

            layoutCardItem = itemView.findViewById(R.id.layout_card_item);
        }
    }
}
