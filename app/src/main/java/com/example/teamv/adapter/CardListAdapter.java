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
import com.example.teamv.my_interface.CardDataCallback;
import com.example.teamv.my_interface.ClickCardItemInterface;
import com.example.teamv.object.Card;
import com.example.teamv.object.ToDoListTask;

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

        if (card.getResource_id() > 0) {
            holder.ivCardAvt.setImageResource(card.getResource_id());
            holder.ivCardAvt.setVisibility(View.VISIBLE);
        }
        holder.tvCardName.setText(card.getName());
        holder.tvCardCreatedAt.setText(card.getCreated_at());
        switch (card.getStatus()) {
            case "Unscheduled":
                holder.ivStatus.setImageResource(R.drawable.ic_unscheduled);
                break;
            case "In process":
                holder.ivStatus.setImageResource(R.drawable.ic_in_process);

                holder.llDeadline.setVisibility(View.VISIBLE);
                holder.tvDeadline.setText("Deadline: " + card.getDeadline_at());
                holder.llDeadline.setBackgroundResource(R.drawable.bg_corner_in_process);
                break;
            case "Completed":
                holder.ivStatus.setImageResource(R.drawable.ic_completed);

                if (!card.getDeadline_at().equals("")) {
                    holder.llDeadline.setVisibility(View.VISIBLE);
                    holder.tvDeadline.setText("Deadline: " + card.getDeadline_at());
                    holder.llDeadline.setBackgroundResource(R.drawable.bg_corner_completed);
                }
                break;
            case "Overdue":
                holder.ivStatus.setImageResource(R.drawable.ic_overdue);

                holder.llDeadline.setVisibility(View.VISIBLE);
                holder.tvDeadline.setText("Deadline: " + card.getDeadline_at());
                holder.llDeadline.setBackgroundResource(R.drawable.bg_corner_overdue);
                break;
        }

        List<ToDoListTask> toDoListTasks = card.getTo_do_list();
        int numberOfTask = toDoListTasks.size(), numberOfCheckedTask = getNumberOfCheckedTask(toDoListTasks);
        if (numberOfTask != 0) {
            holder.llToDoList.setVisibility(View.VISIBLE);
            holder.tvToDoList.setText(String.valueOf(numberOfCheckedTask) + "/" + String.valueOf(numberOfTask));
            if (numberOfCheckedTask != numberOfTask) {
                holder.llToDoList.setBackgroundResource(R.drawable.bg_corner_in_process);
            } else {
                holder.llToDoList.setBackgroundResource(R.drawable.bg_corner_completed);
            }
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
    public int getNumberOfCheckedTask (List<ToDoListTask> toDoListTasks) {
        int numberOfCheckedTask = 0;
        for (int i = 0; i < toDoListTasks.size(); i++) {
            if (toDoListTasks.get(i).isIs_checked() == true)
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
    public void removeItem(int index) {
        cards.remove(index);
        notifyItemRemoved(index);
    }
    public void undoItem(Card card, int index) {
        cards.add(index, card);
        notifyItemInserted(index);
    }
    public class CardListViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCardAvt, ivStatus, ivPin;
        private TextView tvCardName, tvDeadline, tvToDoList, tvCardCreatedAt;
        private LinearLayout llToDoList, llDeadline;
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
            tvCardCreatedAt = itemView.findViewById(R.id.tv_card_created_at);

            llToDoList = itemView.findViewById(R.id.ll_to_do_list);

            layoutCardItem = itemView.findViewById(R.id.layout_card_item);

            llDeadline = itemView.findViewById(R.id.ll_deadline);
        }
    }
}
