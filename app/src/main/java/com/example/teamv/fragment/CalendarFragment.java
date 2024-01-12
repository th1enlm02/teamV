package com.example.teamv.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.teamv.R;
import com.example.teamv.object.Card;

import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment {

    private List<Card> myCardList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
    private void readAllMyCard() {

    }
}