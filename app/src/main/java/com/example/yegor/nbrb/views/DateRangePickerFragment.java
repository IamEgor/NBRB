package com.example.yegor.nbrb.views;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TabHost;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.utils.Utils;

import java.util.Calendar;

/**
 * A DialogFragment that show 2 date pickers.
 * Yesid Lazaro @ingyesid
 */
public class DateRangePickerFragment extends DialogFragment implements View.OnClickListener {

    private OnDateRangeSelectedListener onDateRangeSelectedListener;

    private TabHost tabHost;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private Button butSetDateRange;
    boolean is24HourMode;

    private PickerDate startDate;
    private PickerDate endDate;

    public DateRangePickerFragment() {
        // Required empty public constructor
    }

    public static DateRangePickerFragment newInstance(OnDateRangeSelectedListener callback,
                                                      boolean is24HourMode, PickerDate[] dates) {
        DateRangePickerFragment dateRangePickerFragment = new DateRangePickerFragment();
        dateRangePickerFragment.initialize(callback, is24HourMode, dates);
        return dateRangePickerFragment;
    }

    public void initialize(OnDateRangeSelectedListener callback,
                           boolean is24HourMode, PickerDate[] dates) {
        startDate = dates[0];
        endDate = dates[1];

        onDateRangeSelectedListener = callback;
        this.is24HourMode = is24HourMode;
    }

    public void setStartDate(PickerDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(PickerDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.date_range_picker, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        tabHost = (TabHost) root.findViewById(R.id.tabHost);
        butSetDateRange = (Button) root.findViewById(R.id.but_set_time_range);
        startDatePicker = (DatePicker) root.findViewById(R.id.start_date_picker);
        endDatePicker = (DatePicker) root.findViewById(R.id.end_date_picker);
        butSetDateRange.setOnClickListener(this);
        tabHost.findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec startDatePage = tabHost.newTabSpec("start");
        startDatePage.setContent(R.id.start_date_group);
        startDatePage.setIndicator(getString(R.string.title_tab_start_date));

        TabHost.TabSpec endDatePage = tabHost.newTabSpec("end");
        endDatePage.setContent(R.id.end_date_group);
        endDatePage.setIndicator(getString(R.string.ttile_tab_end_date));

        tabHost.addTab(startDatePage);
        tabHost.addTab(endDatePage);

        Utils.logT("[updateDate]", endDate.toString());
        Utils.logT("[updateDate]", startDate.toString());

        endDatePicker.updateDate(endDate.getYear(), endDate.getMonth(), endDate.getDay());
        startDatePicker.updateDate(startDate.getYear(), startDate.getMonth(), startDate.getDay());

        endDatePicker.invalidate();
        startDatePicker.invalidate();

        endDatePicker.refreshDrawableState();
        startDatePicker.refreshDrawableState();

        return root;

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    public void setOnDateRangeSelectedListener(OnDateRangeSelectedListener callback) {
        this.onDateRangeSelectedListener = callback;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        onDateRangeSelectedListener.onDateRangeSelected(startDatePicker.getDayOfMonth(), startDatePicker.getMonth(), startDatePicker.getYear(),
                endDatePicker.getDayOfMonth(), endDatePicker.getMonth(), endDatePicker.getYear());
    }

    public interface OnDateRangeSelectedListener {
        void onDateRangeSelected(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear);
    }

    public static class PickerDate {

        private int year;
        private int month;
        private int day;

        public PickerDate() {
        }

        public PickerDate(Calendar calendar) {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDay() {
            return day;
        }

        @Override
        public String toString() {
            return "PickerDate{" +
                    "year=" + year +
                    ", month=" + month +
                    ", day=" + day +
                    '}';
        }

    }

}
