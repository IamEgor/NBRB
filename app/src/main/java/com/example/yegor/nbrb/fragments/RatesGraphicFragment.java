package com.example.yegor.nbrb.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.adapters.SpinnerAdapter;
import com.example.yegor.nbrb.loaders.AbstractLoader;
import com.example.yegor.nbrb.loaders.AdapterDataAsync;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.ExRatesDynModel;
import com.example.yegor.nbrb.models.SpinnerModel;
import com.example.yegor.nbrb.storage.MySQLiteClass;
import com.example.yegor.nbrb.utils.ChartUtils;
import com.example.yegor.nbrb.utils.SoapUtils;
import com.example.yegor.nbrb.utils.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.List;

public class RatesGraphicFragment extends AbstractRatesFragment<List<ExRatesDynModel>> implements
        View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        DatePickerDialog.OnDateSetListener {

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

    public RatesGraphicFragment() {
        calendar = Calendar.getInstance();
    }

    public static RatesGraphicFragment newInstance() {
        return new RatesGraphicFragment();
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

        Utils.logT("[Lifecycle]", "onCreateView");

        return rootView;
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
    public Loader<ContentWrapper<List<ExRatesDynModel>>> onCreateLoader(int id, Bundle args) {

        setStatus(Status.LOADING);

        return new AbstractLoader<>(getContext(), () -> {
            String curId = MySQLiteClass.getInstance().getIdByAbbr(args.getString(ABBR));
            return SoapUtils.getRatesDyn(curId, args.getString(FROM_DATE), args.getString(TO_DATE));
        });
    }

    @Override
    protected Bundle getBundleArgs() {

        Bundle bundle = new Bundle();

        Utils.log(spinner);
        Utils.log(spinner.getSelectedItem());

        bundle.putString(ABBR, ((SpinnerModel) spinner.getSelectedItem()).getAbbr());
        bundle.putString(FROM_DATE, Utils.format((Long) fromDate.getTag()));
        bundle.putString(TO_DATE, Utils.format((Long) toDate.getTag()));

        return bundle;
    }

    @Override
    protected void onDataReceived(List<ExRatesDynModel> models) {
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
                //mChart.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.VISIBLE);
                //errorView.setVisibility(View.GONE);
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
            spinner.setAdapter(adapter);
            spinner.setSelection(adapter.getPosition(new SpinnerModel("USD", "Доллар США", "")));
            //restartLoader();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.logT("[Lifecycle]", "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.logT("[Lifecycle]", "onActivityCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Utils.logT("[Lifecycle]", "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Utils.logT("[Lifecycle]", "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.logT("[Lifecycle]", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.logT("[Lifecycle]", "onPause");
    }


    @Override
    public void onStop() {
        super.onStop();
        Utils.logT("[Lifecycle]", "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Utils.logT("[Lifecycle]", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.logT("[Lifecycle]", "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Utils.logT("[Lifecycle]", "onDetach");
    }
}
