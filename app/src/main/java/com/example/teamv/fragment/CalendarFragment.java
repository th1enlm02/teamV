package com.example.teamv.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.teamv.R;
import com.example.teamv.activity.StatusListActivity;
import com.example.teamv.adapter.CalendarAdapter;
import com.example.teamv.my_interface.AllCardDataCallback;
import com.example.teamv.my_interface.CardDataCallback;
import com.example.teamv.object.Board;
import com.example.teamv.object.Card;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener, AllCardDataCallback {
    private List<Card> myCardList = new ArrayList<>();
    private List<Card> myCardListForDashBoard = new ArrayList<>();

    private TextView tv_MonthYear,tv_DeadlineInDay; //tv để hiện thông tin tháng năm
    private RecyclerView rv_Calendar;
    private LocalDate selectedDate; // ngày hiện tại
    public CalendarAdapter adapterCalendar;
    private ArrayList<String> deadlinesInMonth ; // những ngày có deadline trong tháng
    private ArrayList<String> daysInMonth;  // những ngày trong tháng
    private ArrayList<LocalDate> deadlines = new ArrayList<>(); // mảng chứa thời gian có deadline
    private View view;
    private ImageView iv_nextmonth, iv_previousmonth;
    LinearLayout ll_task;
    TextView textView;
    ListView lv_item_task_in_calendar;
    String check="0";
    //biến để tối ưu hiển thị click vào lịch


    //Các công việc
    int unschedule = 0;
    int completion=0;
    int overdue=0;
    int inprocess=0;

    private ArrayAdapter<String> adapter; // adapter mặc định cho item task khi click vào ngày
    // firebase
    private String userID;

    private PieChart pieChart; // dùng cho dashboard
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);


        findViewByIds();


        // Khởi tạo ArrayAdapter
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);

        lv_item_task_in_calendar.setAdapter(adapter);
        lv_item_task_in_calendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //
                //onClickBoardItemGoToStatusList();
            }
        });

        tv_DeadlineInDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ll_task.getVisibility()==View.GONE)
                {
                    ll_task.setVisibility(View.VISIBLE);
                }
                else {
                    ll_task.setVisibility(View.GONE);
                }
            }
        });


        iv_previousmonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = selectedDate.minusMonths(1);
                setMonthView();
            }
        });
        iv_nextmonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = selectedDate.plusMonths(1);
                setMonthView();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // get all cards data
        // sau hàm này là có thể sử dụng list card được lấy về
        readAllMyCard(this);
    }

    private String getUserID() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userID = null;
        if (firebaseUser == null) {
            Log.e("GetUserInfor", "Not found");
        } else {
            userID = firebaseUser.getUid();
        }
        return userID;
    }
    private void readAllMyCard(AllCardDataCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firestore.collection("Card");
        userID = getUserID();

        Query query = collectionReference
                .whereEqualTo("user_id", userID);

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> myList = queryDocumentSnapshots.getDocuments();
                            List<Card> tempList = new ArrayList<>();

                            for (DocumentSnapshot documentSnapshot : myList) {
                                Card myCard = documentSnapshot.toObject(Card.class);
                                tempList.add(myCard);
                            }
                            callback.onDataReceived(tempList);
                        }
                        Log.d("GetAllCards", "Read all cards successfully");
                    }
                });
    }
    @Override
    public void onDataReceived(List<Card> cards) {
        this.myCardList.clear();
        this.myCardList.addAll(cards);
        myCardListForDashBoard = myCardList;
        selectedDate = LocalDate.now();
        getFormattedCardDates();
        initDeadlines();
        setMonthView();



    }

    void initDeadlines() {
        // Tạo mảng các deadline


        deadlines = new ArrayList<>();
//                deadlines.add(LocalDate.of(2024, Month.JANUARY, 10));
//                deadlines.add(LocalDate.of(2024, Month.JANUARY, 15));
//                deadlines.add(LocalDate.of(2024, Month.FEBRUARY, 14));
//                deadlines.add(LocalDate.of(2023, Month.JANUARY, 5));
//                deadlines.add(LocalDate.of(2024, Month.JANUARY, 11));
//                deadlines.add(LocalDate.of(2024, Month.JANUARY, 12));
//                deadlines.add(LocalDate.of(2024, Month.FEBRUARY, 18));
//                deadlines.add(LocalDate.of(2023, Month.JANUARY, 5));
        //thay các thành phần của deadlines là card.getdeadline at


        for (Card card : myCardList) {
            if(!card.getDeadline_at().equals(""))
            {
                LocalDate deadlineDate = LocalDate.parse(card.getDeadline_at(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                deadlines.add(deadlineDate);
            }

        }


    }

    private void findViewByIds()
    {
        rv_Calendar = (RecyclerView) view.findViewById(R.id.rv_Calendar);
        tv_MonthYear = (TextView) view.findViewById(R.id.tv_MonthYear);
        iv_nextmonth = (ImageView) view.findViewById(R.id.iv_nextmonth);
        iv_previousmonth=(ImageView) view.findViewById(R.id.iv_previousmonth);
        lv_item_task_in_calendar =(ListView) view.findViewById(R.id.lv_task_in_calendar);
        ll_task =(LinearLayout) view.findViewById(R.id.ll_task);
        pieChart = (PieChart) view.findViewById(R.id.pieChart);
        tv_DeadlineInDay =(TextView) view.findViewById(R.id.tv_deadline_in_day);
    }

    private void initCard() {
        // Card 1
        Card card1 = new Card(
                "1",
                "Board1",
                "User1",
                "Làm bài tập",
                1,
                "Mô tả công việc 1",
                "2024-01-20",
                null, // Assume to_do_list is null for simplicity
                null, // Assume attached_file_list is null for simplicity
                "2024-01-13",
                false,
                true,
                "In Progress"
        );
        myCardList.add(card1);

        // Card 2
        Card card2 = new Card(
                "2",
                "Board1",
                "User2",
                "Học bài",
                2,
                "Mô tả công việc 2",
                "2024-01-25",
                null, // Assume to_do_list is null for simplicity
                null, // Assume attached_file_list is null for simplicity
                "2024-01-14",
                true,
                false,
                "Completed"
        );
        myCardList.add(card2);

        // Thêm 8 công việc khác
        // Card 3
        Card card3 = new Card(
                "3",
                "Board1",
                "User3",
                "Thực hành toán",
                3,
                "Mô tả công việc 3",
                "2024-01-10",
                null,
                null,
                "2024-01-05",
                false,
                true,
                "In Progress"
        );
        myCardList.add(card3);

        // Card 4
        Card card4 = new Card(
                "4",
                "Board1",
                "User4",
                "Viết báo cáo",
                4,
                "Mô tả công việc 4",
                "2024-01-15",
                null,
                null,
                "2024-01-11",
                true,
                false,
                "Completed"
        );
        myCardList.add(card4);

        // Card 5
        Card card5 = new Card(
                "5",
                "Board1",
                "User5",
                "Lập kế hoạch học tập",
                5,
                "Mô tả công việc 5",
                "2024-02-14",
                null,
                null,
                "2024-02-18",
                false,
                true,
                "In Progress"
        );
        myCardList.add(card5);

        // Card 6
        Card card6 = new Card(
                "6",
                "Board1",
                "User6",
                "Thiết kế đồ án",
                6,
                "Mô tả công việc 6",
                "2023-01-05",
                null,
                null,
                "2023-01-05",
                true,
                false,
                "Completed"
        );
        myCardList.add(card6);

        // Card 7
        Card card7 = new Card(
                "7",
                "Board1",
                "User7",
                "Dựng phim ngắn",
                7,
                "Mô tả công việc 7",
                "2024-01-11",
                null,
                null,
                "2024-01-12",
                false,
                true,
                "In Progress"
        );
        myCardList.add(card7);

        // Card 8
        Card card8 = new Card(
                "8",
                "Board1",
                "User8",
                "Sửa ảnh",
                8,
                "Mô tả công việc 8",
                "2024-02-18",
                null,
                null,
                "2023-01-05",
                true,
                false,
                "Completed"
        );
        myCardList.add(card8);

        // Card 9
        Card card9 = new Card(
                "9",
                "Board1",
                "User9",
                "Chơi guitar",
                9,
                "Mô tả công việc 9",
                "2024-01-05",
                null,
                null,
                "2024-01-11",
                false,
                true,
                "In Progress"
        );
        myCardList.add(card9);

        // Card 10
        Card card10 = new Card(
                "10",
                "Board1",
                "User10",
                "Viết blog",
                10,
                "Mô tả công việc 10",
                "2024-01-12",
                null,
                null,
                "2024-02-18",
                true,
                false,
                "Completed"
        );
        myCardList.add(card10);
        // Card 11
        Card card11 = new Card(
                "11",
                "Board11",
                "User11",
                "Đi chơi cùng bạn bè",
                1,
                "Mô tả công việc 1",
                "2024-01-20",
                null, // Assume to_do_list is null for simplicity
                null, // Assume attached_file_list is null for simplicity
                "2024-01-13",
                false,
                true,
                "In Progress"
        );
        myCardList.add(card11);
        // Card 12
        Card card12 = new Card(
                "12",
                "Board12",
                "User12",
                "Tập viết CV",
                1,
                "Mô tả công việc 1",
                "2024-01-20",
                null, // Assume to_do_list is null for simplicity
                null, // Assume attached_file_list is null for simplicity
                "2024-01-13",
                false,
                true,
                "In Progress"
        );
        myCardList.add(card12);
        // Card 13
        Card card13 = new Card(
                "13",
                "Board13",
                "User13",
                "Tập viết tiếng anh",
                1,
                "Mô tả công việc 1",
                "2024-01-20",
                null, // Assume to_do_list is null for simplicity
                null, // Assume attached_file_list is null for simplicity
                "2024-01-13",
                false,
                true,
                "In Progress"
        );
        myCardList.add(card13);
        // Card 12
        Card card14 = new Card(
                "14",
                "Board14",
                "User14",
                "Nghe tiếng anh",
                1,
                "Mô tả công việc 1",
                "2024-01-20",
                null, // Assume to_do_list is null for simplicity
                null, // Assume attached_file_list is null for simplicity
                "2024-01-13",
                false,
                true,
                "In Progress"
        );
        myCardList.add(card14);

    }




    private void setMonthView()
    {
        tv_MonthYear.setText(monthYearFromDate(selectedDate));
        daysInMonth = daysInMonthArray(selectedDate);
        deadlinesInMonth = deadlinesInMonthArray(selectedDate);
        adapterCalendar = new CalendarAdapter(daysInMonth, deadlinesInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        rv_Calendar.setLayoutManager(layoutManager);
        rv_Calendar.setAdapter(adapterCalendar);

        calculateTaskInMonth();
        updateCharts();

    }

    private ArrayList<String> daysInMonthArray(LocalDate date)
    {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i = 1; i <= 42; i++)
        {
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
            {
                daysInMonthArray.add("");
            }
            else
            {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek));
            }
        }

        if(daysInMonthArray.get(6)=="")
        {
            for (int i=0;i<7;i++)
                daysInMonthArray.remove(0);
        }
        return  daysInMonthArray;
    }

    private ArrayList<String> deadlinesInMonthArray(LocalDate date)
    {

        ArrayList<String> deadlinesInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int month = date.getMonthValue();
        int year = date.getYear();
        // Duyệt mảng deadlines để lọc các ngày thuộc tháng và năm được chọn
        for (LocalDate deadline : deadlines) {
            if (deadline.getMonthValue() == month && deadline.getYear() == year) {
                deadlinesInMonthArray.add(String.valueOf(deadline.getDayOfMonth()));
            }
        }

        return deadlinesInMonthArray;


    }




    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public void previousMonthAction(View view)
    {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view)
    {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(View view, int position, String dayText)
    {
//        String s="Danh sách các deadline vào ngày này:\n";
//        int flag=0;
//        String day = dayText + " " + monthYearFromDate(selectedDate);
//        day = convertToFormattedDate(day);
//
//        for (Card card : myCardList) {
//            String deadline = card.getDeadline_at();
//            if(day.contains(deadline))
//            {
//                flag=1;
//                s+=card.getName()+"\n";
//            }
//        }
//        if(flag==0)
//        {
//            s="Không có deadline vào ngày này";
//        }
//        textView.setText(s);

        if(!dayText.equals(""))
        {
            if(dayText.equals(check))
            {
                ll_task.setVisibility(View.GONE);
                check="0";
            }
            else {
                ll_task.setVisibility(View.VISIBLE);
                check=dayText;
            }

        }
        else
        {
            ll_task.setVisibility(View.GONE);
            check=dayText;
        }



        if (dayText!="")
        {
            List<String> list = new ArrayList<>();
            int flag = 0;
            String day = dayText + " " + monthYearFromDate(selectedDate);
            day = convertToFormattedDate(day);

            for (Card card : myCardList) {
                if(!card.getDeadline_at().equals(""))
                {
                    String deadline = card.getDeadline_at();
                    if (day.contains(deadline)) {
                        flag = 1;
                        list.add(card.getName());
                    }
                }

            }
            if (flag == 0) {
                list.add("Không có deadline vào ngày này") ;
            }

            // Hiển thị danh sách trong ListView
            updateListView(list);
        }
        else
            ll_task.setVisibility(View.GONE);




    }
    private void updateListView(List<String> contentList) {
        // Xóa dữ liệu cũ
        adapter.clear();
        // Thêm dữ liệu mới
        adapter.addAll(contentList);
        // Cập nhật ListView
        adapter.notifyDataSetChanged();
    }
    // Hàm để chuyển đổi định dạng ngày của các card
    private void getFormattedCardDates() {

        for (Card card : myCardList) {
            if(!card.getDeadline_at().equals(""))
            {
                String[] parts = card.getDeadline_at().split(" ");
                card.setDeadline_at(formatDate(parts[0]));
            }

        }
        for (Card card : myCardList) {
            String[] parts = card.getCreated_at().split(" ");
            card.setCreated_at(formatDate(parts[0]));
        }
    }
    private String formatDate(String inputDate) {
        // Chuyển định dạng từ "yyyy-MM-dd" sang định dạng "dd/MM/yyyy"
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate date = LocalDate.parse(inputDate, inputFormatter);
        return date.format(outputFormatter);
    }
    public static String convertToFormattedDate(String inputDate) {
        // Định dạng ngày vào
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault());

        // Định dạng ngày ra
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Parse ngày từ chuỗi đầu vào
        LocalDate date = LocalDate.parse(inputDate, inputFormatter);

        // Format ngày theo định dạng mong muốn
        return date.format(outputFormatter);
    }
    private void onClickBoardItemGoToStatusList(Board board){
        Intent intent = new Intent(getContext(), StatusListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_board", board);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void updateCharts() {


        // Get values from EditTexts
        String overDueJobsStr = overdue+"";
        String completedJobsStr = completion+"";
        String unscheduledJobStr = unschedule+"";
        String inProcessJobStr = inprocess+"";

        if (!overDueJobsStr.isEmpty() && !completedJobsStr.isEmpty() && !unscheduledJobStr.isEmpty() && !inProcessJobStr.isEmpty()) {
            float overDueJobs = Float.parseFloat(overDueJobsStr);
            float completedJobs = Float.parseFloat(completedJobsStr);
            float unscheduledJobs = Float.parseFloat(unscheduledJobStr);
            float inProcessJobs = Float.parseFloat(inProcessJobStr);

            float total = overDueJobs + completedJobs + unscheduledJobs + inProcessJobs;

            // Calculate percentage completion
            float percentage_completion = (completedJobs / total) * 100;
            float percentage_unScheduled = (unscheduledJobs / total) * 100;
            float percentage_inProcess = (inProcessJobs / total) * 100;
            float percentage_overDue = (overDueJobs / total) * 100;
            // Set up PieChart
            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            pieEntries.add(new PieEntry(percentage_completion, "Đã hoàn thành"));
            pieEntries.add(new PieEntry(percentage_unScheduled, "Chưa đặt lịch"));
            pieEntries.add(new PieEntry(percentage_inProcess, "Đang thực hiện"));
            pieEntries.add(new PieEntry(percentage_overDue, "Trễ hạn"));

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
            //pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

            int unscheduledColor = ContextCompat.getColor(getContext(), R.color.unscheduled);
            int inProcessColor = ContextCompat.getColor(getContext(), R.color.in_process);
            int completedColor = ContextCompat.getColor(getContext(), R.color.completed);
            int overdueColor = ContextCompat.getColor(getContext(), R.color.overdue);

            int[] customColors = new int[]{completedColor, unscheduledColor, inProcessColor, overdueColor};
            pieDataSet.setColors(customColors);
            pieDataSet.setColors(customColors);
            pieDataSet.setColors(customColors);
            pieDataSet.setValueTextSize(18f); // Set textSize for the values in the PieChart
            pieDataSet.setValueTextColor(Color.WHITE);

            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);
            pieChart.getDescription().setEnabled(false);
            pieChart.invalidate();

            // Hiển thị tổng công việc giữa PieChart
            pieChart.setCenterText("Tổng công việc\n" + String.format("%.0f", total));
            pieChart.setCenterTextSize(14f);
            pieChart.setCenterTextColor(Color.BLACK);

            // Thay đổi text color
            Legend legend = pieChart.getLegend();
            legend.setTextColor(Color.BLACK);

        }
    }

    void calculateTaskInMonth() {
        overdue=0;
        inprocess=0;
        completion=0;
        unschedule=0;
        String day = "1" + " " + monthYearFromDate(selectedDate);
        day = convertToFormattedDate(day);
        if (myCardList.size() != 0) {
            for (Card card : myCardList) {
                if (compareDay(day, card.getCreated_at())) {

                    if(card.getStatus().equals("Overdue"))
                        overdue++;
                    else if(card.getStatus().equals("In process"))
                        inprocess++;
                    else if(card.getStatus().equals("Completed"))
                        completion++;
                    else
                        unschedule++;


                }
            }
        }

        }




    public boolean compareDay(String date1, String date2) {
        // Định dạng ngày vào
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Parse ngày từ chuỗi đầu vào
        LocalDate localDate1 = LocalDate.parse(date1, formatter);
        LocalDate localDate2 = LocalDate.parse(date2, formatter);

        // So sánh tháng và năm
        return localDate1.getMonthValue() == localDate2.getMonthValue() &&
                localDate1.getYear() == localDate2.getYear();
    }




}






