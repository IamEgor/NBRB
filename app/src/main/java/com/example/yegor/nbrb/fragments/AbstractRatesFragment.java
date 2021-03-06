package com.example.yegor.nbrb.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.example.yegor.nbrb.exceptions.NoConnectionException;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.utils.Utils;

import java.io.IOException;

public abstract class AbstractRatesFragment<T> extends Fragment implements
        LoaderManager.LoaderCallbacks<ContentWrapper<T>> {

    public AbstractRatesFragment() {
    }

    @Override
    public void onLoadFinished(Loader<ContentWrapper<T>> loader, ContentWrapper<T> data) {

        Utils.log(data);

        if (data.getException() == null && data.getContent() != null) {
            onDataReceived(data.getContent());
        } else if (data.getException() != null) {

            Exception e = data.getException();

            if (e instanceof NoConnectionException || e instanceof IOException)
                onFailure(e);

        } else
            throw new RuntimeException("Unknown exception " + data.getException().getMessage());

    }

    @Override
    public void onLoaderReset(Loader<ContentWrapper<T>> loader) {
    }

    protected void restartLoader() {
        restartLoader(0);
    }

    protected void restartLoader(int loaderId) {
        /*
        Bundle bundle  =getBundleArgs();
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            Utils.logT("[restartLoader]", String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }
        */
        getActivity().getSupportLoaderManager()
                .restartLoader(loaderId, getBundleArgs(), this)
                .forceLoad();
    }

    protected abstract Bundle getBundleArgs();

    protected abstract void onDataReceived(T model);

    protected abstract void onFailure(Exception e);

    protected abstract void setStatus(Status stutus);

    protected enum Status {
        LOADING, OK, FAILED, NONE
    }

}
