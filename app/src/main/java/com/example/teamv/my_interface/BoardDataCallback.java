package com.example.teamv.my_interface;

import com.example.teamv.object.Board;

import java.util.List;

public interface BoardDataCallback {
    void onDataReceived(List<Board> boards);
}
