package com.example.yegor.nbrb.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.adapters.ChooseCurrencyAdapter;
import com.example.yegor.nbrb.models.SpinnerModel;
import com.example.yegor.nbrb.storage.MySQLiteClass;

import java.util.ArrayList;
import java.util.List;

public class ChooseCurrencyFragment extends Fragment implements
        SearchView.OnQueryTextListener,
        ChooseCurrencyAdapter.OnItemClickListener {

    public static final String EXTRA = ChooseCurrencyFragment.class.getSimpleName();

    private RecyclerView rv;

    private ChooseCurrencyAdapter adapter;

    private List<SpinnerModel> models;

    public static ChooseCurrencyFragment newInstance(Bundle args) {

        ChooseCurrencyFragment fragment = new ChooseCurrencyFragment();

        if (args != null)
            fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rv = new RecyclerView(getActivity());

        return rv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        models = MySQLiteClass.getInstance().getCurrenciesDescription();
        adapter = new ChooseCurrencyAdapter(models, this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_currencies, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<SpinnerModel> filteredModelList = filter(models, query);
        adapter.animateTo(filteredModelList);
        rv.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void onItemClick(SpinnerModel model) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA, model);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private List<SpinnerModel> filter(List<SpinnerModel> models, String query) {

        query = query.toLowerCase();

        final List<SpinnerModel> filteredModelList = new ArrayList<>();
        for (SpinnerModel model : models) {
            final String abbr = model.getAbbr().toLowerCase();
            final String name = model.getName().toLowerCase();
            if (abbr.contains(query) || name.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }


}
