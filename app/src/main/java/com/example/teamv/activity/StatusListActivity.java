package com.example.teamv.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teamv.R;
import com.example.teamv.adapter.CardListAdapter;
import com.example.teamv.my_interface.ClickCardItemInterface;
import com.example.teamv.object.Board;
import com.example.teamv.object.Card;

import java.util.ArrayList;
import java.util.List;

public class StatusListActivity extends AppCompatActivity {
    private List<Card> cards = new ArrayList<>();
    private List<Card> listUnscheduled = new ArrayList<>();
    private List<Card> listInProcess = new ArrayList<>();
    private List<Card> listCompleted = new ArrayList<>();
    private List<Card> listOverdue = new ArrayList<>();
    private TextView tvTitle;
    private TextView tvUnscheduledNumber, tvInProcessNumber, tvCompletedNumber, tvOverdueNumber;
    private RecyclerView rcvUnscheduled, rcvInProcess, rcvCompleted, rcvOverdue;
    private ImageView ivBackToHome, ivAddCard;
    private CardListAdapter unscheduledListAdapter, inProcessListAdapter, completedListAdapter, overdueListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_list);

        // findviewbyids
        findViewByIds();

        // Lấy thông tin board từ main activity
        Board board = getBoardInfoFromMainActivity();

        // set title
        tvTitle.setText(board.getName());



        // test
        cards.add(new Card("1", "board_id_1", "Card 1", R.color.custom_blue, "Description 1", "2023-12-20", "2023-12-30", new ArrayList<>(), new ArrayList<>(), "2023-12-15", false, true, "In process"));
        cards.add(new Card("2", "board_id_1", "Card 2", R.color.custom_blue, "Description 2", "2023-12-25", "2023-12-31", new ArrayList<>(), new ArrayList<>(), "2023-12-16", false, false, "Unscheduled"));
        cards.add(new Card("3", "board_id_2", "Card 3", R.color.custom_blue, "Description 3", "2023-12-28", "2023-12-31", new ArrayList<>(), new ArrayList<>(), "2023-12-17", true, false, "Overdue"));

        // set card list adapters
        setCardListAdapters();

        // display size of each status list
        displaySizeOfStatusLists();

        // back event
        ivBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void displaySizeOfStatusLists() {
        tvUnscheduledNumber.setText("(" + listUnscheduled.size() + ")");
        tvInProcessNumber.setText("(" + listInProcess.size() + ")");
        tvCompletedNumber.setText("(" + listCompleted.size() + ")");
        tvOverdueNumber.setText("(" + listOverdue.size() + ")");
    }
    private void setCardListAdapters() {
        rcvUnscheduled.setLayoutManager(new LinearLayoutManager(this));
        rcvInProcess.setLayoutManager(new LinearLayoutManager(this));
        rcvCompleted.setLayoutManager(new LinearLayoutManager(this));
        rcvOverdue.setLayoutManager(new LinearLayoutManager(this));

        // init lists
        initCardLists();

        // init adapters
        unscheduledListAdapter = new CardListAdapter(listUnscheduled, new ClickCardItemInterface() {
            @Override
            public void OnClickCardItem(Card card) {
                onClickCardItemGoToCardActivity(card);
            }
        });
        rcvUnscheduled.setAdapter(unscheduledListAdapter);

        inProcessListAdapter = new CardListAdapter(listInProcess, new ClickCardItemInterface() {
            @Override
            public void OnClickCardItem(Card card) {
                onClickCardItemGoToCardActivity(card);
            }
        });
        rcvInProcess.setAdapter(inProcessListAdapter);

        completedListAdapter = new CardListAdapter(listCompleted, new ClickCardItemInterface() {
            @Override
            public void OnClickCardItem(Card card) {
                onClickCardItemGoToCardActivity(card);
            }
        });
        rcvCompleted.setAdapter(completedListAdapter);

        overdueListAdapter = new CardListAdapter(listOverdue, new ClickCardItemInterface() {
            @Override
            public void OnClickCardItem(Card card) {
                onClickCardItemGoToCardActivity(card);
            }
        });
        rcvOverdue.setAdapter(overdueListAdapter);
    }
    private void onClickCardItemGoToCardActivity(Card card) {
        Intent intent = new Intent(this, CardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_card", card);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void initCardLists() {
        if (cards.size() == 0)
            return;
        for (Card card : cards) {
            switch (card.getStatus()) {
                case "Unscheduled":
                    listUnscheduled.add(card);
                    break;
                case "In process":
                    listInProcess.add(card);
                    break;
                case "Completed":
                    listCompleted.add(card);
                    break;
                case "Overdue":
                    listOverdue.add(card);
                    break;
            }
        }
    }
    private Board getBoardInfoFromMainActivity() {
        Bundle bundle = getIntent().getExtras();
        Board board = (Board) bundle.get("object_board");
        return board;
    }

    private void findViewByIds() {
        tvTitle = (TextView) findViewById(R.id.tv_title_board_name);

        tvUnscheduledNumber = (TextView) findViewById(R.id.tv_unscheduled_number);
        tvInProcessNumber = (TextView) findViewById(R.id.tv_in_process_number);
        tvCompletedNumber = (TextView) findViewById(R.id.tv_completed_number);
        tvOverdueNumber = (TextView) findViewById(R.id.tv_overdue_number);

        rcvUnscheduled = (RecyclerView) findViewById(R.id.rcv_unscheduled);
        rcvInProcess = (RecyclerView) findViewById(R.id.rcv_in_process);
        rcvCompleted = (RecyclerView) findViewById(R.id.rcv_completed);
        rcvOverdue = (RecyclerView) findViewById(R.id.rcv_overdue);

        ivBackToHome = (ImageView) findViewById(R.id.iv_back_to_home);
        ivAddCard = (ImageView) findViewById(R.id.iv_add_card);
    }
}