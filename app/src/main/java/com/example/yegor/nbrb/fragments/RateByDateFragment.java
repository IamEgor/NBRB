package com.example.yegor.nbrb.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.activities.ChooseCurrencyActivity;
import com.example.yegor.nbrb.adapters.views.YearSelectorAdapter;
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
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.ksoap2.transport.HttpResponseException;

import java.util.Calendar;
import java.util.List;

public class RateByDateFragment extends AbstractRatesFragment<DailyExRatesOnDateModel> implements
        OnDateSelectedListener {

    private static final int REQUEST_CODE = 1;
    private static final String CURRENCY = "CURRENCY";
    private static final String DATE = "DATE";

    private View loadingView;

    private TextView rate, abbr, scale;
    private RelativeLayout rateContainer;
    private ProgressBar progress;

    private CoordinatorLayout coordinatorLayout;
    private MaterialCalendarView calendarView;
    private ListView yearsList;
    private TextView dateText, yearsText;

    private YearSelectorAdapter adapter;
    private Calendar calendar;
    private SpinnerModel spinnerModel;

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

        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.main_content);
        loadingView = rootView.findViewById(R.id.loading_view);
        rateContainer = (RelativeLayout) rootView.findViewById(R.id.container13);
        progress = (ProgressBar) rootView.findViewById(R.id.progress);
        rate = (TextView) rootView.findViewById(R.id.rate);
        abbr = (TextView) rootView.findViewById(R.id.abbr);
        scale = (TextView) rootView.findViewById(R.id.scale);
        calendarView = (MaterialCalendarView) rootView.findViewById(R.id.calendar_view);
        yearsList = (ListView) rootView.findViewById(R.id.year_list);
        dateText = (TextView) rootView.findViewById(R.id.date_text);
        yearsText = (TextView) rootView.findViewById(R.id.year_text);


        progress.getIndeterminateDrawable()
                .setColorFilter(Utils.getColor(R.color.colorPrimaryLight), PorterDuff.Mode.MULTIPLY);
        calendarView.setDateSelected(Calendar.getInstance(), true);
        calendarView.state().edit().setMaximumDate(DateUtils.getDateTomorrow()).commit();


        List<String> list = Stream
                .range(1997, Calendar.getInstance().get(Calendar.YEAR) + 1)
                .map(String::valueOf)
                .collect(Collectors.<String>toList());

        adapter = new YearSelectorAdapter(getActivity(), list);
        yearsList.setAdapter(adapter);
        int position = adapter.getPosition(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        yearsList.setSelection(position);
        yearsList.setSelection(position);


        dateText.setOnClickListener((view) -> showCalendar(true));
        yearsText.setOnClickListener((view) -> showCalendar(false));
        rateContainer.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChooseCurrencyActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        });
        yearsList.setOnItemClickListener((parent, view, pos, arg3) -> {
            adapter.setSelection(pos);
            calendar.set(Calendar.YEAR, Integer.parseInt(adapter.getText(pos)));
            yearsText.setText(adapter.getText(pos));
            calendarView.setCurrentDate(calendar);
            //TODO исправить
            calendarView.goToPrevious();
            calendarView.goToPrevious();
        });


        calendarView.setOnDateChangedListener(this);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            spinnerModel = data.getParcelableExtra(ChooseCurrencyFragment.EXTRA);
            abbr.setText(spinnerModel.getAbbr());
            scale.setText(String.valueOf(spinnerModel.getScale()));

            if (spinnerModel.getDateEnd() != -1)
                calendarView.state()
                        .edit()
                        .setMaximumDate(DateUtils.getCalendar(spinnerModel.getDateEnd()))
                        .commit();

            restartLoader(LOADER_1);

        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
            restartLoader(LOADER_1);
        Utils.logT("setUserVisibleHint", "RateByDateFragment.setUserVisibleHint() " + isVisibleToUser);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        calendar = date.getCalendar();
        dateText.setText(DateUtils.formatWeekdayAndDate(date.getCalendar()));
        yearsText.setText(String.valueOf(date.getYear()));
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

        rate.setPaintFlags(rate.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        abbr.setText(model.getAbbreviation());
        rate.setText(String.valueOf(model.getRate()));
        scale.setText(String.valueOf(model.getScale()));

        setStatus(Status.OK);
    }

    @Override
    protected void onFailure(Exception e) {
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
            default:
                loadingView.setVisibility(View.GONE);
        }

    }

    void showCalendar(boolean isCalendar) {

        if (isCalendar) {
            calendarView.setVisibility(View.VISIBLE);
            yearsList.setVisibility(View.INVISIBLE);
        } else {
            calendarView.setVisibility(View.INVISIBLE);
            yearsList.setVisibility(View.VISIBLE);
        }

    }

}
