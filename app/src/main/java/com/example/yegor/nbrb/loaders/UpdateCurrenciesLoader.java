package com.example.yegor.nbrb.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.yegor.nbrb.models.CurrencyModel;
import com.example.yegor.nbrb.utils.SoapUtils;

import java.util.List;

public class UpdateCurrenciesLoader extends AsyncTaskLoader<List<CurrencyModel>> {

    public UpdateCurrenciesLoader(Context context) {
        super(context);
    }

    @Override
    public List<CurrencyModel> loadInBackground() {
        return SoapUtils.getCurrenciesList();
    }

}