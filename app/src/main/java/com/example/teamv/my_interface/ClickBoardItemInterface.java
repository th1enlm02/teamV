package com.example.teamv.my_interface;

import android.view.View;

import com.example.teamv.object.Board;

public interface ClickBoardItemInterface {
    void OnClickBoardItem(Board board);
    void onClickBoardItemOption(View v, Board board);
}
