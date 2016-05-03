package com.example.yegor.nbrb.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.exceptions.NoConnectionException;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.loaders.AbstractLoader;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.CurrencyModel;
import com.example.yegor.nbrb.storage.AppPrefs;
import com.example.yegor.nbrb.storage.MySQLiteClass;
import com.example.yegor.nbrb.utils.SoapUtils;
import com.example.yegor.nbrb.utils.Utils;

import org.ksoap2.transport.HttpResponseException;

import java.util.Calendar;
import java.util.List;

public class UpdateActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ContentWrapper<List<CurrencyModel>>> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Utils.need2Update()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public Loader<ContentWrapper<List<CurrencyModel>>> onCreateLoader(int id, Bundle args) {
        return new AbstractLoader<>(this, SoapUtils::getCurrenciesList);
    }

    @Override
    public void onLoadFinished(Loader<ContentWrapper<List<CurrencyModel>>> loader,
                               ContentWrapper<List<CurrencyModel>> data) {

        if (data.getException() == null && data.getContent() != null) {

            MySQLiteClass mySQLiteClass = new MySQLiteClass(this);
            mySQLiteClass.addCurrencies(data.getContent());

            AppPrefs.setLastUpdate(Calendar.getInstance().getTimeInMillis());

            startActivity(new Intent(this, MainActivity.class));
            finish();

        } else if (data.getException() instanceof NoConnectionException) {
            Toast.makeText(this, "NoConnectionException", Toast.LENGTH_LONG).show();
        } else if (data.getException() instanceof NoDataFoundException) {
            Toast.makeText(this, "NoDataFoundException", Toast.LENGTH_LONG).show();
        } else if (data.getException() instanceof HttpResponseException) {
            Toast.makeText(this, "HttpResponseException", Toast.LENGTH_LONG).show();
        } else
            throw new RuntimeException("Unknown exception");

    }

    @Override
    public void onLoaderReset(Loader<ContentWrapper<List<CurrencyModel>>> loader) {

    }

}
