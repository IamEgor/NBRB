package com.example.yegor.calendarview;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.yegor.calendarview.datepicker.SelectedDate;
import com.example.yegor.calendarview.datepicker.SublimeDatePicker;
import com.example.yegor.calendarview.helpers.SublimeListener;
import com.example.yegor.calendarview.helpers.SublimeOptions;
import com.example.yegor.calendarview.utilities.SUtils;

import java.util.Calendar;

public class SublimePicker extends FrameLayout
        implements SublimeDatePicker.OnDateChangedListener,
        SublimeDatePicker.DatePickerValidationCallback {
    private static final String TAG = SublimePicker.class.getSimpleName();

    // Container for 'SublimeDatePicker' & 'SublimeTimePicker'
    private LinearLayout llMainContentHolder;

    // Keeps track which picker is showing
    private SublimeOptions.Picker mCurrentPicker;

    // Date picker
    private SublimeDatePicker mDatePicker;

    // Callback
    private SublimeListener mListener;

    // Client-set options
    private SublimeOptions mOptions;

    // Flags set based on client-set options {SublimeOptions}
    private boolean mDatePickerValid = true,
            mDatePickerSyncStateCalled;

    public SublimePicker(Context context) {
        this(context, null);
    }

    public SublimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.sublimePickerStyle);
    }

    public SublimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(createThemeWrapper(context), attrs, defStyleAttr);
        initializeLayout();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SublimePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(createThemeWrapper(context), attrs, defStyleAttr, defStyleRes);
        initializeLayout();
    }

    public void setMinDate(long minDate) {
        mDatePicker.setMinDate(minDate);
    }

    public void setMaxDate(long maxDate) {
        mDatePicker.setMaxDate(maxDate);
    }

    public void setMaxDateByYegor(long maxDate) {
        mDatePicker.setMaxDateByYegor(maxDate);
    }

    public void setCurrentDate(Calendar calendar) {
        mDatePicker.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    @SuppressWarnings("unused")
    public void setCurrentDate3(Calendar calendar) {
        mDatePicker.updateDateByYegor(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

    }

    private static ContextThemeWrapper createThemeWrapper(Context context) {
        final TypedArray forParent = context.obtainStyledAttributes(
                new int[]{R.attr.sublimePickerStyle});
        int parentStyle = forParent.getResourceId(0, R.style.SublimePickerStyleLight);
        forParent.recycle();

        return new ContextThemeWrapper(context, parentStyle);
    }

    private void initializeLayout() {
        Context context = getContext();
        SUtils.initializeResources(context);

        LayoutInflater.from(context).inflate(R.layout.sublime_picker_view_layout,
                this, true);

        llMainContentHolder = (LinearLayout) findViewById(R.id.llMainContentHolder);

        mDatePicker = (SublimeDatePicker) findViewById(R.id.datePicker);

    }

    public void initializePicker(SublimeOptions options, SublimeListener listener) {

        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null.");
        }

        if (options != null)
            options.verifyValidity();
        else
            options = new SublimeOptions();

        mOptions = options;
        mListener = listener;

        processOptions();
        updateDisplay();
    }

    private void processOptions() {
        if (mOptions.animateLayoutChanges()) {
            // Basic Layout Change Animation(s)
            LayoutTransition layoutTransition = new LayoutTransition();
            if (SUtils.isApi_16_OrHigher()) {
                layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            }
            setLayoutTransition(layoutTransition);
        } else {
            setLayoutTransition(null);
        }

        mDatePicker.init(mOptions.getDateParams(), mOptions.canPickDateRange(), this);
        mDatePicker.setCallback(mListener);

        long[] dateRange = mOptions.getDateRange();

        if (dateRange[0] /* min date */ != Long.MIN_VALUE) {
            mDatePicker.setMinDate(dateRange[0]);
        }

        if (dateRange[1] /* max date */ != Long.MIN_VALUE) {
            mDatePicker.setMaxDate(dateRange[1]);
        }

        mDatePicker.setValidationCallback(this);

        mCurrentPicker = mOptions.getPickerToShow();
    }

    private void updateDisplay() {

        if (mCurrentPicker == SublimeOptions.Picker.DATE_PICKER) {

            mDatePicker.setVisibility(View.VISIBLE);
            llMainContentHolder.setVisibility(View.VISIBLE);

            if (!mDatePickerSyncStateCalled) {
                mDatePickerSyncStateCalled = true;
            }
        }
    }

    @Override
    public void onDatePickerValidationChanged(boolean valid) {
        mDatePickerValid = valid;
    }

    @Override
    public void onDateChanged(SublimeDatePicker view, SelectedDate selectedDate) {
        // TODO: Consider removing this propagation of date change event altogether
        mDatePicker.init(selectedDate, mOptions.canPickDateRange(), this);
        mListener.onDateTimeRecurrenceSet(selectedDate.getFirstDate());
    }

}
