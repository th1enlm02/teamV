package com.example.teamv.my_interface;

import com.example.teamv.object.Card;

import java.util.List;

public interface CardDataCallback {
    void onDataReceived(List<Card> cards, String identifier);
}
