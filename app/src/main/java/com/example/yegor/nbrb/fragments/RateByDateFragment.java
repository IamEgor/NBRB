package com.example.yegor.nbrb.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.yegor.calendarview.SublimePicker;
import com.example.yegor.calendarview.helpers.SublimeListener;
import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.activities.ChooseCurrencyActivity;
import com.example.yegor.nbrb.exceptions.ExchangeRateAssignsOnceInMonth;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.loaders.AbstractLoader;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.ExRatesOnDateModel;
import com.example.yegor.nbrb.models.SpinnerModel;
import com.example.yegor.nbrb.storage.DatabaseManager;
import com.example.yegor.nbrb.utils.DateUtils;
import com.example.yegor.nbrb.utils.SoapUtils;
import com.example.yegor.nbrb.utils.Utils;
import com.example.yegor.nbrb.views.ResizeWidthAnimation;
import com.rey.material.widget.ProgressView;

import org.ksoap2.transport.HttpResponseException;

import java.io.EOFException;
import java.util.Calendar;

public class RateByDateFragment extends AbstractRatesFragment<ExRatesOnDateModel>
        implements SublimeListener {

    private static final int REQUEST_CODE = 1;
    private static final String CURRENCY = "CURRENCY";
    private static final String DATE = "DATE";

    private ProgressView loadingView;

    private TextView rate, abbr, scale;
    private View strikethroughLine;

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
        loadingView = (ProgressView) rootView.findViewById(R.id.loading_view);
        rate = (TextView) rootView.findViewById(R.id.rate);
        abbr = (TextView) rootView.findViewById(R.id.abbr);
        scale = (TextView) rootView.findViewById(R.id.scale);
        strikethroughLine = rootView.findViewById(R.id.strikethrough_line);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        ((RelativeLayout) rootView.findViewById(R.id.container)).addView(mSublimePicker, params);

        rootView.findViewById(R.id.container2).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChooseCurrencyActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        mSublimePicker.initializePicker(null, this);
        mSublimePicker.setMaxDate(DateUtils.getDateTomorrow().getTimeInMillis());
        mSublimePicker.setMinDate(DateUtils.START_DATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            SpinnerModel spinnerModel = data.getParcelableExtra(ChooseCurrencyFragment.EXTRA);

            abbr.setText(spinnerModel.getAbbr());
            scale.setText(String.valueOf(spinnerModel.getScale()));

            long tomorrow = DateUtils.getDateTomorrow().getTimeInMillis();
            long date_end = spinnerModel.getDateEnd();

            Utils.logT(RateByDateFragment.class.getName(), "Long tomorrow = " + DateUtils.format(tomorrow) +
                    ", date_end = " + DateUtils.format(date_end));

            if (date_end < tomorrow) {
                calendar.setTimeInMillis(date_end);
                mSublimePicker.setCurrentDate3(DateUtils.getCalendar(date_end));
                mSublimePicker.setMaxDateByYegor(date_end);
            } else {
                calendar.setTimeInMillis(tomorrow);
                mSublimePicker.setCurrentDate3(Calendar.getInstance());
                mSublimePicker.setMaxDateByYegor(tomorrow);
            }

            Utils.logT(RateByDateFragment.class.getName(), "new Max Date " + DateUtils.format(calendar));
            restartLoader(LOADER_1);
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

        Utils.logT("getBundleArgs", "CURRENCY = " + abbr.getText().toString());
        Utils.logT("getBundleArgs", "DATE = " + DateUtils.format(calendar));

        bundle.putString(CURRENCY, abbr.getText().toString());
        bundle.putString(DATE, DateUtils.format(calendar));

        return bundle;
    }

    @Override
    public Loader<ContentWrapper<ExRatesOnDateModel>> onCreateLoader(int id, Bundle args) {

        setStatus(Status.LOADING);

        String currency = args.getString(CURRENCY);
        String date = args.getString(DATE);

        assert currency != null;
        assert date != null;

        if (!DatabaseManager.getInstance().isDateValid(currency, date))
            return new AbstractLoader<>(getContext(), () -> {
                throw new NoDataFoundException();
            });

        AbstractLoader<ExRatesOnDateModel> loader = null;

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
    protected void onDataReceived(ExRatesOnDateModel model) {

        abbr.setText(model.getAbbreviation());
        rate.setText(String.valueOf(model.getRate()));
        scale.setText(String.valueOf(model.getScale()));

        setStatus(Status.OK);
    }

    @Override
    protected void onFailure(Exception e) {

        Snackbar snackbar;

        if (e instanceof ExchangeRateAssignsOnceInMonth) {
            Utils.logT(RateByDateFragment.class.getName(), "instanceof ExchangeRateAssignsOnceInMonth");
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
        } else if (e instanceof EOFException) {
            snackbar = Snackbar
                    .make(coordinatorLayout,
                            R.string.could_not_connect_to_server,
                            Snackbar.LENGTH_LONG);
        } else
            snackbar = Snackbar
                    .make(coordinatorLayout,
                            e.toString(),
                            Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry, view -> retry());

        Utils.logT("onFailure", e.toString() + " " + e.getMessage());
        snackbar.show();

        setStatus(Status.FAILED);
    }

    @Override
    protected void setStatus(Status status) {
        switch (status) {
            case LOADING:
                paintStripe(true);
                loadingView.start();
                break;
            case FAILED:
                paintStripe(false);
                loadingView.stop();
                rate.setText(R.string.rate_not_selected);
                break;
            case OK:
                paintStripe(false);
                loadingView.stop();
                break;
        }
    }

    private void paintStripe(boolean paint) {

        if (getString(R.string.rate_not_selected).equals(rate.getText().toString()))
            return;

        if (paint) {
            ResizeWidthAnimation anim = new ResizeWidthAnimation(strikethroughLine, rate.getWidth());
            anim.setDuration(300);
            rate.startAnimation(anim);
        } else {
            ResizeWidthAnimation anim = new ResizeWidthAnimation(strikethroughLine, 0);
            anim.setDuration(0);
            rate.startAnimation(anim);
        }
    }

}
