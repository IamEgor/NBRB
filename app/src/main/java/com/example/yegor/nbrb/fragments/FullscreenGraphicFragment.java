package com.example.yegor.nbrb.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.activities.ChooseCurrencyActivity;
import com.example.yegor.nbrb.utils.ChartUtils;
import com.example.yegor.nbrb.views.ParcelableLineData;
import com.example.yegor.togglenavigation.ToggleNavigation;
import com.github.mikephil.charting.charts.LineChart;

public class FullscreenGraphicFragment extends RatesGraphicFragment {

    protected View loadingView;

    public static FullscreenGraphicFragment newInstance(Bundle extras) {

        FullscreenGraphicFragment fragment = new FullscreenGraphicFragment();
        fragment.setArguments(extras);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_fullscreen_graphic, container, false);

        mChart = (LineChart) rootView.findViewById(R.id.line_chart);
        toggleNavigation = (ToggleNavigation) rootView.findViewById(R.id.toggle);
        loadingView = rootView.findViewById(R.id.loading_view);
        errorView = rootView.findViewById(R.id.error_view);
        errorMessage = (TextView) rootView.findViewById(R.id.error_message);

        View toolbarContent = getActivity().findViewById(R.id.toolbar_content);

        scale = (TextView) toolbarContent.findViewById(R.id.scale);
        date = (TextView) toolbarContent.findViewById(R.id.date);
        abbr = (TextView) toolbarContent.findViewById(R.id.abbr);
        rate = (TextView) toolbarContent.findViewById(R.id.rate);
        fullscreen = (AppCompatImageButton) toolbarContent.findViewById(R.id.fullscreen);

        rootView.findViewById(R.id.retry_btn).setOnClickListener(v -> restartLoader());

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        ParcelableLineData models = arguments.getParcelable(CHART_DATA);

        chartAdapter.setDates(models.getLineData().getXVals());
        ChartUtils.setUpChart(mChart, models.getLineData(), true);
        abbr.setText(arguments.getString(ABBR));
        scale.setText(arguments.getString(SCALE));
        abbr.setText(arguments.getString(ABBR));
        toggleNavigation.setActiveStateless(arguments.getInt(TOGGLE_POS));


        abbr.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChooseCurrencyActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        });

        fullscreen.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "intent.putExtra(CHART_DATA)", Toast.LENGTH_SHORT).show();
            /*
            Intent intent = new Intent();
            intent.putExtra(CHART_DATA, model);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
            */
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(getActivity(), "Back from fragment", Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setStatus(Status status) {

        switch (status) {
            case LOADING:
                ChartUtils.setDisabledColor(mChart);
                mChart.setHighlightPerTapEnabled(false);
                date.setVisibility(View.INVISIBLE);
                loadingView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                //TODO
                //rate.setText(R.string.rate_not_selected);
                break;
            case FAILED:
                mChart.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.GONE);
                break;
            case OK:
                mChart.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
                loadingView.setVisibility(View.GONE);
                break;

        }
    }

}
