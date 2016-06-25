package com.example.yegor.nbrb.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.example.yegor.nbrb.models.ContentWrapper;

import java.util.Set;

public abstract class AbstractRatesFragment<T> extends Fragment implements
        LoaderManager.LoaderCallbacks<ContentWrapper<T>> {

    protected static final int LOADER_1 = 1;
    protected static final int LOADER_2 = 2;

    protected Bundle prevArgs;

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

        Bundle bundleArgs = getBundleArgs();

        if (!equalsBundles(bundleArgs, prevArgs))
            getLoaderManager()
                    .restartLoader(loaderId, bundleArgs, this)
                    .forceLoad();

        prevArgs = bundleArgs;
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
