package com.example.yegor.nbrb.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.example.yegor.nbrb.models.ContentWrapper;

public abstract class AbstractRatesFragment<T> extends Fragment implements
        LoaderManager.LoaderCallbacks<ContentWrapper<T>> {

    protected static final int LOADER_1 = 1;
    protected static final int LOADER_2 = 2;

    public AbstractRatesFragment() {
    }

    @Override
    public void onLoadFinished(Loader<ContentWrapper<T>> loader, ContentWrapper<T> data) {

        if (data.getException() == null && data.getContent() != null)
            onDataReceived(data.getContent());
        else if (data.getException() != null)
            onFailure(data.getException());
        else
            throw new RuntimeException("Unknown exception " + data.getException().getMessage());

    }

    @Override
    public void onLoaderReset(Loader<ContentWrapper<T>> loader) {
    }

    protected void restartLoader() {
        restartLoader(LOADER_1);
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
