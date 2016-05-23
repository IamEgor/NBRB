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
import com.example.yegor.nbrb.exceptions.ExchangeRateAssignsOnceInMonth;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.loaders.AbstractLoader;
import com.example.yegor.nbrb.loaders.AdapterDataAsync;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.DailyExRatesOnDateModel;
import com.example.yegor.nbrb.models.SpinnerModel;
import com.example.yegor.nbrb.storage.MySQLiteClass;
import com.example.yegor.nbrb.utils.SoapUtils;
import com.example.yegor.nbrb.utils.Utils;
import com.example.yegor.nbrb.views.Validator;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.ksoap2.transport.HttpResponseException;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class RateByDateFragment extends AbstractRatesFragment<DailyExRatesOnDateModel> implements
        DatePickerDialog.OnDateSetListener {

    private static final int LOADER_1 = 1;
    private static final int LOADER_2 = 2;

    private static final String CURRENCY = "CURRENCY";
    private static final String DATE = "DATE";

    private SearchableSpinner spinner;
    private EditText editText;
    private TextInputLayout inputLayout;

    private View cv, loadingView;
    private TextView errorMessage;

    private TextView abbr, name, scale, rate;

    private Calendar calendar;
    private Validator validator;

    public RateByDateFragment() {
        calendar = Calendar.getInstance();
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

        rate = (TextView) rootView.findViewById(R.id.rate);
        scale = (TextView) rootView.findViewById(R.id.scale);
        name = (TextView) rootView.findViewById(R.id.name);
        abbr = (TextView) rootView.findViewById(R.id.abbr);

        spinner.setTitle(getString(R.string.select_currency));
        spinner.setPositiveButton("OK");

        validator = new Validator(editText);
        editText.setText(Utils.format(calendar.getTimeInMillis()));

        rootView.findViewById(R.id.pick_date).setOnClickListener((view) -> {

                    if (validator.getResult() == Validator.VALID)
                        Utils.setCalendar(calendar, editText.getText().toString());

                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            RateByDateFragment.this,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.setThemeDark(true);
                    dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");

                }
        );

        rootView.findViewById(R.id.find).setOnClickListener(v -> {

            int result = validator.getResult();

            switch (result) {
                case Validator.VALID:
                    restartLoader(LOADER_1);
                    inputLayout.setError(null);
                    Utils.hideKeyboard(getActivity());
                    break;
                case Validator.INVALID_FORMAT:
                case Validator.TOO_EARLY_YET:
                case Validator.TOO_OLD_DATE:
                    inputLayout.setError(Validator.getMessage(result));
            }

        });

        (new InstallAdapter()).execute();

        return rootView;

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        inputLayout.setError(null);
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

        name.setText(model.getQuotName());
        abbr.setText(model.getAbbreviation());
        rate.setText(String.valueOf(model.getRate()));
        scale.setText(String.valueOf(model.getScale()));

        setStatus(Status.OK);
    }

    @Override
    protected void onFailure(Exception e) {
        if (e instanceof ExchangeRateAssignsOnceInMonth) {
            restartLoader(LOADER_2);
            return;
        } else if (e instanceof HttpResponseException)
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
            case NONE:
                cv.setVisibility(View.GONE);
                loadingView.setVisibility(View.GONE);
                errorMessage.setVisibility(View.GONE);
        }
    }

    private class InstallAdapter extends AdapterDataAsync {
        @Override
        protected void onPostExecute(SpinnerAdapter adapter) {
            spinner.setAdapter(adapter);
            restartLoader(LOADER_1);
        }
    }

}
