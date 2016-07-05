package com.example.yegor.nbrb.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
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
import com.example.yegor.nbrb.models.ExRatesOnDateModel;
import com.example.yegor.nbrb.utils.SoapUtils;
import com.example.yegor.nbrb.utils.Utils;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

public class CurrentRatesFragment extends AbstractRatesFragment<List<ExRatesOnDateModel>> {

    private RecyclerView rv;
    private View loadingView;
    private View errorView;
    private TextView errorMessage;

    private CurrentRatesAdapter adapter;

    public CurrentRatesFragment() {
    }

    public static CurrentRatesFragment newInstance() {
        return new CurrentRatesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_current_rates, container, false);

        rv = (RecyclerView) rootView.findViewById(R.id.rv);
        loadingView = rootView.findViewById(R.id.loading_view);
        errorView = rootView.findViewById(R.id.error_view);
        errorMessage = (TextView) rootView.findViewById(R.id.error_message);

        rootView.findViewById(R.id.retry_btn).setOnClickListener((view) -> retry());

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        LinearLayoutManager manager = Utils.isPortrait(getActivity()) ?
                new LinearLayoutManager(getContext()) : new GridLayoutManager(getContext(), 2);

        adapter = new CurrentRatesAdapter(new ArrayList<>(0), manager);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(manager);
        rv.setAdapter(adapter);

        restartLoader(LOADER_1);
    }

    @Override
    protected Bundle getBundleArgs() {
        return null;
    }

    @Override
    public Loader<ContentWrapper<List<ExRatesOnDateModel>>> onCreateLoader(int id, Bundle args) {

        Utils.log("Loader", "CurrentRatesFragment.onCreateLoader()");
        setStatus(Status.LOADING);

        return new AbstractLoader<>(getContext(), SoapUtils::getCurrenciesNow);
    }

    @Override
    protected void onDataReceived(List<ExRatesOnDateModel> models) {

        adapter.setModels(models);
        setStatus(Status.OK);
    }

    @Override
    protected void onFailure(Exception e) {

        if (e instanceof EOFException)
            errorMessage.setText(R.string.could_not_connect_to_server);
        else
            errorMessage.setText(e.getMessage());

        setStatus(Status.FAILED);
    }

    @Override
    public void onLoaderReset(Loader<ContentWrapper<List<ExRatesOnDateModel>>> loader) {
        adapter.setModels(new ArrayList<>(0));
    }

    @Override
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
