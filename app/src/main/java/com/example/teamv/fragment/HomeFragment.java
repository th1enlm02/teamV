package com.example.teamv.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamv.activity.StatusListActivity;
import com.example.teamv.adapter.ColorPickerAdapter;
import com.example.teamv.my_interface.BoardDataCallback;
import com.example.teamv.my_interface.CardDataCallback;
import com.example.teamv.my_interface.ClickBoardItemInterface;
import com.example.teamv.object.Board;
import com.example.teamv.R;
import com.example.teamv.adapter.BoardListAdapter;
import com.example.teamv.object.Card;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements BoardDataCallback, SwipeRefreshLayout.OnRefreshListener {
    private View view;
    private RecyclerView rvBoardList;
    private ImageView ivAddBoard;
    private BoardListAdapter boardListAdapter;
    private List <Board> boards = new ArrayList<>();
    private ImageView ivBroadColor;
    private EditText etFindBoard;
    private SwipeRefreshLayout boardSwipeRefreshLayout;
    private MenuBuilder menuBuilder;
    private ProgressBar progressBar;
    private int selectedColor = R.color.custom_blue;
    private FirebaseFirestore writeBoardFirestore = FirebaseFirestore.getInstance();
    private FirebaseFirestore readBoardFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String userID = getUserID();
    private CollectionReference boardCollectionReference;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        // get view
        findViewByIds(view);

        // just for displaying
//        boards.add(new Board(formatBoardId(getCurrentTime()), "Demo", R.color.custom_blue, getCurrentTime(), userID));

        boardSwipeRefreshLayout.setOnRefreshListener(this);

        // set adapter
        setBoardListAdapter(view);

        // board option
        menuBuilder = new MenuBuilder(this.getContext());
        MenuInflater menuInflater = new MenuInflater(this.getContext());
        menuInflater.inflate(R.menu.menu_board_item_option, menuBuilder);


        // get my board data
//        readMyBoardData(this);

        // add board
        ivAddBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddBoardDialog();
            }
        });

        etFindBoard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                queryBoardFromFirestore(HomeFragment.this, s.toString());
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
    private void queryBoardFromFirestore(BoardDataCallback callback, String searchString){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firestore.collection("Board");

        Query query = collectionReference
                .whereEqualTo("user_id", userID)
                .whereEqualTo("name", searchString);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> myList = queryDocumentSnapshots.getDocuments();
                    List<Board> tempList = new ArrayList<>();

                    for (DocumentSnapshot documentSnapshot : myList) {
                        Board board = documentSnapshot.toObject(Board.class);
                        tempList.add(board);
                    }
                    callback.onDataReceived(tempList);
                }
                Log.d("QueryBoard", "Queried board successfully");
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        readMyBoardData(this);
    }

    private void setBoardListAdapter(View view){
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        rvBoardList.setLayoutManager(layoutManager);

        boardListAdapter = new BoardListAdapter(boards, new ClickBoardItemInterface() {
            @Override
            public void OnClickBoardItem(Board board) {
                onClickBoardItemGoToStatusList(board);
            }
            @Override
            public void onClickBoardItemOption(View v, Board board) {
                showBoardItemOption(v, board);
            }
        });
        rvBoardList.setAdapter(boardListAdapter);
    }
    private void writeBoardDataToFireStore (Board writeBoard) {
        String boardID = writeBoard.getBoard_id();
        DocumentReference documentReference = writeBoardFirestore.collection("Board").document(boardID);

        progressBar.setVisibility(View.VISIBLE);
        documentReference.set(writeBoard)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // thêm board vào list
                        boards.add(writeBoard);
                        boardListAdapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Tạo bảng thành công!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("WriteBoardFailed", e.getMessage());
                        progressBar.setVisibility(View.GONE);
                    }
                });
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

        selectedColor = R.color.custom_blue;

        EditText etBoardName = dialog.findViewById(R.id.et_board_name_to_add);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_add_board_dialog);
        Button btnOK = dialog.findViewById(R.id.btn_ok_add_board_dialog);
        ivBroadColor = dialog.findViewById(R.id.iv_board_color_to_add);

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
                if (!TextUtils.isEmpty(etBoardName.getText().toString())) {
                    // Lấy thông tin của board được nhập vào
                    Board writeBoard = new Board(formatBoardId(getCurrentTime()), etBoardName.getText().toString(), selectedColor, getCurrentTime(), userID);

                    writeBoardDataToFireStore(writeBoard);

                    selectedColor = R.color.custom_blue;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void readMyBoardData(BoardDataCallback callback) {
        boards.clear();
        if (boardListAdapter != null)
            boardListAdapter.notifyDataSetChanged();
        boardCollectionReference = readBoardFirestore.collection("Board");
        boardCollectionReference
                .whereEqualTo("user_id", userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            List<Board> tempBoards = new ArrayList<>();

                            for (DocumentSnapshot documentSnapshot : list) {
                                Board readBoard = documentSnapshot.toObject(Board.class);
                                tempBoards.add(readBoard);
                            }
                            callback.onDataReceived(tempBoards);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ReadBoardData", e.getMessage());
                    }
                });
    }
    @Override
    public void onDataReceived(List<Board> boards) {
        Log.d("onDataReceived", "Received new data"); // Thêm dòng log để kiểm tra
        // Đã nhận được dữ liệu từ `onSuccess` và có thể sử dụng nó ở đây
        this.boards.clear(); // Xóa dữ liệu cũ
        this.boards.addAll(boards); // Thêm dữ liệu mới
        boardListAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
    }

    //Color picker
    public void openDiaLogColor()
    {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_color_picker);


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

        GridView gridView = (GridView) dialog.findViewById(R.id.gridview_color_picker);
        ImageView ivClose = (ImageView) dialog.findViewById(R.id.iv_close_color_picker);

        int[] colors = {
                R.color.custom_blue, R.color.custom_green, R.color.custom_orange, R.color.custom_red, R.color.custom_yellow,
                R.color.custom_purple, R.color.custom_pink, R.color.custom_cyan, R.color.custom_lime, R.color.custom_silver
        };
        ColorPickerAdapter adapter = new ColorPickerAdapter(getContext(), colors);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedColor = colors[position];

                ivBroadColor.setImageResource(selectedColor);

                dialog.cancel();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }




    // Xử lý click vào board item
    private void onClickBoardItemGoToStatusList(Board board){
        Intent intent = new Intent(getContext(), StatusListActivity.class);
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

        boardSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.board_swipeRefreshLayout);
    }
    // get user id in firebase
    private String getUserID() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userID = null;
        if (firebaseUser == null) {
            Log.e("GetUserInfor", "Not found");
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

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return new String(simpleDateFormat.format(currentTime));
    }

    @Override
    public void onRefresh() {
        readMyBoardData(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boardSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }
    private void openEditBoardDialog(Board board) {
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

        EditText etBoardName = dialog.findViewById(R.id.et_board_name_to_add);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_add_board_dialog);
        Button btnOK = dialog.findViewById(R.id.btn_ok_add_board_dialog);
        ivBroadColor = dialog.findViewById(R.id.iv_board_color_to_add);

        selectedColor = board.getResource_id();
        etBoardName.setText(board.getName());
        etBoardName.setSelection(etBoardName.getText().length());
        ivBroadColor.setImageResource(selectedColor);

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
                if (!TextUtils.isEmpty(etBoardName.getText().toString())) {
                    board.setResource_id(selectedColor);
                    board.setName(etBoardName.getText().toString());

                    updateBoardItem(board);

                    selectedColor = R.color.custom_blue;
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void updateBoardItem(Board board) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firestore.collection("Board");

        collectionReference.document(board.getBoard_id())
                .update("name", board.getName(), "resource_id", board.getResource_id())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("SuccessUpdateBoard", "Updated board successfully");
                        Toast.makeText(getContext(), "Đã cập nhật bảng!", Toast.LENGTH_SHORT).show();
                        onRefresh();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("UpdateBoardFailed", e.getMessage());
                    }
                });
    }
    private void openDeleteBoardDialog(Board board) {
        final Dialog dialog = new Dialog(this.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_delete_board);

        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true);

        TextView tvCancelDeleteBoard = (TextView) dialog.findViewById(R.id.tv_cancel_delete_board_dialog);
        TextView tvConfirmDeleteBoard = (TextView) dialog.findViewById(R.id.tv_confirm_delete_board_dialog);

        tvCancelDeleteBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvConfirmDeleteBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBoardItem(board);

                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void deleteBoardItem(Board board) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firestore.collection("Board");

        collectionReference.document(board.getBoard_id())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("SuccessfulDelete", "Deleted board from Firestore successfully");
                        Toast.makeText(getContext(), "Đã xoá bảng!", Toast.LENGTH_SHORT).show();
                        onRefresh();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FailedDelete", e.getMessage());
                    }
                });
    }
    @SuppressLint("RestrictedApi")
    public void showBoardItemOption(View v, Board board) {
        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(this.getContext(), menuBuilder, v);
        menuPopupHelper.setForceShowIcon(true);
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_board_item_option_edit) {
                    openEditBoardDialog(board);
                } else if (item.getItemId() == R.id.menu_board_item_option_delete) {
                    openDeleteBoardDialog(board);
                }
                return true;
            }
            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) {

            }
        });
        menuPopupHelper.show();
    }
}