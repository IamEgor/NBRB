package com.example.yegor.nbrb.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.adapters.SpinnerAdapter;
import com.example.yegor.nbrb.loaders.AbstractLoader;
import com.example.yegor.nbrb.loaders.AdapterDataAsync;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.DailyExRatesOnDateModel;
import com.example.yegor.nbrb.models.SpinnerModel;
import com.example.yegor.nbrb.utils.SoapUtils;
import com.example.yegor.nbrb.utils.Utils;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.ksoap2.transport.HttpResponseException;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class RateByDateFragment extends AbstractRatesFragment<DailyExRatesOnDateModel> implements
        DatePickerDialog.OnDateSetListener {

    public static final String CURRENCY = "CURRENCY";
    public static final String DATE = "DATE";

    private SearchableSpinner spinner;
    private EditText editText;
    private TextInputLayout inputLayout;

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

        spinner = (SearchableSpinner) rootView.findViewById(R.id.pick_currency);
        editText = (EditText) rootView.findViewById(R.id.date);
        inputLayout = (TextInputLayout) rootView.findViewById(R.id.inputLayout);

        cv = rootView.findViewById(R.id.cv);
        loadingView = rootView.findViewById(R.id.loading_view);
        errorMessage = (TextView) rootView.findViewById(R.id.error_message);

        currency = (TextView) rootView.findViewById(R.id.currency);
        rate = (TextView) rootView.findViewById(R.id.rate);

        spinner.setTitle(getString(R.string.select_currency));
        spinner.setPositiveButton("OK");

        rootView.findViewById(R.id.pick_date).setOnClickListener((view) -> {
                    inputLayout.setError(null);
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
        );

        rootView.findViewById(R.id.find).setOnClickListener(v -> {
            if (!editText.getText().toString().matches("\\d{4}-\\d{2}-\\d{2}"))
                inputLayout.setError(String.format("Should match input patterm [%s]",
                        getString(R.string.input_date_pattern)));
            else {
                restartLoader();
                inputLayout.setError(null);
                Utils.hideKeyboard(getActivity());
            }
        });

        (new InstallAdapter()).execute();

        return rootView;

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        editText.setText(Utils.format(new GregorianCalendar(year, monthOfYear, dayOfMonth)));
    }

    @Override
    protected Bundle getBundleArgs() {

        Bundle bundle = new Bundle();
        bundle.putString(CURRENCY, ((SpinnerModel) spinner.getSelectedItem()).getAbbr());
        bundle.putString(DATE, editText.getText().toString());

        return bundle;
    }

    @Override
    public Loader<ContentWrapper<DailyExRatesOnDateModel>> onCreateLoader(int id, Bundle args) {
        setStatus(Status.LOADING);
        return new AbstractLoader<>(getContext(),
                () -> SoapUtils.getCurrencyByDate(args.getString(CURRENCY), args.getString(DATE)));
    }

    @Override
    protected void onDataReceived(DailyExRatesOnDateModel model) {
        currency.setText(model.getAbbreviation());
        rate.setText(String.valueOf(model.getRate()));
        setStatus(Status.OK);
    }

    @Override
    protected void onFailure(Exception e) {
        if (e instanceof HttpResponseException)
            errorMessage.setText("Wrong data input");
        else
            errorMessage.setText(e.getMessage());
        setStatus(Status.FAILED);
    }

    @Override
    protected void setStatus(Status status) {
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

    private class InstallAdapter extends AdapterDataAsync {
        @Override
        protected void onPostExecute(SpinnerAdapter adapter) {
            spinner.setAdapter(adapter);
        }
    }

}
