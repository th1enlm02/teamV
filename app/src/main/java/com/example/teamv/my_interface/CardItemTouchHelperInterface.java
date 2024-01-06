package com.example.teamv.my_interface;

import androidx.recyclerview.widget.RecyclerView;

public interface CardItemTouchHelperInterface {
    void onSwiped(RecyclerView.ViewHolder viewHolder, String identifier);
}
