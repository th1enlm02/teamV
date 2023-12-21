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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.teamv.activity.StatusList;
import com.example.teamv.adapter.ColorPickerAdapter;
import com.example.teamv.my_interface.ClickBoardItemInterface;
import com.example.teamv.object.Board;
import com.example.teamv.R;
import com.example.teamv.adapter.BoardListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    private List <Board> boards = new ArrayList<>();;
    private ImageView ivBroadColor;
    private EditText etFindBoard;
    private ProgressBar progressBar;
    private int selectedColor = R.color.yellow;
    private FirebaseFirestore writeBoardfirestore = FirebaseFirestore.getInstance();
    private FirebaseFirestore readBoardFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String userID = getUserID();
    private CollectionReference boardCollectionReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // get view
        findViewByIds(view);

        // set default board
        boards.add(new Board("Demo", R.drawable.ic_main_logo, getCurrentTime(), userID));

        // get my board data
        readMyBoardData();

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
    private void setDefaultBoard () {
        Board defaultBoard = new Board("Công việc cần làm", R.drawable.ic_main_logo, getCurrentTime(), userID);
        writeBoardDataToFireStore(defaultBoard);
    }
    private void setBoardListAdapter(View view){
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        rvBoardList.setLayoutManager(layoutManager);

        boardListAdapter = new BoardListAdapter(boards, new ClickBoardItemInterface() {
            @Override
            public void OnClickBoardItem(Board board) {
                onClickBoardItemGoToStatusList(board);
            }
        });
        rvBoardList.setAdapter(boardListAdapter);
    }
    // Hàm mở dialog thêm bảng
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
        ivBroadColor = dialog.findViewById(R.id.iv_board_color);

        // Hàm thêm color picker
        ivBroadColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDiaLogColor();
            }
        });

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
                Board writeBoard = new Board(etBoardName.getText().toString(), selectedColor, getCurrentTime(), userID);

                writeBoardDataToFireStore(writeBoard);
                Toast.makeText(getContext(), "Tạo bảng thành công!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void writeBoardDataToFireStore (Board writeBoard) {
        String boardID = formatBoardId(getCurrentTime());
        DocumentReference documentReference = writeBoardfirestore.collection("Board").document(boardID);

        progressBar.setVisibility(View.VISIBLE);
        documentReference.set(writeBoard)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // thêm board vào list
                        boards.add(writeBoard);
                        boardListAdapter.notifyDataSetChanged();

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
    private void readMyBoardData() {
        boardCollectionReference = readBoardFirestore.collection("Board");
        boardCollectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot documentSnapshot : list) {
                                Board readBoard = documentSnapshot.toObject(Board.class);
                                if (documentSnapshot.getId().equals(formatBoardId(readBoard.getCreated_at()))) {
                                    boards.add(readBoard);
                                }
                            }
                            boardListAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void readAllBoardDataFromFirestore() {
        boardCollectionReference = readBoardFirestore.collection("Board");
        boardCollectionReference.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot documentSnapshot : list) {
                                Board readBoard = documentSnapshot.toObject(Board.class);
                                boards.add(readBoard);
                            }
                            boardListAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void openDiaLogColor()
    {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.layout_dialog_color_picker);
        GridView gridView = (GridView) dialog.findViewById(R.id.gridview_color_picker);
        int[] colors = {
                R.color.black, R.color.blue, R.color.red, R.color.green, R.color.yellow,
                R.color.purple, R.color.orange, R.color.pink, R.color.brown, R.color.cyan,
                R.color.teal, R.color.light_blue, R.color.gray, R.color.gold, R.color.silver,
                R.color.lavender, R.color.turquoise, R.color.mint, R.color.salmon, R.color.coral,
                R.color.violet, R.color.indigo, R.color.magenta, R.color.lime
        };
        ColorPickerAdapter adapter = new ColorPickerAdapter(getContext(),colors);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedColor = colors[position];

                ivBroadColor.setImageResource(selectedColor);

                dialog.cancel();
            }
        });
        dialog.show();
    }
    // Xử lý click vào board item
    private void onClickBoardItemGoToStatusList(Board board){
        Intent intent = new Intent(getContext(), StatusList.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_board", board);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void findViewByIds(View view){
        rvBoardList = (RecyclerView) view.findViewById(R.id.rv_board_list);
        ivAddBoard = (ImageView) view.findViewById(R.id.iv_add_board);
        etFindBoard = (EditText) view.findViewById(R.id.et_find_board);
        progressBar = (ProgressBar) view.findViewById(R.id.process_bar_add_board);
    }
    // get user id in firebase
    private String getUserID() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userID = null;
        if (firebaseUser == null) {
            Toast.makeText(getActivity(), "Đã xảy ra lỗi. Thông tin người dùng hiện không có sẵn",
                    Toast.LENGTH_LONG).show();
        } else {
            userID = firebaseUser.getUid();
        }
        return userID;
    }
    private String formatBoardId(String date) {
        return userID + "?" + date.replace(" ", "_");
    }
    private String getCurrentTime() {
        Date currentTime = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a", Locale.getDefault());
        return new String(simpleDateFormat.format(currentTime));
    }
}