package com.example.yegor.nbrb.fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yegor.nbrb.MyMarkerView;
import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.exceptions.NoConnectionException;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.loaders.GraphicDynLoader;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.ExRatesDynModel;
import com.example.yegor.nbrb.storage.MySQLiteClass;
import com.example.yegor.nbrb.utils.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.ksoap2.transport.HttpResponseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RatesGraphicFragment extends Fragment implements
        View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        DatePickerDialog.OnDateSetListener,
        LoaderManager.LoaderCallbacks<ContentWrapper<List<ExRatesDynModel>>> {

    private static final String IS_LEFT_BUTTON = "IS_LEFT_BUTTON";

    public static final String ABBR = "ABBR";
    public static final String FROM_DATE = "FROM_DATE";
    public static final String TO_DATE = "TO_DATE";

    private LineChart mChart;
    private ProgressBar loadingView;
    private View errorView;
    private TextView errorMessage;
    private AppCompatButton retryBtn;

    private AppCompatButton fromDate, toDate;
    private AppCompatSpinner spinner;
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
        retryBtn = (AppCompatButton) rootView.findViewById(R.id.retry_btn);
        fromDate = (AppCompatButton) rootView.findViewById(R.id.from_date);
        toDate = (AppCompatButton) rootView.findViewById(R.id.to_date);
        spinner = (AppCompatSpinner) rootView.findViewById(R.id.spinner);
        fullscreen = (AppCompatImageButton) rootView.findViewById(R.id.fullscreen);

        //TODO вынести обращение из UI
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                (new MySQLiteClass(getContext())).getCurrenciesAbbr());

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);
        spinner.setSelection(dataAdapter.getPosition("USD"));
        spinner.setOnItemSelectedListener(this);

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

        /*
        Bundle bundle = new Bundle();

        bundle.putString(ABBR, spinner.getSelectedItem().toString());
        bundle.putString(FROM_DATE, Utils.format(calendar.getTimeInMillis()));
        fromDate.setTag(calendar.getTimeInMillis());
        calendar.roll(Calendar.MONTH, true);
        bundle.putString(TO_DATE, Utils.format(calendar.getTimeInMillis()));
        toDate.setTag(calendar.getTimeInMillis());

        getActivity().getSupportLoaderManager()
                .initLoader(0, bundle, this)
                .forceLoad();
        */
        refreshChart();

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
                refreshChart();
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        refreshChart();
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

        refreshChart();

    }

    @Override
    public Loader<ContentWrapper<List<ExRatesDynModel>>> onCreateLoader(int id, Bundle args) {

        String[] strings = new String[]{
                args.getString(ABBR),
                args.getString(FROM_DATE),
                args.getString(TO_DATE)};

        System.out.println("onCreateLoader");
        Log.w("onCreateLoader", "load started " + Arrays.asList(strings).toString());

        setStatus(Status.LOADING);

        return new GraphicDynLoader(getContext(), strings);
    }

    @Override
    public void onLoadFinished(Loader<ContentWrapper<List<ExRatesDynModel>>> loader,
                               ContentWrapper<List<ExRatesDynModel>> data) {

        if (data.getException() == null && data.getContent() != null) {

            List<ExRatesDynModel> content = data.getContent();

            System.out.println("onLoadFinished");
            Log.w("onLoadFinished", "load finished " + content.toString());

            float max = 0f, min = content.get(0).getRate();
            float[] floats = new float[content.size()];
            String[] strings = new String[content.size()];


            for (int i = 0; i < content.size(); i++) {
                floats[i] = content.get(i).getRate();
                strings[i] = content.get(i).getDate();
                if (floats[i] > max)
                    max = floats[i];
                if (floats[i] < min)
                    min = floats[i];
            }

            //Kind of normalize
            min = 0.998f * min;
            max = 1.001f * max;

            int count = 10;//floats.length ;
            int iMin = (int) min;
            int iMax = (int) max;
            int division = (iMax - iMin) / count;

            do {
                if ((iMax - iMin) % count == 0)
                    break;
                iMax++;
                if ((iMax - iMin) % count == 0)
                    break;
                iMin--;
            } while (true);

            // create a dataset and give it a type
            List<Entry> yVals = new ArrayList<>();
            for (int i = 0; i < floats.length; i++) {
                yVals.add(new Entry(floats[i], i));
            }

            LineDataSet set1 = new LineDataSet(yVals, null);
            set1.setDrawValues(false);
            set1.setFillAlpha(110);
            set1.setFillColor(Color.RED);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.disableDashedLine();

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.fade_red);
                set1.setFillDrawable(drawable);
            } else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData lineData = new LineData(strings, dataSets);

            //mChart.invalidate();
            // set data
            mChart.setData(lineData);
            mChart.invalidate();

            mChart.setDescription(null);
            mChart.getLegend().setEnabled(false);
            mChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
            mChart.getAxisRight().setEnabled(false);


            MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view, strings);
            mChart.setMarkerView(mv);

            setStatus(Status.OK);

        } else if (data.getException() instanceof NoConnectionException) {
            setStatus(Status.FAILED);
            errorMessage.setText(data.getException().getMessage());
            Toast.makeText(getContext(), "NoConnectionException", Toast.LENGTH_LONG).show();
        } else if (data.getException() instanceof NoDataFoundException) {
            setStatus(Status.FAILED);
            errorMessage.setText(data.getException().getMessage());
            Toast.makeText(getContext(), "NoDataFoundException", Toast.LENGTH_LONG).show();
        } else if (data.getException() instanceof HttpResponseException) {
            setStatus(Status.FAILED);
            errorMessage.setText(data.getException().getMessage());
            Toast.makeText(getContext(), "HttpResponseException", Toast.LENGTH_LONG).show();
        } else
            throw new RuntimeException("Unknown exception");

    }

    @Override
    public void onLoaderReset(Loader<ContentWrapper<List<ExRatesDynModel>>> loader) {
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

    private void refreshChart() {
        getActivity().getSupportLoaderManager()
                .restartLoader(0, getFilledBundle(), this)
                .forceLoad();
    }

    private Bundle getFilledBundle() {

        Bundle bundle = new Bundle();

        bundle.putString(ABBR, spinner.getSelectedItem().toString());
        bundle.putString(FROM_DATE, Utils.format((Long) fromDate.getTag()));
        bundle.putString(TO_DATE, Utils.format((Long) toDate.getTag()));

        return bundle;
    }

    private void setStatus(Status status) {
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


    enum Status {
        LOADING, OK, FAILED
    }

}
