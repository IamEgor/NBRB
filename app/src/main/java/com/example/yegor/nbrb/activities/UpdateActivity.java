package com.example.yegor.nbrb.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.loaders.UpdateCurrenciesLoader;
import com.example.yegor.nbrb.models.CurrencyModel;
import com.example.yegor.nbrb.storage.AppPrefs;
import com.example.yegor.nbrb.storage.MySQLiteClass;
import com.example.yegor.nbrb.utils.Utils;

import java.util.Calendar;
import java.util.List;

public class UpdateActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<CurrencyModel>> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Utils.need2Update()) {
            System.out.println("Utils.need2Update() - " + Utils.need2Update() );
            Toast.makeText(this, "need2Update", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Main2Activity.class));
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public Loader<List<CurrencyModel>> onCreateLoader(int id, Bundle args) {
        Toast.makeText(this, "Update start", Toast.LENGTH_SHORT).show();
        return new UpdateCurrenciesLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<CurrencyModel>> loader, List<CurrencyModel> data) {

        MySQLiteClass mySQLiteClass = new MySQLiteClass(this);
        mySQLiteClass.addCurrencies(data);

        AppPrefs.setLastUpdate(Calendar.getInstance().getTimeInMillis());

        startActivity(new Intent(this, Main2Activity.class));
        finish();
    }

    @Override
    public void onLoaderReset(Loader<List<CurrencyModel>> loader) {

    }

}
