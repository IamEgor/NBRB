package com.example.yegor.nbrb.loaders;

import android.content.Context;
import android.os.SystemClock;
import android.support.v4.content.AsyncTaskLoader;

import com.example.yegor.nbrb.exceptions.NoConnectionException;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.utils.Utils;

import java.io.IOException;

public class AbstractLoader<T> extends AsyncTaskLoader<ContentWrapper<T>> {

    private static final int MIN_ANIMATION_DURATION = 1000;

    private ContentWrapper<T> data;
    private AbstractLoaderInterface<T> action;

    public AbstractLoader(Context context, AbstractLoaderInterface<T> action) {
        super(context);

        this.action = action;
        Utils.log("MIN_ANIMATION_DURATION = " + MIN_ANIMATION_DURATION);
    }

    @Override
    public ContentWrapper<T> loadInBackground() {

        long executionTime = System.currentTimeMillis();
        ContentWrapper<T> content = receiveContent();
        executionTime = System.currentTimeMillis() - executionTime;

        if (executionTime < MIN_ANIMATION_DURATION)
            SystemClock.sleep(MIN_ANIMATION_DURATION - executionTime);

        return content;
    }

    private ContentWrapper<T> receiveContent() {

        if (!Utils.hasConnection())
            return new ContentWrapper<>(new NoConnectionException());

        T data;

        try {
            data = action.action();
        } catch (IOException e) {
            return new ContentWrapper<>(e);
        }

        if (data != null)
            return new ContentWrapper<>(data);
        else
            return new ContentWrapper<>(new NoDataFoundException());
    }

    @Override
    public void deliverResult(ContentWrapper<T> data) {

        if (isReset()) {
            if (data != null)
                releaseResources(data);
        }

        ContentWrapper<T> oldData = this.data;
        this.data = data;

        if (isStarted())
            super.deliverResult(data);

        if (oldData != null)//&& oldData != data)
            releaseResources(oldData);
    }

    @Override
    protected void onStartLoading() {

        if (data != null)
            deliverResult(data);
        else
            forceLoad();
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(ContentWrapper<T> data) {
        super.onCanceled(data);

        releaseResources(data);
    }

    @Override
    protected void onReset() {

        onStopLoading();

        if (data != null) {
            releaseResources(data);
            data = null;
        }
    }

    private void releaseResources(ContentWrapper<T> data) {
    }

}
