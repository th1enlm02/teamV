package com.example.teamv.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamv.R;
import com.example.teamv.adapter.CardListAdapter;
import com.example.teamv.my_interface.ClickCardItemInterface;
import com.example.teamv.object.Board;
import com.example.teamv.object.Card;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class StatusListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private String boardID;
    // status lists
    private List<Card> listUnscheduled = new ArrayList<>();
    private List<Card> listInProcess = new ArrayList<>();
    private List<Card> listCompleted = new ArrayList<>();
    private List<Card> listOverdue = new ArrayList<>();
    // views
    private TextView tvTitle;
    private TextView tvUnscheduledNumber, tvInProcessNumber, tvCompletedNumber, tvOverdueNumber;
    private RecyclerView rcvUnscheduled, rcvInProcess, rcvCompleted, rcvOverdue;
    private ImageView ivBackToHome, ivAddCard;
    private CardListAdapter unscheduledListAdapter, inProcessListAdapter, completedListAdapter, overdueListAdapter;
    private LinearLayout llStatusListTopBar;
    private SwipeRefreshLayout statusSwipeRefreshLayout;
    // Firestore database
    private FirebaseFirestore writeCardFirestore = FirebaseFirestore.getInstance();
    private FirebaseFirestore readCardFirestore = FirebaseFirestore.getInstance();
    private CollectionReference cardCollectionReference = readCardFirestore.collection("Card");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_list);

        // findviewbyids
        findViewByIds();

        statusSwipeRefreshLayout.setOnRefreshListener(this);

        // Lấy thông tin board từ main activity
        Board board = getBoardInfoFromHomeFragment();
        boardID = board.getBoard_id();

        // set layout
        tvTitle.setText(board.getName());
        llStatusListTopBar.setBackgroundTintList(getResources().getColorStateList(board.getResource_id()));

        // test
//        cards.add(new Card("1", "board_id_1", "Card 1", R.color.custom_blue, "Description 1", "2023-12-20", "2023-12-30", new ArrayList<>(), new ArrayList<>(), "2023-12-15", false, true, "In process"));
//        cards.add(new Card("2", "board_id_1", "Card 2", R.color.custom_blue, "Description 2", "2023-12-25", "2023-12-31", new ArrayList<>(), new ArrayList<>(), "2023-12-16", false, false, "Unscheduled"));
//        cards.add(new Card("3", "board_id_2", "Card 3", R.color.custom_blue, "Description 3", "2023-12-28", "2023-12-31", new ArrayList<>(), new ArrayList<>(), "2023-12-17", true, false, "Overdue"));

        // read my card
//        readMyCardData();

        // set card list adapters
        setCardListAdapters();

        // back event
        ivBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // add card event
        ivAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddCardDialog();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        readMyCardData();
    }

    private void readMyCardData() {
        resetStatus();
        readUnscheduledCard();
        readInProcessCard();
        readCompletedCard();
        readOverdueCard();
    }
    private void readUnscheduledCard() {
        cardCollectionReference
                .whereEqualTo("board_id", boardID)
                .whereEqualTo("status", "Unscheduled")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> unscheduledList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : unscheduledList) {
                                Card unscheduledCard = documentSnapshot.toObject(Card.class);
                                listUnscheduled.add(unscheduledCard);
                            }
                            tvUnscheduledNumber.setText("(" + listUnscheduled.size() + ")");
                            if (listUnscheduled.size() != 0) {
                                unscheduledListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Unscheduled error", e.getMessage());
                    }
                });
    }
    private void readInProcessCard() {
        cardCollectionReference
                .whereEqualTo("board_id", boardID)
                .whereEqualTo("status", "In process")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> inProcessList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : inProcessList) {
                                Card inProcessCard = documentSnapshot.toObject(Card.class);
                                listInProcess.add(inProcessCard);
                            }
                            tvInProcessNumber.setText("(" + listInProcess.size() + ")");
                            if (listInProcess.size() != 0) {
                                inProcessListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("In process error", e.getMessage());
                    }
                });
    }
    private void readCompletedCard() {
        cardCollectionReference
                .whereEqualTo("board_id", boardID)
                .whereEqualTo("status", "Completed")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> completedList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : completedList) {
                                Card completedCard = documentSnapshot.toObject(Card.class);
                                listCompleted.add(completedCard);
                            }
                            tvCompletedNumber.setText("(" + listCompleted.size() + ")");
                            if (listCompleted.size() != 0)
                                completedListAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Completed error", e.getMessage());
                    }
                });
    }
    private void readOverdueCard() {
        cardCollectionReference
                .whereEqualTo("board_id", boardID)
                .whereEqualTo("status", "Overdue")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> overdueList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot documentSnapshot : overdueList) {
                                Card overdueCard = documentSnapshot.toObject(Card.class);
                                listOverdue.add(overdueCard);
                            }
                            tvOverdueNumber.setText("(" + listOverdue.size() + ")");
                            if (listOverdue.size() != 0)
                                overdueListAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Overdue error", e.getMessage());
                    }
                });
    }
    private void openAddCardDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_add_card);

        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        window.getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCancelable(true);

        EditText etCardName = (EditText) dialog.findViewById(R.id.et_card_name_to_add);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel_add_card_dialog);
        Button btnOK = (Button) dialog.findViewById(R.id.btn_ok_add_card_dialog);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card writeCard = new Card(formatCardId(getCurrentTime()), boardID, etCardName.getText().toString(),
                        0, "", "", new ArrayList<>(), new ArrayList<>(), getCurrentTime(), false, false,
                        getString(R.string.Unscheduled));

                writeCardDataToFireStoreWithOnlyCardName(writeCard);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void writeCardDataToFireStoreWithOnlyCardName(Card writeCard) {
        String cardID = writeCard.getCard_id();
        DocumentReference documentReference = writeCardFirestore.collection("Card").document(cardID);

        documentReference.set(writeCard)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        listUnscheduled.add(writeCard);

                        String sizeStr = tvUnscheduledNumber.getText().toString().substring(1, tvUnscheduledNumber.getText().toString().length() - 1);
                        try {
                            int sizeInt = Integer.parseInt(sizeStr);
                            sizeInt += 1;
                            tvUnscheduledNumber.setText("(" + sizeInt + ")");
                        } catch (NumberFormatException e) {
                            Log.e("NumberFormatException", e.getMessage());
                        }

                        unscheduledListAdapter.notifyDataSetChanged();

                        Toast.makeText(StatusListActivity.this, "Tạo thẻ thành công!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StatusListActivity.this, "Đã xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private String formatCardId(String date) {
        return boardID + "?" + date.replace(" ", "_");
    }
    private String getCurrentTime() {
        Date currentTime = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return new String(simpleDateFormat.format(currentTime));
    }
    private void setCardListAdapters() {
        rcvUnscheduled.setLayoutManager(new LinearLayoutManager(StatusListActivity.this));
        rcvInProcess.setLayoutManager(new LinearLayoutManager(StatusListActivity.this));
        rcvCompleted.setLayoutManager(new LinearLayoutManager(StatusListActivity.this));
        rcvOverdue.setLayoutManager(new LinearLayoutManager(StatusListActivity.this));

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

        // init adapters
//        if (unscheduledListAdapter == null) {
//            unscheduledListAdapter = new CardListAdapter(listUnscheduled, new ClickCardItemInterface() {
//                @Override
//                public void OnClickCardItem(Card card) {
//                    onClickCardItemGoToCardActivity(card);
//                }
//            });
//            rcvUnscheduled.setAdapter(unscheduledListAdapter);
//        }
//
//        if (inProcessListAdapter == null) {
//            inProcessListAdapter = new CardListAdapter(listInProcess, new ClickCardItemInterface() {
//                @Override
//                public void OnClickCardItem(Card card) {
//                    onClickCardItemGoToCardActivity(card);
//                }
//            });
//            rcvInProcess.setAdapter(inProcessListAdapter);
//        }
//
//        if (completedListAdapter == null) {
//            completedListAdapter = new CardListAdapter(listCompleted, new ClickCardItemInterface() {
//                @Override
//                public void OnClickCardItem(Card card) {
//                    onClickCardItemGoToCardActivity(card);
//                }
//            });
//            rcvCompleted.setAdapter(completedListAdapter);
//        }
//
//        if (overdueListAdapter == null) {
//            overdueListAdapter = new CardListAdapter(listOverdue, new ClickCardItemInterface() {
//                @Override
//                public void OnClickCardItem(Card card) {
//                    onClickCardItemGoToCardActivity(card);
//                }
//            });
//            rcvOverdue.setAdapter(overdueListAdapter);
//        }
    }
    private void onClickCardItemGoToCardActivity(Card card) {
        Intent intent = new Intent(this, CardActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_card", card);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private Board getBoardInfoFromHomeFragment() {
        Bundle bundle = getIntent().getExtras();
        Board board = (Board) bundle.get("object_board");
        return board;
    }
    private void findViewByIds() {
        tvTitle = (TextView) findViewById(R.id.tv_title_board_name);
        llStatusListTopBar = (LinearLayout) findViewById(R.id.ll_status_list_top_bar);

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

        statusSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.status_swipeRefreshLayout);
    }
    private void resetStatus() {
        listUnscheduled.clear();
        listInProcess.clear();
        listCompleted.clear();
        listOverdue.clear();
        setCardListAdapters();
        if (unscheduledListAdapter != null)
            unscheduledListAdapter.notifyDataSetChanged();
        if (inProcessListAdapter != null)
            inProcessListAdapter.notifyDataSetChanged();
        if (completedListAdapter != null)
            completedListAdapter.notifyDataSetChanged();
        if (overdueListAdapter != null)
            overdueListAdapter.notifyDataSetChanged();
        tvUnscheduledNumber.setText("(" + 0 + ")");
        tvInProcessNumber.setText("(" + 0 + ")");
        tvCompletedNumber.setText("(" + 0 + ")");
        tvOverdueNumber.setText("(" + 0 + ")");
    }

    @Override
    public void onRefresh() {
        readMyCardData();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }
}