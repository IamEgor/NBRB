package com.example.yegor.nbrb.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.adapters.CurrentRatesAdapter;
import com.example.yegor.nbrb.loaders.CurrentRatesLoader;
import com.example.yegor.nbrb.models.DailyExRatesOnDateModel;

import java.util.ArrayList;
import java.util.List;

public class CurrentRatesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<DailyExRatesOnDateModel>> {

    private RecyclerView rv;
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

        adapter = new CurrentRatesAdapter(new ArrayList<DailyExRatesOnDateModel>(0));

        rv = (RecyclerView) rootView.findViewById(R.id.rv);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        getLoaderManager().initLoader(0, null, this).forceLoad();

        return rootView;
    }

    @Override
    public Loader<List<DailyExRatesOnDateModel>> onCreateLoader(int id, Bundle args) {
        return new CurrentRatesLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<DailyExRatesOnDateModel>> loader, List<DailyExRatesOnDateModel> data) {
        adapter.setModels(data);
    }

    @Override
    public void onLoaderReset(Loader<List<DailyExRatesOnDateModel>> loader) {
        adapter.setModels(new ArrayList<DailyExRatesOnDateModel>(0));
    }

}
