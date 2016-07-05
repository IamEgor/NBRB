package com.example.yegor.nbrb.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.utils.Utils;

import java.util.Set;

public abstract class AbstractRatesFragment<T> extends Fragment implements
        LoaderManager.LoaderCallbacks<ContentWrapper<T>> {

    protected static final int LOADER_1 = 1;
    protected static final int LOADER_2 = 2;

    protected Bundle prevArgs;
    protected int prevLoaderId;

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

    protected void restartLoader(int loaderId) {

        Bundle bundleArgs = getBundleArgs();
        Utils.logT("restartLoader", "!equalsBundles(bundleArgs, prevArgs) = " + !equalsBundles(bundleArgs, prevArgs));
        Utils.logT("restartLoader", "prevLoaderId != loaderId = " + (prevLoaderId != loaderId));

        if (!equalsBundles(bundleArgs, prevArgs) || prevLoaderId != loaderId)
            getLoaderManager()
                    .restartLoader(loaderId, bundleArgs, this)
                    .forceLoad();

        prevLoaderId = loaderId;
        prevArgs = bundleArgs;
    }

    protected void retry() {

        getLoaderManager()
                .restartLoader(LOADER_1, getBundleArgs(), this)
                .forceLoad();
    }

    private static boolean equalsBundles(Bundle current, Bundle previous) {

        if (current == null || previous == null)
            return false;

        Set<String> setCurrent = current.keySet();
        Set<String> setPrev = previous.keySet();

        if (!setCurrent.containsAll(setPrev)) {
            return false;
        }

        Object objCurrent, objPrev;

        for (String key : setCurrent) {

            objCurrent = current.get(key);
            objPrev = previous.get(key);
            if (objCurrent == null && objPrev != null || objCurrent != null && objPrev == null)
                return false;

            if (!objCurrent.equals(objPrev)) {
                return false;
            }
        }

        return true;
    }

    protected abstract Bundle getBundleArgs();

    protected abstract void onDataReceived(T model);

    protected abstract void onFailure(Exception e);

    protected abstract void setStatus(Status stutus);

    protected enum Status {
        LOADING, OK, FAILED, NONE
    }

}
