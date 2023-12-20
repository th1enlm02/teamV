package com.example.teamv.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamv.my_interface.ClickBoardItemInterface;
import com.example.teamv.object.Board;
import com.example.teamv.R;

import org.checkerframework.checker.units.qual.C;

import java.util.List;

public class BoardListAdapter extends RecyclerView.Adapter<BoardListAdapter.BoardListViewHolder> {
    private List<Board> boards;
    private ClickBoardItemInterface clickBoardItemInterface;

    public BoardListAdapter(List<Board> boards, ClickBoardItemInterface clickBoardItemInterface) {
        this.boards = boards;
        this.clickBoardItemInterface = clickBoardItemInterface;
    }
    @NonNull
    @Override
    public BoardListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board, parent, false);
        return new BoardListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardListViewHolder holder, int position) {
        Board board = boards.get(position);
        if (board == null)
            return;
        holder.ivAvtBoard.setImageResource(board.getResource_id());
        holder.tvBoardName.setText(board.getName());
        holder.tvBoardDate.setText(board.getCreated_at());
        holder.ivEditBoard.setImageResource(R.drawable.ic_edit_board);

        holder.layoutBoardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBoardItemInterface.OnClickBoardItem(board);
            }
        });
    }


    @Override
    public int getItemCount() {
        if (boards != null)
            return boards.size();
        return 0;
    }

    public class BoardListViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAvtBoard;
        private TextView tvBoardName, tvBoardDate;
        private ImageView ivEditBoard;
        private CardView layoutBoardItem;
        public BoardListViewHolder(@NonNull View itemView) {
            super(itemView);

            // Ánh xạ view
            ivAvtBoard = itemView.findViewById(R.id.iv_avt_board);
            tvBoardName = itemView.findViewById(R.id.tv_board_name);
            ivEditBoard = itemView.findViewById(R.id.iv_edit_board);
            tvBoardDate = itemView.findViewById(R.id.tv_board_date);
            layoutBoardItem = itemView.findViewById(R.id.layout_board_item);
        }
    }
}
