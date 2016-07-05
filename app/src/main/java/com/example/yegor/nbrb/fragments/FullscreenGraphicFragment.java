package com.example.yegor.nbrb.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.activities.ChooseCurrencyActivity;
import com.example.yegor.nbrb.models.ParcelableLineData;
import com.example.yegor.nbrb.utils.ChartUtils;
import com.example.yegor.nbrb.utils.DateUtils;
import com.example.yegor.togglenavigation.ToggleNavigation;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.rey.material.widget.ProgressView;

public class FullscreenGraphicFragment extends RatesGraphicFragment {

    public static FullscreenGraphicFragment newInstance(Bundle extras) {

        FullscreenGraphicFragment fragment = new FullscreenGraphicFragment();
        fragment.setArguments(extras);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_fullscreen_graphic, container, false);

        mChart = (LineChart) rootView.findViewById(R.id.line_chart);
        toggleNavigation = (ToggleNavigation) rootView.findViewById(R.id.toggle);
        errorView = rootView.findViewById(R.id.error_view);
        errorMessage = (TextView) rootView.findViewById(R.id.error_message);

        View toolbarContent = getActivity().findViewById(R.id.toolbar_content);

        scale = (TextView) toolbarContent.findViewById(R.id.scale);
        date = (TextView) toolbarContent.findViewById(R.id.date);
        abbr = (TextView) toolbarContent.findViewById(R.id.abbr);
        rate = (TextView) toolbarContent.findViewById(R.id.rate);
        loadingView = (ProgressView) toolbarContent.findViewById(R.id.loading_view);
        fullscreen = (AppCompatImageButton) toolbarContent.findViewById(R.id.fullscreen);

        toolbarContent.findViewById(R.id.back_button).setOnClickListener(v1 -> getActivity().finish());
        rootView.findViewById(R.id.retry_btn).setOnClickListener(v -> retry());

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        ParcelableLineData models = arguments.getParcelable(CHART_DATA);

        abbr.setText(arguments.getString(ABBR));
        scale.setText(arguments.getString(SCALE));
        abbr.setText(arguments.getString(ABBR));
        toggleNavigation.setActivePosition(arguments.getInt(TOGGLE_POS));

        LineData lineData = models.getLineData();

        if (lineData == null)
            restartLoader(LOADER_1);
        else {
            mChart.setScaleEnabled(false);
            chartAdapter.setDates(lineData.getXVals());
            ChartUtils.setUpChart(mChart, lineData);
        }

        abbr.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChooseCurrencyActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CURRENCY);
        });

        fullscreen.setOnClickListener(v -> {

            Intent intent = new Intent();

            intent.putExtra(CHART_DATA, new ParcelableLineData(mChart.getLineData()));
            intent.putExtra(ABBR, abbr.getText().toString());
            intent.putExtra(SCALE, scale.getText().toString());
            //TODO не только position, но и интервал для случая "Период"
            intent.putExtra(TOGGLE_POS, toggleNavigation.getActivePosition());

            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        });
    }

    @Override
    public void onChartValueSelected(String rate, String date) {
        super.onChartValueSelected(rate, date);

        this.date.setText(DateUtils.formatWeekdayAndDate(date));
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);

        switch (status) {
            case LOADING:
                rate.setText("");
                break;
        }
    }

}
