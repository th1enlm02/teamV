package com.example.teamv.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.teamv.fragment.HomeFragment;
import com.example.teamv.my_interface.CardDataCallback;
import com.example.teamv.my_interface.CardItemTouchHelperInterface;
import com.example.teamv.my_interface.ClickCardItemInterface;
import com.example.teamv.object.AttachedFile;
import com.example.teamv.object.Board;
import com.example.teamv.object.Card;
import com.example.teamv.object.ToDoListTask;
import com.example.teamv.recyclerview_class.CardListItemTouchHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.C;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatusListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, CardDataCallback, CardItemTouchHelperInterface {
    private Board myBoard;
    private String boardID;
    // status lists
    private List<Card> listUnscheduled = new ArrayList<>();
    private List<Card> listInProcess = new ArrayList<>();
    private List<Card> listCompleted = new ArrayList<>();
    private List<Card> listOverdue = new ArrayList<>();
    // menu popup
    private MenuBuilder menuBuilder;
    // views
    private TextView tvTitle;
    private TextView tvUnscheduledNumber, tvInProcessNumber, tvCompletedNumber, tvOverdueNumber;
    private RecyclerView rcvUnscheduled, rcvInProcess, rcvCompleted, rcvOverdue;
    private ImageView ivBackToHome, ivCardOption;
    private CardListAdapter unscheduledListAdapter, inProcessListAdapter, completedListAdapter, overdueListAdapter;
    private LinearLayout llStatusListTopBar;
    private SwipeRefreshLayout statusSwipeRefreshLayout;
    // Firestore database
    private FirebaseFirestore writeCardFirestore = FirebaseFirestore.getInstance();
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_list);

        // findviewbyids
        findViewByIds();

        statusSwipeRefreshLayout.setOnRefreshListener(this);

        // Lấy thông tin board từ main activity
        myBoard = getBoardInfoFromHomeFragment();
        boardID = myBoard.getBoard_id();

        // set layout
        tvTitle.setText(myBoard.getName());
        llStatusListTopBar.setBackgroundTintList(getResources().getColorStateList(myBoard.getResource_id()));

        // read my card
//        readMyCardData();

        //Xử lí cho menu popup
        menuBuilder = new MenuBuilder(this);
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_popup_statuslist, menuBuilder);

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
        ivCardOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuPopup(v);
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        readMyCardData();
    }
    private void checkIfCardIsOverdue(List<Card> listInProcess) {
        Date currentDate = new Date();
        for (Card card: listInProcess) {
            try {
                Date deadline = convertStringToDate(card.getDeadline_at());

                if (deadline != null && deadline.before(currentDate)) {
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    CollectionReference reference = firestore.collection("Card");

                    reference.document(card.getCard_id())
                            .update("status", "Overdue")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("SuccessUpdateStatus", "Updated card status successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("UpdateStatusFailed", "Updated card status failed!");
                                }
                            });
            }
            } catch (ParseException e) {
                Log.e("ParseException", e.getMessage());
            }
        }
    }
    public static Date convertStringToDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        return dateFormat.parse(dateString);
    }
    private void readMyCardData() {
        resetStatus();
        readUnscheduledCard(this);
        readInProcessCard(this);
        readCompletedCard(this);
        readOverdueCard(this);
    }
    private void readUnscheduledCard(CardDataCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firestore.collection("Card");
        collectionReference
                .whereEqualTo("board_id", boardID)
                .whereEqualTo("status", "Unscheduled")
                .orderBy("is_pinned", Query.Direction.DESCENDING)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> unscheduledList = queryDocumentSnapshots.getDocuments();
                            List<Card> tempList = new ArrayList<>();

                            for (DocumentSnapshot documentSnapshot : unscheduledList) {
                                Card unscheduledCard = documentSnapshot.toObject(Card.class);
                                tempList.add(unscheduledCard);
                            }
                            callback.onDataReceived(tempList, "Unscheduled");
                        }
                    }
                })
                // Thêm xử lý sự kiện khi đang lấy dữ liệu
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ReadUnscheduledFailed", e.getMessage());
                    }
                });
    }
    private void readInProcessCard(CardDataCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firestore.collection("Card");
        collectionReference
                .whereEqualTo("board_id", boardID)
                .whereEqualTo("status", "In process")
                .orderBy("is_pinned", Query.Direction.DESCENDING)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> inProcessList = queryDocumentSnapshots.getDocuments();
                            List<Card> tempList = new ArrayList<>();

                            for (DocumentSnapshot documentSnapshot : inProcessList) {
                                Card inProcessCard = documentSnapshot.toObject(Card.class);
                                tempList.add(inProcessCard);
                            }
                            callback.onDataReceived(tempList, "In process");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ReadInProcessFailed", e.getMessage());
                    }
                });
    }
    private void readCompletedCard(CardDataCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firestore.collection("Card");
        collectionReference
                .whereEqualTo("board_id", boardID)
                .whereEqualTo("status", "Completed")
                .orderBy("is_pinned", Query.Direction.DESCENDING)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> completedList = queryDocumentSnapshots.getDocuments();
                            List<Card> tempList = new ArrayList<>();

                            for (DocumentSnapshot documentSnapshot : completedList) {
                                Card completedCard = documentSnapshot.toObject(Card.class);
                                tempList.add(completedCard);
                            }
                            callback.onDataReceived(tempList, "Completed");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ReadCompletedFailed", e.getMessage());
                    }
                });
    }
    private void readOverdueCard(CardDataCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firestore.collection("Card");
        collectionReference
                .whereEqualTo("board_id", boardID)
                .whereEqualTo("status", "Overdue")
                .orderBy("is_pinned", Query.Direction.DESCENDING)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> overdueList = queryDocumentSnapshots.getDocuments();
                            List<Card> tempList = new ArrayList<>();

                            for (DocumentSnapshot documentSnapshot : overdueList) {
                                Card overdueCard = documentSnapshot.toObject(Card.class);
                                tempList.add(overdueCard);
                            }
                            callback.onDataReceived(tempList, "Overdue");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ReadOverdueFailed", e.getMessage());
                    }
                });
    }
    private void setStatusListNumber() {
        tvUnscheduledNumber.setText("(" + listUnscheduled.size() + ")");
        tvInProcessNumber.setText("(" + listInProcess.size() + ")");
        tvCompletedNumber.setText("(" + listCompleted.size() + ")");
        tvOverdueNumber.setText("(" + listOverdue.size() + ")");
    }
    @Override
    public void onDataReceived(List<Card> cards, String identifier) {
        if (identifier.equals("Unscheduled")) {
            this.listUnscheduled.clear();
            this.listUnscheduled.addAll(cards);
            if (listUnscheduled.size() != 0) {
                unscheduledListAdapter.notifyDataSetChanged();
            }
        } else if (identifier.equals("In process")) {
            this.listInProcess.clear();
            this.listInProcess.addAll(cards);
            if (listInProcess.size() != 0) {
                checkIfCardIsOverdue(listInProcess);
                inProcessListAdapter.notifyDataSetChanged();
            }
        } else if (identifier.equals("Completed")) {
            this.listCompleted.clear();
            this.listCompleted.addAll(cards);
            if (listCompleted.size() != 0)
                completedListAdapter.notifyDataSetChanged();
        } else if (identifier.equals("Overdue")) {
            this.listOverdue.clear();
            this.listOverdue.addAll(cards);
            if (listOverdue.size() != 0)
                overdueListAdapter.notifyDataSetChanged();
        }
        setStatusListNumber();
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

        dialog.setCancelable(true);

        EditText etCardName = (EditText) dialog.findViewById(R.id.et_card_name_to_add);
        TextView btnCancel = (TextView) dialog.findViewById(R.id.btn_cancel_add_card_dialog);
        TextView btnOK = (TextView) dialog.findViewById(R.id.btn_ok_add_card_dialog);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etCardName.getText().toString())) {
                    Card writeCard = new Card(formatCardId(getCurrentTime()), boardID, etCardName.getText().toString(),
                            0, "", "", new ArrayList<>(), new ArrayList<>(), getCurrentTime(), false, false,
                            getString(R.string.Unscheduled));

                    writeCardDataToFireStoreWithOnlyCardName(writeCard);
                }
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
                        Log.e("WriteCardFailed", e.getMessage());
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
        RecyclerView.ItemDecoration unscheduledItemDecoration = new DividerItemDecoration(StatusListActivity.this, DividerItemDecoration.VERTICAL);
        rcvUnscheduled.addItemDecoration(unscheduledItemDecoration);
        RecyclerView.ItemDecoration inProcessItemDecoration = new DividerItemDecoration(StatusListActivity.this, DividerItemDecoration.VERTICAL);
        rcvInProcess.addItemDecoration(inProcessItemDecoration);
        RecyclerView.ItemDecoration completedItemDecoration = new DividerItemDecoration(StatusListActivity.this, DividerItemDecoration.VERTICAL);
        rcvCompleted.addItemDecoration(completedItemDecoration);
        RecyclerView.ItemDecoration overdueItemDecoration = new DividerItemDecoration(StatusListActivity.this, DividerItemDecoration.VERTICAL);
        rcvOverdue.addItemDecoration(overdueItemDecoration);

        unscheduledListAdapter = new CardListAdapter(listUnscheduled, new ClickCardItemInterface() {
            @Override
            public void OnClickCardItem(Card card) {
                onClickCardItemGoToCardActivity(card);
            }
        });
        rcvUnscheduled.setAdapter(unscheduledListAdapter);
        ItemTouchHelper.SimpleCallback unscheduledSimpleCallback = new CardListItemTouchHelper(0, ItemTouchHelper.LEFT, this, "Unscheduled");
        new ItemTouchHelper(unscheduledSimpleCallback).attachToRecyclerView(rcvUnscheduled);

        inProcessListAdapter = new CardListAdapter(listInProcess, new ClickCardItemInterface() {
            @Override
            public void OnClickCardItem(Card card) {
                onClickCardItemGoToCardActivity(card);
            }
        });
        rcvInProcess.setAdapter(inProcessListAdapter);
        ItemTouchHelper.SimpleCallback inProcessSimpleCallback = new CardListItemTouchHelper(0, ItemTouchHelper.LEFT, this, "In process");
        new ItemTouchHelper(inProcessSimpleCallback).attachToRecyclerView(rcvInProcess);

        completedListAdapter = new CardListAdapter(listCompleted, new ClickCardItemInterface() {
            @Override
            public void OnClickCardItem(Card card) {
                onClickCardItemGoToCardActivity(card);
            }
        });
        rcvCompleted.setAdapter(completedListAdapter);
        ItemTouchHelper.SimpleCallback completedSimpleCallback = new CardListItemTouchHelper(0, ItemTouchHelper.LEFT, this, "Completed");
        new ItemTouchHelper(completedSimpleCallback).attachToRecyclerView(rcvCompleted);

        overdueListAdapter = new CardListAdapter(listOverdue, new ClickCardItemInterface() {
            @Override
            public void OnClickCardItem(Card card) {
                onClickCardItemGoToCardActivity(card);
            }
        });
        rcvOverdue.setAdapter(overdueListAdapter);
        ItemTouchHelper.SimpleCallback overdueSimpleCallback = new CardListItemTouchHelper(0, ItemTouchHelper.LEFT, this, "Overdue");
        new ItemTouchHelper(overdueSimpleCallback).attachToRecyclerView(rcvOverdue);
    }
    private void deleteFilesInFolder(String folderPath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child(folderPath);

        storageReference.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference item : listResult.getItems()) {
                        item.delete()
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Xoá thành công
                                    Log.d("FileDelete", "Deleted file: " + item.getName());
                                })
                                .addOnFailureListener(exception -> {
                                    // Xoá thất bại
                                    Log.e("FileDelete", "Failed to delete file: " + item.getName() + ", Error: " + exception.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Lỗi khi lấy danh sách tệp tin
                    Log.e("FileList", "Failed to list files in folder: " + folderPath + ", Error: " + e.getMessage());
                });
    }
    private void deleteCardFromStorage(Card card) {
        List<AttachedFile> attachedFiles = card.getAttached_file_list();
        String userID = myBoard.getUser_id();

        for (AttachedFile attachedFile : attachedFiles) {
            String fileFormat = attachedFile.getFormat();
            String folderPath = userID + "/" + boardID + "/" + card.getCard_id() + "/attached_files/" + fileFormat;

            deleteFilesInFolder(folderPath);
        }
    }
    private void deleteCardFromFirestore(Card card) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firestore.collection("Card");

        collectionReference.document(card.getCard_id())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("SuccessfulDelete", "Deleted card from Firestore successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FailedDelete", e.getMessage());
                    }
                });
    }
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, String identifier) {
        if (viewHolder instanceof CardListAdapter.CardListViewHolder) {
            // Xử lý việc vuốt item trong danh sách tương ứng với identifier
            if (identifier.equals("Unscheduled")) {
                // Xoá item trong listUnscheduled tại vị trí position
                String cardNameDelete = listUnscheduled.get(viewHolder.getAdapterPosition()).getName();

                final Card cardDelete = listUnscheduled.get(viewHolder.getAdapterPosition());
                final int indexCardDelete = viewHolder.getAdapterPosition();

                // remove item
                unscheduledListAdapter.removeItem(indexCardDelete);

                Snackbar snackbar = Snackbar.make(rcvUnscheduled, "Đã xoá '" + cardNameDelete + "' khỏi danh sách!", Snackbar.LENGTH_LONG);
                snackbar.setAction("Hoàn tác", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unscheduledListAdapter.undoItem(cardDelete, indexCardDelete);
                        if (indexCardDelete == 0 || indexCardDelete == listUnscheduled.size() - 1) {
                            rcvUnscheduled.scrollToPosition(indexCardDelete);
                        }
                    }
                });
                // Thêm hành động khi Snackbar kết thúc
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_SWIPE ||
                                event == DISMISS_EVENT_CONSECUTIVE || event == DISMISS_EVENT_MANUAL) {
                            deleteCardFromStorage(cardDelete);
                            deleteCardFromFirestore(cardDelete);
                            setStatusListNumber();
                        }
                    }
                });
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.show();
            } else if (identifier.equals("In process")) {
                // Xoá item trong listInProcess tại vị trí position
                String cardNameDelete = listInProcess.get(viewHolder.getAdapterPosition()).getName();

                final Card cardDelete = listInProcess.get(viewHolder.getAdapterPosition());
                final int indexCardDelete = viewHolder.getAdapterPosition();

                // remove item
                inProcessListAdapter.removeItem(indexCardDelete);

                Snackbar snackbar = Snackbar.make(rcvInProcess, "Đã xoá '" + cardNameDelete + "' khỏi danh sách!", Snackbar.LENGTH_LONG);
                snackbar.setAction("Hoàn tác", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inProcessListAdapter.undoItem(cardDelete, indexCardDelete);
                        if (indexCardDelete == 0 || indexCardDelete == listInProcess.size() - 1) {
                            rcvInProcess.scrollToPosition(indexCardDelete);
                        }
                    }
                });
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.show();
            } else if (identifier.equals("Completed")) {
                // Xoá item trong listCompleted tại vị trí position
                String cardNameDelete = listCompleted.get(viewHolder.getAdapterPosition()).getName();

                final Card cardDelete = listCompleted.get(viewHolder.getAdapterPosition());
                final int indexCardDelete = viewHolder.getAdapterPosition();

                // remove item
                completedListAdapter.removeItem(indexCardDelete);

                Snackbar snackbar = Snackbar.make(rcvCompleted, "Đã xoá '" + cardNameDelete + "' khỏi danh sách!", Snackbar.LENGTH_LONG);
                snackbar.setAction("Hoàn tác", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        completedListAdapter.undoItem(cardDelete, indexCardDelete);
                        if (indexCardDelete == 0 || indexCardDelete == listCompleted.size() - 1) {
                            rcvCompleted.scrollToPosition(indexCardDelete);
                        }
                    }
                });
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.show();
            } else if (identifier.equals("Overdue")) {
                // Xoá item trong listOverdue tại vị trí position
                String cardNameDelete = listOverdue.get(viewHolder.getAdapterPosition()).getName();

                final Card cardDelete = listOverdue.get(viewHolder.getAdapterPosition());
                final int indexCardDelete = viewHolder.getAdapterPosition();

                // remove item
                overdueListAdapter.removeItem(indexCardDelete);

                Snackbar snackbar = Snackbar.make(rcvOverdue, "Đã xoá '" + cardNameDelete + "' khỏi danh sách!", Snackbar.LENGTH_LONG);
                snackbar.setAction("Hoàn tác", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        overdueListAdapter.undoItem(cardDelete, indexCardDelete);
                        if (indexCardDelete == 0 || indexCardDelete == listOverdue.size() - 1) {
                            rcvOverdue.scrollToPosition(indexCardDelete);
                        }
                    }
                });
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.show();
            }
        }
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
        ivCardOption = (ImageView) findViewById(R.id.iv_card_option);

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
    private void openRenameBoardDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_rename_board);

        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true);

        EditText etRenameBoard = (EditText) dialog.findViewById(R.id.et_rename_board);
        ImageView ivRenameBoardConfirm = (ImageView) dialog.findViewById(R.id.iv_rename_board_confirm);
        ivRenameBoardConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etRenameBoard.getText().toString())) {
                    myBoard.setName(etRenameBoard.getText().toString());

                    tvTitle.setText(etRenameBoard.getText().toString());
                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    CollectionReference collectionReference = firestore.collection("Board");

                    collectionReference.document(boardID)
                            .update("name", myBoard.getName())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("SuccessUpdateBoard", "Updated board successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("UpdateBoardFailed", e.getMessage());
                                }
                            });
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void openDeleteBoardDialog() {
        final Dialog dialog = new Dialog(this);
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
                deleteMyBoard();
                dialog.dismiss();
                onBackPressed();
                finish();
            }
        });
        dialog.show();
    }
    private void deleteMyBoard() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firestore.collection("Board");

        collectionReference.document(boardID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("SuccessfulDelete", "Deleted board from Firestore successfully");
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
    public void showMenuPopup(View v) {
        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(StatusListActivity.this, menuBuilder, v);
        menuPopupHelper.setForceShowIcon(true);
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_pop_up_item_add_card) {
                    openAddCardDialog();
                } else if (item.getItemId() == R.id.menu_pop_up_item_edit_board) {
                    openRenameBoardDialog();
                } else if (item.getItemId() == R.id.menu_pop_up_item_delete_board) {
                    openDeleteBoardDialog();
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