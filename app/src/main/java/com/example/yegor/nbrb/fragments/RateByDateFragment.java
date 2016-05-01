package com.example.yegor.nbrb.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.exceptions.NoConnectionException;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.loaders.RatesByDateLoader;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.DailyExRatesOnDateModel;
import com.example.yegor.nbrb.storage.MySQLiteClass;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Locale;

public class RateByDateFragment extends Fragment implements
        DatePickerDialog.OnDateSetListener,
        LoaderManager.LoaderCallbacks<ContentWrapper<DailyExRatesOnDateModel>> {

    public static final String CURRENCY = "CURRENCY";
    public static final String DATE = "DATE";

    private AppCompatSpinner spinner;
    private AppCompatEditText editText;

    private View cv, loadingView;
    private TextView errorMessage;

    private TextView currency, rate;

    public RateByDateFragment() {
    }

    public static RateByDateFragment newInstance() {
        return new RateByDateFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rates_by_date, container, false);

        spinner = (AppCompatSpinner) rootView.findViewById(R.id.pick_currency);
        editText = (AppCompatEditText) rootView.findViewById(R.id.date);

        cv = rootView.findViewById(R.id.cv);
        loadingView = rootView.findViewById(R.id.avloadingIndicatorView);
        errorMessage = (TextView) rootView.findViewById(R.id.error_message);

        currency = (TextView) rootView.findViewById(R.id.currency);
        rate = (TextView) rootView.findViewById(R.id.rate);

        rootView.findViewById(R.id.pick_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        RateByDateFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setThemeDark(true);
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
            }
        });

        rootView.findViewById(R.id.find).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().matches("\\d{4}-\\d{2}-\\d{2}"))
                    editText.setError(String.format("Should match input patterm [%s]",
                            getString(R.string.input_date_pattern)));
                else {

                    Bundle bundle = new Bundle();
                    bundle.putString(CURRENCY, spinner.getSelectedItem().toString());
                    bundle.putString(DATE, editText.getText().toString());

                    getActivity()
                            .getSupportLoaderManager()
                            .restartLoader(0, bundle, RateByDateFragment.this)
                            .forceLoad();
                }
            }
        });

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                (new MySQLiteClass(getContext())).getCurrenciesAbbr());

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        return rootView;

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        editText.setText(String.format(Locale.getDefault(), "%1$04d-%2$02d-%3$02d", year, monthOfYear, dayOfMonth));
    }

    @Override
    public Loader<ContentWrapper<DailyExRatesOnDateModel>> onCreateLoader(int id, Bundle args) {
        setStatus(Status.LOADING);
        return new RatesByDateLoader(getContext(), args.getString(CURRENCY), args.getString(DATE));
    }

    @Override
    public void onLoadFinished(Loader<ContentWrapper<DailyExRatesOnDateModel>> loader, ContentWrapper<DailyExRatesOnDateModel> data) {
        if (data.getException() == null && data.getContent() != null) {
            currency.setText(data.getContent().getAbbreviation());
            rate.setText(String.valueOf(data.getContent().getRate()));
            setStatus(Status.OK);
        } else if (data.getException() instanceof NoConnectionException) {
            errorMessage.setText(data.getException().getMessage());
            setStatus(Status.FAILED);
        } else if (data.getException() instanceof NoDataFoundException) {
            errorMessage.setText("No rate for the given currency on that day");
            setStatus(Status.FAILED);
        } else
            throw new RuntimeException("Unknown exception");

    }

    @Override
    public void onLoaderReset(Loader<ContentWrapper<DailyExRatesOnDateModel>> loader) {

    }

    private void setStatus(Status status) {
        switch (status) {
            case LOADING:
                cv.setVisibility(View.GONE);
                loadingView.setVisibility(View.VISIBLE);
                errorMessage.setVisibility(View.GONE);
                break;
            case OK:
                cv.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.GONE);
                errorMessage.setVisibility(View.GONE);
                break;
            case FAILED:
                cv.setVisibility(View.GONE);
                loadingView.setVisibility(View.GONE);
                errorMessage.setVisibility(View.VISIBLE);
                break;
        }
    }

    enum Status {
        LOADING, OK, FAILED
    }

}
