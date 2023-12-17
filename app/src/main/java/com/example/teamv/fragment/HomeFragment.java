package com.example.teamv.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.teamv.activity.StatusList;
import com.example.teamv.my_interface.ClickBoardItemInterface;
import com.example.teamv.object.Board;
import com.example.teamv.R;
import com.example.teamv.adapter.BoardListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private View view;
    private RecyclerView rvBoardList;
    private ImageView ivAddBoard;
    private BoardListAdapter boardListAdapter;
    private List <Board> boards;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // get view
        findViewByIds(view);

        // set adapter
        setBoardListAdapter(view);

        // add board
        ivAddBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddBoardDialog();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
    private void findViewByIds(View view){
        rvBoardList = (RecyclerView) view.findViewById(R.id.rv_board_list);
        ivAddBoard = (ImageView) view.findViewById(R.id.iv_add_board);
    }
    private void setBoardListAdapter(View view){
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        rvBoardList.setLayoutManager(layoutManager);

        boards = new ArrayList<>();
        setDefaultBoardList();

        boardListAdapter = new BoardListAdapter(boards, new ClickBoardItemInterface() {
            @Override
            public void OnClickBoardItem(Board board) {
                onClickBoardItemGoToStatusList(board);
            }
        });
        rvBoardList.setAdapter(boardListAdapter);
    }
    private void openAddBoardDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add_board);

        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;
        window.setAttributes(windowAttributes);
        window.getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCancelable(true);

        EditText etBoardName = dialog.findViewById(R.id.et_board_name);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnOK = dialog.findViewById(R.id.btn_ok);
        ProgressBar progressBar = dialog.findViewById(R.id.process_bar_add_board);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // Xử lý thêm thông tin board vào database
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy thông tin của board được nhập vào
                Board writeBoard = new Board(null, etBoardName.getText().toString(), R.drawable.ic_main_logo, getCurrentTime());

                FirebaseFirestore writeBoardfirestore = FirebaseFirestore.getInstance();
                CollectionReference collectionReference = writeBoardfirestore.collection("Board");

                progressBar.setVisibility(View.VISIBLE);
                collectionReference.add(writeBoard)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // Xử lý lấy thông tin board từ database về hiển thị lên recyclerview
                                FirebaseFirestore readBoardFirestore = FirebaseFirestore.getInstance();
                                documentReference = readBoardFirestore.collection("Board").document(documentReference.getId());
                                documentReference.get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Board readBoard = documentSnapshot.toObject(Board.class);
                                        readBoard.setId(documentSnapshot.getId());
                                        // Thêm board được read từ database vào rcv
                                        boards.add(readBoard);
                                        boardListAdapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Tạo bảng thành công!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
        dialog.show();
    }
    private String getCurrentTime() {
        Date currentTime = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MM:yy HH:mm a", Locale.getDefault());
        return new String(simpleDateFormat.format(currentTime));
    }
    private void setDefaultBoardList() {
        boards.add(new Board("", "Phát triển ưng dụng trên thiết bị di động", R.drawable.ic_main_logo, "1/1/2023 00:00 PM"));
        boards.add(new Board("", "An toàn mạng máy tính", R.color.black, "1/1/2023 00:00 PM"));
        boards.add(new Board("", "Thiết kế Mạng", R.drawable.ic_main_logo, "1/1/2023 00:00 PM"));
        boards.add(new Board("", "Hệ thống nhúng Mạng không dây", R.drawable.ic_main_logo, "1/1/2023 00:00 PM"));
    }

    // Xử lý click vào board item
    private void onClickBoardItemGoToStatusList(Board board){
        Intent intent = new Intent(getContext(), StatusList.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_board", board);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}