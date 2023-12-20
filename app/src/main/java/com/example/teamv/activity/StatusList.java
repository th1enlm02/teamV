package com.example.teamv.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.teamv.R;
import com.example.teamv.object.Board;

public class StatusList extends AppCompatActivity {

    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_list);

        // Lấy thông tin board từ main activity
        Board board = getBoardInfoFromMainActivity();

        // findviewbyids
        findViewByIds();

        // set title
        tvTitle.setText(board.getName());

    }
    private Board getBoardInfoFromMainActivity() {
        Bundle bundle = getIntent().getExtras();
        Board board = (Board) bundle.get("object_board");
        return board;
    }
    private void findViewByIds() {
        tvTitle = (TextView) findViewById(R.id.tv_title_board_name);
    }
}