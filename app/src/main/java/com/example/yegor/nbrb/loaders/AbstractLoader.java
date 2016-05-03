package com.example.yegor.nbrb.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.yegor.nbrb.exceptions.NoConnectionException;
import com.example.yegor.nbrb.exceptions.NoDataFoundException;
import com.example.yegor.nbrb.models.ContentWrapper;
import com.example.yegor.nbrb.utils.Utils;

import java.io.IOException;

public class AbstractLoader<T> extends AsyncTaskLoader<ContentWrapper<T>> {

    AbstractLoaderInterface<T> action;

    public AbstractLoader(Context context, AbstractLoaderInterface<T> action) {
        super(context);

        this.action = action;
    }

    @Override
    public ContentWrapper<T> loadInBackground() {

        if (!Utils.hasConnection()) {
            try {
                Thread.sleep(350);//for beauty
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new ContentWrapper<>(new NoConnectionException());
        }

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

}
