package com.example.yegor.nbrb.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yegor.materialdaterangepicker.date.DatePickerDialog;
import com.example.yegor.nbrb.App;
import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.activities.ChooseCurrencyActivity;
import com.example.yegor.nbrb.activities.FullscreenGraphicActivity;
import com.example.yegor.nbrb.activities.MainActivity;
import com.example.yegor.nbrb.adapters.views.ChartAdapter;
import com.example.yegor.nbrb.exceptions.ExchangeRateAssignsOnceInMonth;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.loaders.AbstractLoader;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.CurrencyModel;
import com.example.yegor.nbrb.models.SpinnerModel;
import com.example.yegor.nbrb.storage.DatabaseManager;
import com.example.yegor.nbrb.utils.ChartUtils;
import com.example.yegor.nbrb.utils.DateUtils;
import com.example.yegor.nbrb.utils.Utils;
import com.example.yegor.nbrb.models.ParcelableLineData;
import com.example.yegor.togglenavigation.ToggleNavigation;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.rey.material.widget.ProgressView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class RatesGraphicFragment extends AbstractRatesFragment<LineData> implements
        ToggleNavigation.OnChoose,
        DatePickerDialog.OnDateSetListener,
        DatePickerDialog.OnTabChanged,
        DialogInterface.OnCancelListener,
        ChartAdapter.OnChartSelect {

    public static final String ACTION = App.getContext().getPackageName();
    public static final String CHART_DATA = "CHART_DATA";
    public static final int REQUEST_CODE_CURRENCY = 1;
    public static final int REQUEST_CODE_FULLSCREEN = 2;


    protected static final String ABBR = CurrencyModel.ABBR;
    protected static final String FROM_DATE = "FROM_DATE";
    protected static final String TO_DATE = "TO_DATE";
    protected static final String SCALE = CurrencyModel.SCALE;
    protected static final String TOGGLE_POS = "TOGGLE_POS";


    protected TextView scale, date, abbr, rate;
    protected LineChart mChart;
    protected AppCompatImageButton fullscreen;
    protected ToggleNavigation toggleNavigation;

    protected ProgressView loadingView;
    protected View errorView;
    protected TextView errorMessage;

    protected DatePickerDialog dpd;
    protected ChartAdapter chartAdapter;
    protected Calendar calendar;
    protected String fromDateStr, toDateStr;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            abbr.setText(intent.getStringExtra(CurrencyModel.ABBR));
            scale.setText(intent.getStringExtra(CurrencyModel.SCALE));
            ((MainActivity) getActivity()).setCurrentItem(2, true);
            restartLoader(LOADER_1);
        }
    };

    public static RatesGraphicFragment newInstance() {
        return new RatesGraphicFragment();
    }

    public RatesGraphicFragment() {

        calendar = Calendar.getInstance();
        toDateStr = DateUtils.format(calendar.getTimeInMillis());
        calendar.add(Calendar.MONTH, -1);
        fromDateStr = DateUtils.format(calendar.getTimeInMillis());
        dpd = DatePickerDialog.newInstance(this, 0, 0, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rates_graphic, container, false);

        //TODO ViewStub

        scale = (TextView) rootView.findViewById(R.id.scale);
        date = (TextView) rootView.findViewById(R.id.date);
        abbr = (TextView) rootView.findViewById(R.id.abbr);
        rate = (TextView) rootView.findViewById(R.id.rate);
        mChart = (LineChart) rootView.findViewById(R.id.line_chart);
        fullscreen = (AppCompatImageButton) rootView.findViewById(R.id.fullscreen);
        toggleNavigation = (ToggleNavigation) rootView.findViewById(R.id.toggle);
        loadingView = (ProgressView) rootView.findViewById(R.id.progress);
        errorView = rootView.findViewById(R.id.error_view);
        errorMessage = (TextView) rootView.findViewById(R.id.error_message);

        rootView.findViewById(R.id.retry_btn).setOnClickListener(v -> restartLoader());
        rootView.findViewById(R.id.container2).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChooseCurrencyActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CURRENCY);
        });
        //TODO может быть NPE
        restartLoader(LOADER_1);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        toggleNavigation.setParams(new ArrayList<ToggleNavigation.ButtonParam>() {{
            add(new ToggleNavigation.ButtonParam("Неделя", false, false));
            add(new ToggleNavigation.ButtonParam("Месяц", true, false));
            add(new ToggleNavigation.ButtonParam("Год", false, false));
            add(new ToggleNavigation.ButtonParam("Период", false, true));
        }});

        mChart.setScaleEnabled(false);
        chartAdapter = new ChartAdapter(mChart, this);
        mChart.setOnChartValueSelectedListener(chartAdapter);


        fullscreen.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FullscreenGraphicActivity.class);
            intent.putExtra(CHART_DATA, new ParcelableLineData(mChart.getLineData()));
            intent.putExtra(ABBR, abbr.getText().toString());
            intent.putExtra(SCALE, scale.getText().toString());
            //TODO не только position, но и интервал для случая "Период"
            intent.putExtra(TOGGLE_POS, toggleNavigation.getActivePosition());
            startActivityForResult(intent, REQUEST_CODE_FULLSCREEN);
        });

        toggleNavigation.setOnChoose(this);
        dpd.setOnCancelListener(this);
        dpd.setOnTabChanged(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            Utils.logT("onActivityResult", "requestCode = " + requestCode);

            if (requestCode == REQUEST_CODE_CURRENCY) {

                SpinnerModel spinnerModel = data.getParcelableExtra(ChooseCurrencyFragment.EXTRA);
                abbr.setText(spinnerModel.getAbbr());
                scale.setText(String.valueOf(spinnerModel.getScale()));

                if (spinnerModel.getDateEnd() != -1) {
                    Toast.makeText(getActivity(), "Обработать случай с dateEnd", Toast.LENGTH_SHORT).show();
                }

                restartLoader(LOADER_1);
            } else if (requestCode == REQUEST_CODE_FULLSCREEN) {

                Bundle arguments = data.getExtras();
                ParcelableLineData models = arguments.getParcelable(CHART_DATA);

                mChart.setScaleEnabled(false);
                chartAdapter.setDates(models.getLineData().getXVals());
                ChartUtils.setUpChart(mChart, models.getLineData(), true);
                abbr.setText(arguments.getString(ABBR));
                scale.setText(arguments.getString(SCALE));
                toggleNavigation.setActivePosition(arguments.getInt(TOGGLE_POS));

                Utils.logT("onActivityResult", "abbr = " + abbr.getText().toString());
                Utils.logT("onActivityResult", "scale = " + scale.getText().toString());
                Utils.logT("onActivityResult", "toggleNavigation ActivePosition= " + toggleNavigation.getActivePosition());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        DatePickerDialog dpd = (DatePickerDialog) getActivity()
                .getFragmentManager()
                .findFragmentByTag(getString(R.string.range_picker_dialog));

        if (dpd != null)
            dpd.setOnDateSetListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, new IntentFilter(ACTION));
    }

    @Override
    public void onDetach() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
        super.onDetach();
    }

    @Override
    public void onToggleChoose(int position) {
        switch (position) {
            case 0://за неделю
                calendar = Calendar.getInstance();
                toDateStr = DateUtils.format(calendar.getTimeInMillis());
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                calendar.add(Calendar.DATE, 1);
                fromDateStr = DateUtils.format(calendar.getTimeInMillis());
                dpd.setMinDate(DateUtils.getCalendar(DateUtils.START_DATE));
                dpd.setMaxDate(DateUtils.getDateTomorrow());
                break;
            case 1://за месяц
                calendar = Calendar.getInstance();
                toDateStr = DateUtils.format(calendar.getTimeInMillis());
                calendar.add(Calendar.MONTH, -1);
                calendar.add(Calendar.DATE, 1);
                fromDateStr = DateUtils.format(calendar.getTimeInMillis());
                dpd.setMinDate(DateUtils.getCalendar(DateUtils.START_DATE));
                dpd.setMaxDate(DateUtils.getDateTomorrow());
                break;
            case 2://за год
                calendar = Calendar.getInstance();
                toDateStr = DateUtils.format(calendar.getTimeInMillis());
                calendar.add(Calendar.DATE, -364);//даже если високосный
                fromDateStr = DateUtils.format(calendar.getTimeInMillis());
                dpd.setMinDate(DateUtils.getCalendar(DateUtils.START_DATE));
                dpd.setMaxDate(DateUtils.getDateTomorrow());
                break;
            case 3://за период
                dpd.show(getActivity().getFragmentManager(), getString(R.string.range_picker_dialog),
                        DateUtils.getCalendar(fromDateStr), DateUtils.getCalendar(toDateStr));
                return;
        }

        restartLoader(LOADER_1);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,
                          int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {

        GregorianCalendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        fromDateStr = DateUtils.format(calendar.getTimeInMillis());
        calendar = new GregorianCalendar(yearEnd, monthOfYearEnd, dayOfMonthEnd);
        toDateStr = DateUtils.format(calendar.getTimeInMillis());

        restartLoader(LOADER_1);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        toggleNavigation.setPreviousActive();
        dpd.setMinDate(DateUtils.getCalendar(DateUtils.START_DATE));
        dpd.setMaxDate(DateUtils.getDateTomorrow());
    }

    @Override
    public void onTabChanged(DatePickerDialog dialog, Calendar calendarStart, Calendar calendarEnd,
                             boolean isStartPage) {

        if (isStartPage) {
            calendar.setTimeInMillis(calendarEnd.getTimeInMillis());
            calendar.add(Calendar.DATE, -1);
            dialog.setMaxDate(calendar);
            dialog.setMinDate(DateUtils.getCalendar(DateUtils.START_DATE));
        } else {
            calendar.setTimeInMillis(calendarStart.getTimeInMillis());
            calendar.add(Calendar.DATE, 1);
            dialog.setMinDate(calendar);
            dialog.setMaxDate(DateUtils.getDateTomorrow());
        }

    }

    @Override
    public void onChartValueSelected(String rate, String date) {

        if (this.date.getVisibility() != View.VISIBLE)
            this.date.setVisibility(View.VISIBLE);

        this.rate.setText(rate);
        this.date.setText(date);
    }

    @Override
    protected Bundle getBundleArgs() {

        Bundle bundle = new Bundle();

        bundle.putString(FROM_DATE, fromDateStr);
        bundle.putString(TO_DATE, toDateStr);
        bundle.putString(ABBR, abbr.getText().toString());

        return bundle;
    }

    @Override
    public Loader<ContentWrapper<LineData>> onCreateLoader(int id, Bundle args) {

        Utils.logT("Loader", "RatesGraphicFragment.onCreateLoader()");

        String abbr = args.getString(ABBR);
        String fromDate = args.getString(FROM_DATE);
        String toDate = args.getString(TO_DATE);

        if (!DatabaseManager.getInstance().isDateValid(abbr, fromDate))
            return new AbstractLoader<>(getContext(), () -> {
                throw new NoDataFoundException();
            });

        if (!DatabaseManager.getInstance().isDateValid(abbr, toDate))
            return new AbstractLoader<>(getContext(), () -> {
                throw new NoDataFoundException();
            });

        setStatus(Status.LOADING);

        return new AbstractLoader<>(getContext(),
                () -> ChartUtils.getChartContent(abbr, fromDate, toDate, id == LOADER_2));
    }

    @Override
    public void onDataReceived(LineData models) {
        Utils.logT("Loader", "RatesGraphicFragment.onDataReceived()");
        chartAdapter.setDates(models.getXVals());
        ChartUtils.setUpChart(mChart, models, false);
        setStatus(Status.OK);
    }

    @Override
    protected void onFailure(Exception e) {
        Utils.logT("Loader", "RatesGraphicFragment.onFailure()");
        if (e instanceof ExchangeRateAssignsOnceInMonth) {
            restartLoader(LOADER_2);
            return;
        } else
            errorMessage.setText(e.getMessage());

        setStatus(Status.FAILED);
    }

    @Override
    public void setStatus(Status status) {

        switch (status) {
            case LOADING:
                ChartUtils.setDisabledColor(mChart);
                mChart.setHighlightPerTapEnabled(false);
                date.setVisibility(View.INVISIBLE);
                loadingView.start();
                errorView.setVisibility(View.GONE);
                rate.setText(R.string.rate_not_selected);
                break;
            case FAILED:
                mChart.setVisibility(View.INVISIBLE);
                errorView.setVisibility(View.VISIBLE);
                loadingView.stop();
                break;
            case OK:
                mChart.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
                loadingView.stop();
                break;

        }
    }


}
