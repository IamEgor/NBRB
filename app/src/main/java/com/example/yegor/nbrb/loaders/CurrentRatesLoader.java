package com.example.yegor.nbrb.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.yegor.nbrb.utils.SoapUtils;
import com.example.yegor.nbrb.models.DailyExRatesOnDateModel;

import java.util.List;

public class CurrentRatesLoader extends AsyncTaskLoader<List<DailyExRatesOnDateModel>> {

    public CurrentRatesLoader(Context context) {
        super(context);
    }

    @Override
    public List<DailyExRatesOnDateModel> loadInBackground() {
        return SoapUtils.getCurrenciesNow();
    }

}
