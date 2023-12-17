package com.example.teamv.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.teamv.R;
import com.example.teamv.object.Board;

public class StatusList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_list);

        // Lấy thông tin board từ main activity
        getBoardInfoFromMainActivity();

    }
    private void getBoardInfoFromMainActivity() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return;
        Board board = (Board) bundle.get("object_board");
    }
}