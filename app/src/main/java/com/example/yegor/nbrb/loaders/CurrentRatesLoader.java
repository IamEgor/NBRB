package com.example.yegor.nbrb.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.yegor.nbrb.exceptions.NoConnectionException;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.DailyExRatesOnDateModel;
import com.example.yegor.nbrb.utils.SoapUtils;
import com.example.yegor.nbrb.utils.Utils;

import java.io.IOException;
import java.util.List;

public class CurrentRatesLoader extends AsyncTaskLoader<ContentWrapper<List<DailyExRatesOnDateModel>>> {

    public CurrentRatesLoader(Context context) {
        super(context);
    }

    @Override
    public ContentWrapper<List<DailyExRatesOnDateModel>> loadInBackground() {

        if (!Utils.hasConnection()) {
            try {
                Thread.sleep(350);//for beauty
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new ContentWrapper<>(new NoConnectionException());
        }

        List<DailyExRatesOnDateModel> currenciesNow;
        try {
            currenciesNow = SoapUtils.getCurrenciesNow();
        } catch (IOException e) {
            return new ContentWrapper<>(e);
        }

        if (currenciesNow != null)
            return new ContentWrapper<>(currenciesNow);
        else
            return new ContentWrapper<>(new NoDataFoundException());

    }

}
