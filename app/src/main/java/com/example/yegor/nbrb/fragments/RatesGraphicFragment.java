package com.example.yegor.nbrb.fragments;

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
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.example.yegor.nbrb.App;
import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.activities.MainActivity;
import com.example.yegor.nbrb.adapters.SpinnerAdapter;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.loaders.AbstractLoader;
import com.example.yegor.nbrb.loaders.AdapterDataAsync;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.CurrencyModel;
import com.example.yegor.nbrb.models.SpinnerModel;
import com.example.yegor.nbrb.storage.MySQLiteClass;
import com.example.yegor.nbrb.utils.ChartUtils;
import com.example.yegor.nbrb.utils.Utils;
import com.example.yegor.nbrb.views.ToggleNavigation;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class RatesGraphicFragment extends AbstractRatesFragment<LineData> implements
        View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        ToggleNavigation.OnChoose,
        DatePickerDialog.OnDateSetListener,
        DialogInterface.OnCancelListener{

    public static final String ACTION = App.getContext().getPackageName();

    public static final String ABBR = "ABBR";
    public static final String FROM_DATE = "FROM_DATE";
    public static final String TO_DATE = "TO_DATE";

    private LineChart mChart;
    private ProgressBar loadingView;
    private View errorView;
    private TextView errorMessage;

    private SearchableSpinner spinner;
    private AppCompatImageButton fullscreen;
    private ToggleNavigation toggleNavigation;

    private Calendar calendar;
    private SpinnerAdapter spinnerAdapter;

    private String fromDateStr, toDateStr;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String abbr = intent.getStringExtra(CurrencyModel.ABBR);
            spinner.setSelection(spinnerAdapter.getPosition(abbr));
            ((MainActivity) getActivity()).setCurrentItem(2, true);

        }
    };

    public static RatesGraphicFragment newInstance() {
        return new RatesGraphicFragment();
    }

    public RatesGraphicFragment() {
        calendar = Calendar.getInstance();
        toDateStr = Utils.format(calendar.getTimeInMillis());
        calendar.add(Calendar.MONTH, -1);
        fromDateStr = Utils.format(calendar.getTimeInMillis());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rates_graphic, container, false);

        mChart = (LineChart) rootView.findViewById(R.id.line_chart);
        loadingView = (ProgressBar) rootView.findViewById(R.id.progress);
        errorView = rootView.findViewById(R.id.error_view);
        errorMessage = (TextView) rootView.findViewById(R.id.error_message);
        spinner = (SearchableSpinner) rootView.findViewById(R.id.pick_currency);
        fullscreen = (AppCompatImageButton) rootView.findViewById(R.id.fullscreen);
        toggleNavigation = (ToggleNavigation) rootView.findViewById(R.id.toggle);

        toggleNavigation.setParams(new ArrayList<ToggleNavigation.ButtonParam>() {{
            add(new ToggleNavigation.ButtonParam("Неделя", false, false));
            add(new ToggleNavigation.ButtonParam("Месяц", true, false));
            add(new ToggleNavigation.ButtonParam("Год", false, false));
            add(new ToggleNavigation.ButtonParam("Период", false, true));
        }});
        toggleNavigation.setOnChoose(this);

        fullscreen.setOnClickListener(this);

        spinner.setOnItemSelectedListener(this);
        spinner.setTitle(getString(R.string.select_currency));
        spinner.setPositiveButton("OK");

        (new InstallAdapter()).execute();

        rootView.findViewById(R.id.retry_btn).setOnClickListener((v -> restartLoader()));

        return rootView;
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
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fullscreen:
                Toast.makeText(getContext(), "Not yet", Toast.LENGTH_SHORT).show();
                break;
            case R.id.retry_btn:
                restartLoader();
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        restartLoader();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void choose(int position) {
        //TODO только при измении позиции
        switch (position) {
            case 0:
                calendar = Calendar.getInstance();
                toDateStr = Utils.format(calendar.getTimeInMillis());
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                fromDateStr = Utils.format(calendar.getTimeInMillis());
                break;
            case 1:
                calendar = Calendar.getInstance();
                toDateStr = Utils.format(calendar.getTimeInMillis());
                calendar.add(Calendar.MONTH, -1);
                fromDateStr = Utils.format(calendar.getTimeInMillis());
                break;
            case 2:
                calendar = Calendar.getInstance();
                toDateStr = Utils.format(calendar.getTimeInMillis());
                calendar.add(Calendar.YEAR, -1);
                fromDateStr = Utils.format(calendar.getTimeInMillis());
                break;
            case 3:
                Calendar start = Utils.getCalendar(fromDateStr);
                Calendar end = Utils.getCalendar(toDateStr);

                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        this,
                        start.get(Calendar.YEAR),
                        start.get(Calendar.MONTH),
                        start.get(Calendar.DAY_OF_MONTH),
                        end.get(Calendar.YEAR),
                        end.get(Calendar.MONTH),
                        end.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setOnCancelListener(this);

                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

                return;
        }

        restartLoader();

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,
                          int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {

        GregorianCalendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        fromDateStr = Utils.format(calendar.getTimeInMillis());
        calendar = new GregorianCalendar(yearEnd, monthOfYearEnd, dayOfMonthEnd);
        toDateStr = Utils.format(calendar.getTimeInMillis());

        restartLoader();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        toggleNavigation.setPreviousActive();
    }

    @Override
    protected Bundle getBundleArgs() {

        Bundle bundle = new Bundle();

        bundle.putString(FROM_DATE, fromDateStr);
        bundle.putString(TO_DATE, toDateStr);
        bundle.putString(ABBR, ((SpinnerModel) spinner.getSelectedItem()).getAbbr());

        return bundle;
    }

    @Override
    public Loader<ContentWrapper<LineData>> onCreateLoader(int id, Bundle args) {

        String abbr = args.getString(ABBR);
        String fromDate = args.getString(FROM_DATE);
        String toDate = args.getString(TO_DATE);

        if (!MySQLiteClass.getInstance().isDateValid(abbr, fromDate))
            return new AbstractLoader<>(getContext(), () -> {
                throw new NoDataFoundException();
            });

        if (!MySQLiteClass.getInstance().isDateValid(abbr, toDate))
            return new AbstractLoader<>(getContext(), () -> {
                throw new NoDataFoundException();
            });

        setStatus(Status.LOADING);

        return new AbstractLoader<>(getContext(), () -> {
            // TODO: при изменинии curId валюты отбражать график или нет?
            return ChartUtils.getChartContent(
                    abbr, fromDate, toDate);
        });
    }

    @Override
    protected void onDataReceived(LineData models) {
        ChartUtils.setUpChart(mChart, models);
        setStatus(Status.OK);
    }

    @Override
    protected void onFailure(Exception e) {
        errorMessage.setText(e.getMessage());
        setStatus(Status.FAILED);
    }

    @Override
    protected void setStatus(Status status) {
        switch (status) {
            case LOADING:
                ChartUtils.setDisabledColor(mChart);
                loadingView.setVisibility(View.VISIBLE);
                break;
            case OK:
                mChart.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.INVISIBLE);
                errorView.setVisibility(View.GONE);
                break;
            case FAILED:
                mChart.setVisibility(View.INVISIBLE);
                loadingView.setVisibility(View.INVISIBLE);
                errorView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private class InstallAdapter extends AdapterDataAsync {

        @Override
        protected void onPostExecute(SpinnerAdapter adapter) {
            spinnerAdapter = adapter;
            spinner.setAdapter(spinnerAdapter);
            spinner.setSelection(spinnerAdapter.getPosition(new SpinnerModel("USD", "Доллар США", -1)));
        }

    }

}
