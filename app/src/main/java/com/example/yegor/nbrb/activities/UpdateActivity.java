package com.example.yegor.nbrb.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.loaders.AbstractLoader;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.models.CurrencyModel;
import com.example.yegor.nbrb.storage.AppPrefs;
import com.example.yegor.nbrb.storage.DatabaseManager;
import com.example.yegor.nbrb.utils.DateUtils;
import com.example.yegor.nbrb.utils.SoapUtils;
import com.github.glomadrian.roadrunner.IndeterminateRoadRunner;

import java.util.Calendar;
import java.util.List;

public class UpdateActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<ContentWrapper<List<CurrencyModel>>> {

    private ViewStub stub;
    private View errorView;
    private TextView errorMessage;
    private IndeterminateRoadRunner loaderView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (!DateUtils.need2Update()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        stub = (ViewStub) findViewById(R.id.stub);
        loaderView = (IndeterminateRoadRunner) findViewById(R.id.loading_view);

        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public Loader<ContentWrapper<List<CurrencyModel>>> onCreateLoader(int id, Bundle args) {

        loaderView.setVisibility(View.VISIBLE);
        //loaderView.start();

        if (errorView != null)
            errorView.setVisibility(View.GONE);

        return new AbstractLoader<>(this, SoapUtils::getCurrenciesList);
    }

    @Override
    public void onLoadFinished(Loader<ContentWrapper<List<CurrencyModel>>> loader,
                               ContentWrapper<List<CurrencyModel>> data) {

        if (data.getException() == null && data.getContent() != null) {
            new UpdateDatabase(data.getContent()).execute();
            return;
        }

        if (errorView == null || errorMessage == null) {
            errorView = stub.inflate();
            errorMessage = (TextView) errorView.findViewById(R.id.error_message);
            errorView.findViewById(R.id.retry_btn).setOnClickListener(
                    v -> {
                        getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
                        loaderView.restart();
                    });
        }

        loaderView.stop();
        loaderView.setVisibility(View.INVISIBLE);

        errorView.setVisibility(View.VISIBLE);
        errorMessage.setText(data.getException().getMessage());
    }

    @Override
    public void onLoaderReset(Loader<ContentWrapper<List<CurrencyModel>>> loader) {

    }

    class UpdateDatabase extends AsyncTask<Void, Void, Void> {

        private List<CurrencyModel> models;

        public UpdateDatabase(List<CurrencyModel> models) {
            this.models = models;
        }

        @Override
        protected Void doInBackground(Void... params) {

            DatabaseManager.getInstance().addCurrenciesBulk(models);
            AppPrefs.setLastUpdate(Calendar.getInstance().getTimeInMillis());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            startActivity(new Intent(UpdateActivity.this, MainActivity.class));
            finish();
        }

    }

}
