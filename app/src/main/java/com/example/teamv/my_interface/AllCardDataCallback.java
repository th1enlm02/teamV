package com.example.teamv.my_interface;

import com.example.teamv.object.Card;

import java.util.List;

public interface AllCardDataCallback {
    void onDataReceived(List<Card> cards);

}
