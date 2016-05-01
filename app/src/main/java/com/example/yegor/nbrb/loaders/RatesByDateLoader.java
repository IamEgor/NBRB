package com.example.yegor.nbrb.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.yegor.nbrb.exceptions.NoConnectionException;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.DailyExRatesOnDateModel;
import com.example.yegor.nbrb.utils.SoapUtils;
import com.example.yegor.nbrb.utils.Utils;

public class RatesByDateLoader extends AsyncTaskLoader<ContentWrapper<DailyExRatesOnDateModel>> {

    private String currency;
    private String date;

    public RatesByDateLoader(Context context, String currency, String date) {
        super(context);

        this.currency = currency;
        this.date = date;
    }

    @Override
    public ContentWrapper<DailyExRatesOnDateModel> loadInBackground() {

        if (!Utils.hasConnection()){
            try {
                Thread.sleep(350);//for beauty
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new ContentWrapper<>(new NoConnectionException());
        }

        DailyExRatesOnDateModel rate = SoapUtils.getCurrencyByDate(currency, date);

        if (rate != null)
            return new ContentWrapper<>(rate);
        else
            return new ContentWrapper<>(new NoDataFoundException());

    }


}
