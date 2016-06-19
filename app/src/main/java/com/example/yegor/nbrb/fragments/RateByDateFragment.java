package com.example.yegor.nbrb.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yegor.calendarview.SublimePicker;
import com.example.yegor.calendarview.helpers.SublimeListener;
import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.activities.ChooseCurrencyActivity;
import com.example.yegor.nbrb.exceptions.ExchangeRateAssignsOnceInMonth;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.loaders.AbstractLoader;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.DailyExRatesOnDateModel;
import com.example.yegor.nbrb.models.SpinnerModel;
import com.example.yegor.nbrb.storage.MySQLiteClass;
import com.example.yegor.nbrb.utils.DateUtils;
import com.example.yegor.nbrb.utils.SoapUtils;
import com.example.yegor.nbrb.utils.Utils;

import org.ksoap2.transport.HttpResponseException;

import java.util.Calendar;

public class RateByDateFragment extends AbstractRatesFragment<DailyExRatesOnDateModel>
        implements SublimeListener {

    private static final int REQUEST_CODE = 1;
    private static final String CURRENCY = "CURRENCY";
    private static final String DATE = "DATE";

    private View loadingView;

    private TextView rate, abbr, scale;

    private CoordinatorLayout coordinatorLayout;

    // Picker
    private SublimePicker mSublimePicker;

    private Calendar calendar;

    public RateByDateFragment() {
        calendar = Calendar.getInstance();
    }

    public static RateByDateFragment newInstance() {
        return new RateByDateFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rates_by_date, container, false);

        mSublimePicker = new SublimePicker(getContext());


        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.main_content);
        loadingView = rootView.findViewById(R.id.loading_view);
        rate = (TextView) rootView.findViewById(R.id.rate);
        abbr = (TextView) rootView.findViewById(R.id.abbr);
        scale = (TextView) rootView.findViewById(R.id.scale);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        ((RelativeLayout) rootView.findViewById(R.id.container)).addView(mSublimePicker, params);

        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorPrimaryLight),
                android.graphics.PorterDuff.Mode.SRC_IN);

        rootView.findViewById(R.id.container3).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChooseCurrencyActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mSublimePicker.initializePicker(null, this);
        //mSublimePicker.setSelectedDate(0);
        mSublimePicker.setMaxDate(DateUtils.getDateTomorrow().getTimeInMillis());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            SpinnerModel spinnerModel = data.getParcelableExtra(ChooseCurrencyFragment.EXTRA);
            abbr.setText(spinnerModel.getAbbr());
            scale.setText(String.valueOf(spinnerModel.getScale()));

            if (spinnerModel.getDateEnd() != -1) {
                mSublimePicker.setMaxDate(spinnerModel.getDateEnd());
                Toast.makeText(getActivity(), "добавить date start в spinner model", Toast.LENGTH_SHORT).show();
            }

            restartLoader();
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
            restartLoader(LOADER_1);

    }

    @Override
    public void onDateTimeRecurrenceSet(Calendar selectedDate) {
        calendar = selectedDate;
        restartLoader(LOADER_1);
    }

    @Override
    protected Bundle getBundleArgs() {

        Bundle bundle = new Bundle();
        bundle.putString(CURRENCY, abbr.getText().toString());
        bundle.putString(DATE, DateUtils.format(calendar));

        return bundle;
    }

    @Override
    public Loader<ContentWrapper<DailyExRatesOnDateModel>> onCreateLoader(int id, Bundle args) {

        Utils.logT("Loader", "onFailure");

        String currency = args.getString(CURRENCY);
        String date = args.getString(DATE);

        assert currency != null;
        assert date != null;

        if (!MySQLiteClass.getInstance().isDateValid(currency, date))
            return new AbstractLoader<>(getContext(), () -> {
                throw new NoDataFoundException();
            });

        AbstractLoader<DailyExRatesOnDateModel> loader = null;
        setStatus(Status.LOADING);

        switch (id) {
            case LOADER_1:
                loader = new AbstractLoader<>(getContext(),
                        () -> SoapUtils.getCurrencyDaily(currency, date));
                break;

            case LOADER_2:
                loader = new AbstractLoader<>(getContext(),
                        () -> SoapUtils.getCurrencyMonthly(currency, date));
                break;
        }

        return loader;

    }

    @Override
    protected void onDataReceived(DailyExRatesOnDateModel model) {

        Utils.logT("Loader", "onDataReceived");

        abbr.setText(model.getAbbreviation());
        rate.setText(String.valueOf(model.getRate()));
        scale.setText(String.valueOf(model.getScale()));

        setStatus(Status.OK);
    }

    @Override
    protected void onFailure(Exception e) {

        Utils.logT("Loader", "onFailure");

        Snackbar snackbar;
        if (e instanceof ExchangeRateAssignsOnceInMonth) {
            restartLoader(LOADER_2);
            return;
        } else if (e instanceof HttpResponseException) {
            snackbar = Snackbar
                    .make(coordinatorLayout,
                            R.string.fragment_by_date_wrong_input,
                            Snackbar.LENGTH_LONG);
        } else if (e instanceof NoDataFoundException) {
            snackbar = Snackbar
                    .make(coordinatorLayout,
                            R.string.no_rate_exception,
                            Snackbar.LENGTH_LONG);
        } else
            snackbar = Snackbar
                    .make(coordinatorLayout,
                            e.getMessage(),
                            Snackbar.LENGTH_LONG)
                    .setAction("Retry", view -> restartLoader(LOADER_1));

        snackbar.show();

        setStatus(Status.FAILED);
    }

    @Override
    protected void setStatus(Status status) {
        switch (status) {
            case LOADING:
                loadingView.setVisibility(View.VISIBLE);
                rate.setPaintFlags(rate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                break;
            case OK:
                loadingView.setVisibility(View.GONE);
                rate.setPaintFlags(rate.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                break;
            case FAILED:
                loadingView.setVisibility(View.GONE);
                rate.setPaintFlags(rate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

    }

}
