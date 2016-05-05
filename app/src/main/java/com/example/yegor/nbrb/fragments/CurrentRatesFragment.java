package com.example.yegor.nbrb.fragments;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.adapters.CurrentRatesAdapter;
import com.example.yegor.nbrb.loaders.AbstractLoader;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.DailyExRatesOnDateModel;
import com.example.yegor.nbrb.utils.SoapUtils;

import java.util.ArrayList;
import java.util.List;

public class CurrentRatesFragment extends AbstractRatesFragment<List<DailyExRatesOnDateModel>> {

    private RecyclerView rv;
    private CurrentRatesAdapter adapter;
    private View loadingView;
    private View errorView;
    private TextView errorMessage;

    public CurrentRatesFragment() {
    }

    public static CurrentRatesFragment newInstance() {
        return new CurrentRatesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_rates, container, false);

        adapter = new CurrentRatesAdapter(new ArrayList<>(0));

        rv = (RecyclerView) rootView.findViewById(R.id.rv);
        loadingView = rootView.findViewById(R.id.loading_view);
        errorView = rootView.findViewById(R.id.error_view);
        errorMessage = (TextView) rootView.findViewById(R.id.error_message);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        rootView.findViewById(R.id.retry_btn).setOnClickListener((view) -> restartLoader());

        getLoaderManager().initLoader(0, null, this).forceLoad();

        return rootView;
    }

    @Override
    protected Bundle getBundleArgs() {
        return null;
    }

    @Override
    public Loader<ContentWrapper<List<DailyExRatesOnDateModel>>> onCreateLoader(int id, Bundle args) {
        setStatus(Status.LOADING);
        return new AbstractLoader<>(getContext(), SoapUtils::getCurrenciesNow);
    }

    @Override
    protected void onDataReceived(List<DailyExRatesOnDateModel> models) {
        adapter.setModels(models);
        setStatus(Status.OK);
    }

    @Override
    protected void onFailure(Exception e) {
        errorMessage.setText(e.getMessage());
        setStatus(Status.FAILED);
    }

    @Override
    public void onLoaderReset(Loader<ContentWrapper<List<DailyExRatesOnDateModel>>> loader) {
        adapter.setModels(new ArrayList<>(0));
    }

    protected void setStatus(Status status) {

        switch (status) {
            case LOADING:
                rv.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                loadingView.setVisibility(View.VISIBLE);
                break;
            case OK:
                errorView.setVisibility(View.GONE);
                loadingView.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
                break;
            case FAILED:
                loadingView.setVisibility(View.GONE);
                rv.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                break;
        }

    }

}
