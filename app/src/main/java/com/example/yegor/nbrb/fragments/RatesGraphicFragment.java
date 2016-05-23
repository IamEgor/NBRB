package com.example.yegor.nbrb.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

public class RatesGraphicFragment extends AbstractRatesFragment<LineData> implements
        View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        DatePickerDialog.OnDateSetListener {

    public static final String ACTION = App.getContext().getPackageName();

    private static final String IS_LEFT_BUTTON = "IS_LEFT_BUTTON";

    public static final String ABBR = "ABBR";
    public static final String FROM_DATE = "FROM_DATE";
    public static final String TO_DATE = "TO_DATE";

    private LineChart mChart;
    private ProgressBar loadingView;
    private View errorView;
    private TextView errorMessage;

    private AppCompatButton fromDate, toDate;
    private SearchableSpinner spinner;
    private AppCompatImageButton fullscreen;

    private Calendar calendar;
    private SpinnerAdapter spinnerAdapter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String abbr = intent.getStringExtra(CurrencyModel.ABBR);
            spinner.setSelection(spinnerAdapter.getPosition(abbr));
            ((MainActivity) getActivity()).setCurrentItem(2, true);

            Toast.makeText(App.getContext(), "onReceive", Toast.LENGTH_SHORT).show();

            //restartLoader();
        }
    };

    public static RatesGraphicFragment newInstance() {
        return new RatesGraphicFragment();
    }

    public RatesGraphicFragment() {
        calendar = Calendar.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rates_graphic, container, false);

        mChart = (LineChart) rootView.findViewById(R.id.line_chart);
        loadingView = (ProgressBar) rootView.findViewById(R.id.progress);
        errorView = rootView.findViewById(R.id.error_view);
        errorMessage = (TextView) rootView.findViewById(R.id.error_message);
        fromDate = (AppCompatButton) rootView.findViewById(R.id.from_date);
        toDate = (AppCompatButton) rootView.findViewById(R.id.to_date);
        spinner = (SearchableSpinner) rootView.findViewById(R.id.pick_currency);
        fullscreen = (AppCompatImageButton) rootView.findViewById(R.id.fullscreen);

        Calendar calendar = Calendar.getInstance();
        toDate.setTag(calendar.getTimeInMillis());
        toDate.setText(String.format(getString(R.string.from_date),
                Utils.format(calendar.getTimeInMillis())));
        calendar.roll(Calendar.MONTH, false);
        fromDate.setTag(calendar.getTimeInMillis());
        fromDate.setText(String.format(getString(R.string.from_date),
                Utils.format(calendar.getTimeInMillis())));

        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
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
            case R.id.from_date:
                showDialog(v);
                break;
            case R.id.to_date:
                showDialog(v);
                break;
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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        if (view.getArguments().getBoolean(IS_LEFT_BUTTON)) {
            fromDate.setText(String.format(getString(R.string.from_date), Utils.format(calendar.getTimeInMillis())));
            fromDate.setTag(calendar.getTimeInMillis());
        } else {
            toDate.setText(String.format(getString(R.string.to_date), Utils.format(calendar.getTimeInMillis())));
            toDate.setTag(calendar.getTimeInMillis());
        }

        restartLoader();

    }

    @Override
    protected Bundle getBundleArgs() {

        Bundle bundle = new Bundle();
        //TODO
        //не тот Id
        Utils.log("(spinner == null) = " + (spinner == null));
        Utils.log("((SpinnerModel) spinner.getSelectedItem()) = " + ((SpinnerModel) spinner.getSelectedItem()));
        Utils.log(" ((SpinnerModel) spinner.getSelectedItem()).getAbbr()) = " + (((SpinnerModel) spinner.getSelectedItem()).getAbbr()));

        bundle.putString(ABBR, ((SpinnerModel) spinner.getSelectedItem()).getAbbr());
        bundle.putString(FROM_DATE, Utils.format((Long) fromDate.getTag()));
        bundle.putString(TO_DATE, Utils.format((Long) toDate.getTag()));

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

    private void showDialog(View view) {

        long time = (long) view.getTag();

        calendar.setTimeInMillis(time);

        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_LEFT_BUTTON, view.getId() == R.id.from_date);

        DatePickerDialog pickerDialog = DatePickerDialog.newInstance(
                RatesGraphicFragment.this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        pickerDialog.setThemeDark(true);
        pickerDialog.setArguments(bundle);
        pickerDialog.show(getActivity().getFragmentManager(), "Datepickerdialog");
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
