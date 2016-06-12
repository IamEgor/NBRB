package com.example.yegor.nbrb.loaders;

import android.os.AsyncTask;

import com.example.yegor.nbrb.App;
import com.example.yegor.nbrb.adapters.views.SpinnerAdapter;
import com.example.yegor.nbrb.storage.MySQLiteClass;

public class AdapterDataAsync extends AsyncTask<Void, Void, SpinnerAdapter> {

    @Override
    protected SpinnerAdapter doInBackground(Void... params) {
        return new SpinnerAdapter(App.getContext(),
                MySQLiteClass.getInstance().getCurrenciesDescription());
    }

}
