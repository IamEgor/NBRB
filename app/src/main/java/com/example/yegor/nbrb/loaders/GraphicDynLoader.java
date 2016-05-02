package com.example.yegor.nbrb.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.yegor.nbrb.exceptions.NoConnectionException;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.ExRatesDynModel;
import com.example.yegor.nbrb.storage.MySQLiteClass;
import com.example.yegor.nbrb.utils.SoapUtils;
import com.example.yegor.nbrb.utils.Utils;

import java.io.IOException;
import java.util.List;

public class GraphicDynLoader extends AsyncTaskLoader<ContentWrapper<List<ExRatesDynModel>>> {

    private String abbr, fromDate, toDate;
    private Context context;

    public GraphicDynLoader(Context context, String[] params) {
        super(context);

        this.context = context;
        abbr = params[0];
        fromDate = params[1];
        toDate = params[2];
    }

    @Override
    public ContentWrapper<List<ExRatesDynModel>> loadInBackground() {

        if (!Utils.hasConnection()) {
            try {
                Thread.sleep(350);//for beauty
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new ContentWrapper<>(new NoConnectionException());
        }

        String id = (new MySQLiteClass(context)).getIdByAbbr(abbr);

        List<ExRatesDynModel> ratesDyn;

        try {
            ratesDyn = SoapUtils.getRatesDyn(id, fromDate, toDate);
        } catch (IOException e) {
            return new ContentWrapper<>(e);
        }

        if (ratesDyn != null)
            return new ContentWrapper<>(ratesDyn);
        else
            return new ContentWrapper<>(new NoDataFoundException());

    }

}
