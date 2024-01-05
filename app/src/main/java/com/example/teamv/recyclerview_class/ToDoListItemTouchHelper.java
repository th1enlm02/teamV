package com.example.teamv.recyclerview_class;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teamv.R;
import com.example.teamv.adapter.ToDoListAdapter;
import com.example.teamv.my_interface.ToDoListItemTouchHelperInterface;

public class ToDoListItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private ToDoListItemTouchHelperInterface listener;

    public ToDoListItemTouchHelper(int dragDirs, int swipeDirs, ToDoListItemTouchHelperInterface listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener != null) {
            listener.onSwiped(viewHolder);
        }
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            View toDoListItem = ((ToDoListAdapter.ToDoListViewHolder) viewHolder).itemView.findViewById(R.id.rl_to_do_list_item);
            getDefaultUIUtil().onSelected(toDoListItem);
        }
    }
    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View toDoListItem = ((ToDoListAdapter.ToDoListViewHolder) viewHolder).itemView.findViewById(R.id.rl_to_do_list_item);
        getDefaultUIUtil().onDrawOver(c, recyclerView, toDoListItem, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View toDoListItem = ((ToDoListAdapter.ToDoListViewHolder) viewHolder).itemView.findViewById(R.id.rl_to_do_list_item);
        getDefaultUIUtil().onDraw(c, recyclerView, toDoListItem, dX, dY, actionState, isCurrentlyActive);
    }
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        View toDoListItem = ((ToDoListAdapter.ToDoListViewHolder) viewHolder).itemView.findViewById(R.id.rl_to_do_list_item);
        getDefaultUIUtil().clearView(toDoListItem);
    }
}
