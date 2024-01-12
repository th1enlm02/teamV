package com.example.teamv.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teamv.R;
import com.example.teamv.adapter.CalendarAdapter;
import com.example.teamv.object.Card;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener{

    private List<Card> myCardList = new ArrayList<>();
    private TextView tv_MonthYear; //tv để hiện thông tin tháng năm
    private RecyclerView rv_Calendar;

    private LocalDate selectedDate; // ngày hiện tại
    public CalendarAdapter adapterCalendar;
    ArrayList<String> deadlinesInMonth ; // những ngày có deadline trong tháng
    ArrayList<String> daysInMonth;  // những ngày trong tháng
    private ArrayList<LocalDate> deadlines; // mảng chứa thời gian có deadline
    private View view;
    ImageView iv_nextmonth,iv_previousmonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        findViewByIds();
        initDeadlines();
        selectedDate = LocalDate.now();
        setMonthView();
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
    private void readAllMyCard() {

    }
    void initDeadlines() {
        // Tạo mảng các deadline
        deadlines = new ArrayList<>();
        deadlines.add(LocalDate.of(2024, Month.JANUARY, 10));
        deadlines.add(LocalDate.of(2024, Month.JANUARY, 15));
        deadlines.add(LocalDate.of(2024, Month.FEBRUARY, 14));
        deadlines.add(LocalDate.of(2023, Month.JANUARY, 5));
        deadlines.add(LocalDate.of(2024, Month.JANUARY, 11));
        deadlines.add(LocalDate.of(2024, Month.JANUARY, 12));
        deadlines.add(LocalDate.of(2024, Month.FEBRUARY, 18));
        deadlines.add(LocalDate.of(2023, Month.JANUARY, 5));
    }

    private void findViewByIds()
    {
        rv_Calendar = (RecyclerView) view.findViewById(R.id.rv_Calendar);
        tv_MonthYear = (TextView) view.findViewById(R.id.tv_MonthYear);
        iv_nextmonth = (ImageView) view.findViewById(R.id.iv_nextmonth);
        iv_previousmonth=(ImageView) view.findViewById(R.id.iv_previousmonth);
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
        //view.setBackgroundColor(getResources().getColor(R.color.red));

    }
}