package com.example.teamv.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.teamv.R;
import com.example.teamv.adapter.AttachedFileAdapter;
import com.example.teamv.adapter.ColorPickerAdapter;
import com.example.teamv.adapter.ToDoListAdapter;
import com.example.teamv.my_interface.ToDoListItemTouchHelperInterface;
import com.example.teamv.object.AttachedFile;
import com.example.teamv.object.Card;
import com.example.teamv.object.ToDoListTask;
import com.example.teamv.recyclerview_class.ToDoListItemTouchHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CardActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, ToDoListItemTouchHelperInterface {
    private Card myCard;
    private int selectedBackgroundColor = 0;
    private Calendar selectedDeadline;
    // viewgroups & views
    private LinearLayout llTopBar;
    private ImageView ivBackToStatusList, ivCardOption,
            ivCardBackground,
            ivIsCheckedComplete,
            ivToDoListOption,
            ivToDoListAddTaskSaveChange;
    private CardView cvSetCardBackground,
            cvAddToDoList,
            cvAddAttachedFile;
    private RelativeLayout rlToDoListAddTask;
    private TextView tvCardName, tvCardCreatedAt,
            tvDeadline;
    private EditText etCardDescription,
            etToDoListAddTask;
    private RecyclerView rcvToDoList,
                rcvAttachedFileList;
    private SwipeRefreshLayout cardSwipeRefreshLayout;
    // List
    private List<ToDoListTask> toDoList = new ArrayList<>();
    private List<AttachedFile> attachedFileList = new ArrayList<>();
    // Adapter
    private ToDoListAdapter toDoListAdapter;
    private AttachedFileAdapter attachedFileAdapter;

    // Firebase firestore
    private FirebaseFirestore updateCardFirestore = FirebaseFirestore.getInstance();
    private CollectionReference cardCollectionReference = updateCardFirestore.collection("Card");
    // Firebase Storage
    private StorageReference attachedFileStorageReference = FirebaseStorage.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        // set views
        findViewByIds();

        // set refresh listener
        cardSwipeRefreshLayout.setOnRefreshListener(CardActivity.this);

        // get card data from status list
        myCard = getCardInforFromStatusListActivity();

        // set UI
        setCardUI(myCard);
    }
    @Override
    protected void onStart() {
        super.onStart();
        // update UI
        updateCardUI(myCard);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }


    private void updateCardUI (Card card) {
        // click change background
        cvSetCardBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDiaLogColor(card);
            }
        });
        // timeline clicking
        tvDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeadlinePickerDialog(card);
            }
        });
        // click check
        ivIsCheckedComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = card.getStatus();
                if (card.isIs_checked_complete()) {
                    card.setIs_checked_complete(false);
                    ivIsCheckedComplete.setImageResource(R.drawable.ic_checkbox);

                    String deadline = card.getDeadline_at();
                    if (deadline.equals("")) {
                        card.setStatus("Unscheduled");
                    } else {
                        if (calculateDeadlineDifference(convertStringToCalendar(deadline)) > 0) {
                            card.setStatus("In process");
                            tvDeadline.setBackgroundResource(R.drawable.bg_corner_in_process);
                        } else {
                            card.setStatus("Overdue");
                            tvDeadline.setBackgroundResource(R.drawable.bg_corner_overdue);
                        }
                    }
                } else {
                    card.setIs_checked_complete(true);
                    ivIsCheckedComplete.setImageResource(R.drawable.ic_checked);

                    switch (status) {
                        case "Unscheduled":
                            card.setStatus("Completed");
                            break;
                        case "In process":
                            card.setStatus("Completed");
                            tvDeadline.setBackgroundResource(R.drawable.bg_corner_completed);
                            break;
                        case "Overdue":
                            break;
                    }
                }
            }
        });
        ivBackToStatusList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMyCardData(card);
                onBackPressed();
                finish();
            }
        });
        cvAddToDoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlToDoListAddTask.setVisibility(View.VISIBLE);
                etToDoListAddTask.requestFocus();
                ivToDoListAddTaskSaveChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String taskName = etToDoListAddTask.getText().toString();
                        if (taskName == null || taskName.equals("")) {
                            rlToDoListAddTask.setVisibility(View.GONE);
                        } else {
                            toDoList.add(new ToDoListTask(taskName, false));
                            card.setTo_do_list(toDoList);
                            toDoListAdapter.notifyDataSetChanged();
                            rlToDoListAddTask.setVisibility(View.GONE);
                        }
                        etToDoListAddTask.setText("");
                    }
                });
            }
        });
        cvAddAttachedFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFileFromDevice();
            }
        });
    }
    private String getUserIDBySplitingBoardID(String boardID){
        return boardID.substring(0, boardID.indexOf('?'));
    }
    private void selectFileFromDevice() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn tệp đính kèm"), 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            // Lấy loại MIME của tệp tin
            String mimeType = getContentResolver().getType(uri);

            String fileFormat = "";
            // Xử lý tệp tin dựa trên loại MIME
            if (mimeType != null) {
                if (mimeType.startsWith("image/")) {
                    fileFormat = "image";
                } else if (mimeType.startsWith("video/")) {
                    fileFormat = "video";
                } else if (mimeType.equals("application/pdf")) {
                    fileFormat = "pdf";
                } else if (mimeType.equals("application/msword") || mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                    fileFormat = "ms-word";
                } else if (mimeType.equals("application/vnd.ms-powerpoint") || mimeType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
                    fileFormat = "ms-powerpoint";
                } else if (mimeType.equals("application/vnd.ms-excel") || mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                    fileFormat = "ms-excel";
                } else if (mimeType.equals("application/zip")) {
                    fileFormat = "zip";
                } else {
                    fileFormat = "others";
                }
            }
            String fileName = getFileName(uri);
            String fileCreatedAt = getCurrentTime();
            double d = getFileSize(uri);
            String fileSize = String.valueOf(roundToTwoDecimalPlaces(d));

            String fileExtension = getFileExtension(uri);

            uploadFileToFirestore(data.getData(), fileFormat, fileExtension, fileName, fileCreatedAt, fileSize);
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String extension;

        // Kiểm tra xem Uri có scheme là "content" không
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        } else {
            // Lấy đuôi từ đường dẫn của Uri nếu không phải là "content" (ví dụ: "file" scheme)
            extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        }
        return extension;
    }
    // Assuming 'uri' is the Uri of the file you want to get the size
    public double getFileSize(Uri uri) {
        long fileSize = 0;
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.MediaColumns.SIZE};
            cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int sizeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE);
                fileSize = cursor.getLong(sizeColumnIndex);
            }
        } catch (Exception e) {
            Log.e("GetFileSize", e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return (double) fileSize / (1024 * 1024);
    }
    private double roundToTwoDecimalPlaces(double number) {
        // Tạo mẫu để làm tròn đến hai chữ số thập phân
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP); // Điều chỉnh cách làm tròn (có thể thay đổi nếu cần)

        // Sử dụng DecimalFormat để làm tròn số
        return Double.parseDouble(df.format(number).replace(",", "."));
    }
    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        // get file name
        String uriString = uri.toString();
        File myFile = new File(uriString);

        String attachedFilePath = myFile.getAbsolutePath();
        String attachedFileName = "";

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = this.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    attachedFileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else if (uriString.startsWith("file://")) {
            attachedFileName = myFile.getName();
        }
        return attachedFileName;
    }
    private void uploadFileToFirestore (Uri data, String fileFormat, String fileExtension, String fileName, String fileCreatedAt, String fileSize) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đang tải tệp...");
        progressDialog.show();

        String boardID = myCard.getBoard_id();
        String userID = getUserIDBySplitingBoardID(boardID);
        final StorageReference reference = attachedFileStorageReference.child(userID + "/" + boardID + "/" + myCard.getCard_id() + "/attached_files/" + fileFormat + "/" + System.currentTimeMillis() + "." + fileExtension);

        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri uri = uriTask.getResult();
                        AttachedFile attachedFile = new AttachedFile(fileName, fileCreatedAt, fileSize, uri.toString(), fileFormat);
                        attachedFileList.add(attachedFile);
                        myCard.setAttached_file_list(attachedFileList);
                        attachedFileAdapter.notifyDataSetChanged();
                        Toast.makeText(CardActivity.this, "Tải tệp thành công!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        float percentUploading = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage((int) percentUploading + "%");
                    }
                });
    }
    private void updateMyCardData (Card card) {
        String cardID = card.getCard_id();
        cardCollectionReference.document(cardID)
                .update(
                        "name", card.getName(),
                        "resource_id", card.getResource_id(),
                        "description", card.getDescription(),
                        "deadline_at", card.getDeadline_at(),
                        "to_do_list", card.getTo_do_list(),
                        "attached_file_list", card.getAttached_file_list(),
                        "is_checked_complete", card.isIs_checked_complete(),
                        "is_pinned", card.isIs_pinned(),
                        "status", card.getStatus()
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.i("SuccessUpdateCard", "Updated card successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("UpdateCardFailed", e.getMessage());
                    }
                });
    }
    private void setCardUI (Card card) {
        tvCardName.setText(card.getName());
        tvCardCreatedAt.setText(card.getCreated_at());

        // background
        if (card.getResource_id() > 0) {
            ivCardBackground.setImageResource(card.getResource_id());
            ivCardBackground.setVisibility(View.VISIBLE);

            llTopBar.setBackgroundResource(card.getResource_id());
        }
        // description
        if (!card.getDescription().equals("")) {
            etCardDescription.setText(card.getDescription());
        }
        // status
        boolean isChecked = card.isIs_checked_complete();
        String status = card.getStatus();
        String deadline = card.getDeadline_at();
        if (isChecked) {
            ivIsCheckedComplete.setImageResource(R.drawable.ic_checked);
            if (!deadline.equals("")) {
                tvDeadline.setText("Hết hạn: " + deadline);
                switch (status) {
                    case "Completed":
                        tvDeadline.setBackgroundResource(R.drawable.bg_corner_completed);
                        break;
                    case "Overdue":
                        tvDeadline.setBackgroundResource(R.drawable.bg_corner_overdue);
                        break;
                }
            }
        } else {
            if (!status.equals("Unscheduled")) {
                tvDeadline.setText("Hết hạn: " + deadline);
                switch (status) {
                    case "In process":
                        if (calculateDeadlineDifference(convertStringToCalendar(deadline)) > 0) {
                            tvDeadline.setBackgroundResource(R.drawable.bg_corner_in_process);
                        } else {
                            tvDeadline.setBackgroundResource(R.drawable.bg_corner_overdue);
                            card.setStatus("Overdue");
                        }
                        break;
                    case "Completed":
                        tvDeadline.setBackgroundResource(R.drawable.bg_corner_completed);
                        break;
                    case "Overdue":
                        tvDeadline.setBackgroundResource(R.drawable.bg_corner_overdue);
                        break;
                }
            }
        }
        // to-do list
        toDoList = card.getTo_do_list();
        setToDoList();

        // attached file list
        attachedFileList = card.getAttached_file_list();
        setAttachedFileList();
    }
    private String getCurrentTime() {
        Date currentTime = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return new String(simpleDateFormat.format(currentTime));
    }
    private void setToDoList() {
        rcvToDoList.setLayoutManager(new LinearLayoutManager(CardActivity.this));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(CardActivity.this, DividerItemDecoration.VERTICAL);
        rcvToDoList.addItemDecoration(itemDecoration);
        toDoListAdapter = new ToDoListAdapter(toDoList);
        rcvToDoList.setAdapter(toDoListAdapter);

        // item touch helper
        ItemTouchHelper.SimpleCallback simpleCallback = new ToDoListItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rcvToDoList);
    }
    // swipe to delete to do list task
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ToDoListAdapter.ToDoListViewHolder) {
            String toDoListTaskNameDelete = toDoList.get(viewHolder.getAdapterPosition()).getName();

            final ToDoListTask toDoListTaskDelete = toDoList.get(viewHolder.getAdapterPosition());
            final int indexToDoListTaskDelete = viewHolder.getAdapterPosition();

            // remove item
            toDoListAdapter.removeItem(indexToDoListTaskDelete);

            Snackbar snackbar = Snackbar.make(rcvToDoList, "Đã xoá '" + toDoListTaskNameDelete + "' khỏi danh sách!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Hoàn tác", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toDoListAdapter.undoItem(toDoListTaskDelete, indexToDoListTaskDelete);
                    if (indexToDoListTaskDelete == 0 || indexToDoListTaskDelete == toDoList.size() - 1) {
                        rcvToDoList.scrollToPosition(indexToDoListTaskDelete);
                    }
                }
            });
            snackbar.setActionTextColor(Color.WHITE);
            snackbar.show();
        }
    }
    private void setAttachedFileList() {
        rcvAttachedFileList.setLayoutManager(new LinearLayoutManager(CardActivity.this));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(CardActivity.this, DividerItemDecoration.VERTICAL);
        rcvAttachedFileList.addItemDecoration(itemDecoration);
        attachedFileAdapter = new AttachedFileAdapter(attachedFileList);
        rcvAttachedFileList.setAdapter(attachedFileAdapter);
    }
    private void openDeadlinePickerDialog(Card card) {
        final Calendar currentDate = Calendar.getInstance();
        final int year = currentDate.get(Calendar.YEAR);
        final int month = currentDate.get(Calendar.MONTH);
        final int day = currentDate.get(Calendar.DAY_OF_MONTH);
        final int hour = currentDate.get(Calendar.HOUR_OF_DAY);
        final int minute = currentDate.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CardActivity.this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDeadline = Calendar.getInstance();
                selectedDeadline.set(Calendar.YEAR, year);
                selectedDeadline.set(Calendar.MONTH, month);
                selectedDeadline.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog timePickerDialog = new TimePickerDialog(CardActivity.this, R.style.DatePickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedDeadline.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDeadline.set(Calendar.MINUTE, minute);

                        String deadline = formatSelectedDate(selectedDeadline);
                        tvDeadline.setText("Hết hạn: " + deadline);
                        ivIsCheckedComplete.setVisibility(View.VISIBLE);

                        if (card.isIs_checked_complete()) {
                            if (calculateDeadlineDifference(selectedDeadline) > 0) {
                                tvDeadline.setBackgroundResource(R.drawable.bg_corner_completed);

                                card.setStatus("Completed");
                            } else {
                                tvDeadline.setBackgroundResource(R.drawable.bg_corner_overdue);

                                card.setStatus("Overdue");
                            }
                        } else {
                            if (calculateDeadlineDifference(selectedDeadline) > 0) {
                                tvDeadline.setBackgroundResource(R.drawable.bg_corner_in_process);

                                card.setStatus("In process");
                            } else {
                                tvDeadline.setBackgroundResource(R.drawable.bg_corner_overdue);

                                card.setStatus("Overdue");
                            }
                        }
                        card.setDeadline_at(deadline);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        }, year, month, day);
        datePickerDialog.show();
    }
    public static Calendar convertStringToCalendar(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        try {
            Date parsedDate = dateFormat.parse(dateString);
            calendar.setTime(parsedDate);
        } catch (ParseException e) {
            Log.e("ParseException", e.getMessage());
        }

        return calendar;
    }
    private String formatSelectedDate (Calendar selectedDate) {
        if (selectedDate == null)
            return "";
        // format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        String formatSelectedDate = dateFormat.format(selectedDate.getTime());
        return formatSelectedDate;
    }
    public void openDiaLogColor(Card card)
    {
        final Dialog dialog = new Dialog(CardActivity.this);
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
        ColorPickerAdapter adapter = new ColorPickerAdapter(CardActivity.this, colors);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedBackgroundColor = colors[position];

                ivCardBackground.setImageResource(selectedBackgroundColor);
                ivCardBackground.setVisibility(View.VISIBLE);

                llTopBar.setBackgroundResource(selectedBackgroundColor);

                card.setResource_id(selectedBackgroundColor);

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
    public static long calculateDeadlineDifference(Calendar deadline) {
        Calendar currentDate = Calendar.getInstance();
        long differenceInMillis = deadline.getTimeInMillis() - currentDate.getTimeInMillis();

        return differenceInMillis;
    }
    private Card getCardInforFromStatusListActivity() {
        Bundle bundle = getIntent().getExtras();
        Card card = (Card) bundle.get("object_card");
        return card;
    }
    private void findViewByIds() {
        llTopBar = (LinearLayout) findViewById(R.id.ll_card_top_bar);
        ivBackToStatusList = (ImageView) findViewById(R.id.iv_back_to_status_list);
        ivCardOption = (ImageView) findViewById(R.id.iv_card_option);

        ivCardBackground = (ImageView) findViewById(R.id.iv_card_background);
        cvSetCardBackground = (CardView) findViewById(R.id.cv_set_card_background);

        tvCardName = (TextView) findViewById(R.id.tv_card_name);
        tvCardCreatedAt = (TextView) findViewById(R.id.tv_card_created_at);

        etCardDescription = (EditText) findViewById(R.id.et_card_description);

        tvDeadline = (TextView) findViewById(R.id.tv_deadline);
        ivIsCheckedComplete = (ImageView) findViewById(R.id.iv_is_checked_complete);

        ivToDoListOption = (ImageView) findViewById(R.id.iv_to_do_list_option);
        rcvToDoList = (RecyclerView) findViewById(R.id.rcv_to_do_list);
        cvAddToDoList = (CardView) findViewById(R.id.cv_add_to_do_list);

        rcvAttachedFileList = (RecyclerView) findViewById(R.id.rcv_attached_file_list);
        cvAddAttachedFile = (CardView) findViewById(R.id.cv_add_attached_file);

        rlToDoListAddTask = (RelativeLayout) findViewById(R.id.rl_to_do_list_add_task);
        etToDoListAddTask = (EditText) findViewById(R.id.et_to_do_list_add_task);
        ivToDoListAddTaskSaveChange = (ImageView) findViewById(R.id.iv_to_do_list_add_task_save_change);

        cardSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.card_swipeRefreshLayout);
    }
    @Override
    public void onRefresh() {
        setCardUI(myCard);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cardSwipeRefreshLayout.setRefreshing(false);
            }
        }, 500);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateMyCardData(myCard);
        finish();
    }
}