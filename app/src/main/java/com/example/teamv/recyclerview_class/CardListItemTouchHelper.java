package com.example.teamv.recyclerview_class;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamv.R;
import com.example.teamv.adapter.CardListAdapter;
import com.example.teamv.my_interface.CardItemTouchHelperInterface;

public class CardListItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private CardItemTouchHelperInterface listener;
    private String identifier;

    public CardListItemTouchHelper(int dragDirs, int swipeDirs, CardItemTouchHelperInterface listener, String identifier) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
        this.identifier = identifier;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener != null) {
            listener.onSwiped(viewHolder, identifier);
        }
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            View cardItem = ((CardListAdapter.CardListViewHolder) viewHolder).itemView.findViewById(R.id.layout_card_item);
            getDefaultUIUtil().onSelected(cardItem);
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder != null) {
            View cardItem = ((CardListAdapter.CardListViewHolder) viewHolder).itemView.findViewById(R.id.layout_card_item);
            getDefaultUIUtil().onDrawOver(c, recyclerView, cardItem, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View cardItem = ((CardListAdapter.CardListViewHolder) viewHolder).itemView.findViewById(R.id.layout_card_item);
        getDefaultUIUtil().onDraw(c, recyclerView, cardItem, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        View cardItem = ((CardListAdapter.CardListViewHolder) viewHolder).itemView.findViewById(R.id.layout_card_item);
        getDefaultUIUtil().clearView(cardItem);
    }
}
