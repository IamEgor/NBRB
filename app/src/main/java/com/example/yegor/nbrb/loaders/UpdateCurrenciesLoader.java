package com.example.yegor.nbrb.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.yegor.nbrb.exceptions.NoConnectionException;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.CurrencyModel;
import com.example.yegor.nbrb.utils.SoapUtils;
import com.example.yegor.nbrb.utils.Utils;

import java.io.IOException;
import java.util.List;

public class UpdateCurrenciesLoader extends AsyncTaskLoader<ContentWrapper<List<CurrencyModel>>> {

    public UpdateCurrenciesLoader(Context context) {
        super(context);
    }

    @Override
    public ContentWrapper<List<CurrencyModel>> loadInBackground() {

        if (!Utils.hasConnection()) {
            try {
                Thread.sleep(350);//for beauty
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new ContentWrapper<>(new NoConnectionException());
        }

        List<CurrencyModel> currenciesList;
        try {
            currenciesList = SoapUtils.getCurrenciesList();
        } catch (IOException e) {
            return new ContentWrapper<>(e);
        }

        if (currenciesList != null)
            return new ContentWrapper<>(currenciesList);
        else
            return new ContentWrapper<>(new NoDataFoundException());

    }

}