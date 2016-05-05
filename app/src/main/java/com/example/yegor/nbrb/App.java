package com.example.yegor.nbrb;

import android.app.Application;
import android.content.Context;

import com.example.yegor.nbrb.storage.MySQLiteClass;

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        MySQLiteClass.getInstance();

    }

    @Override
    public void onTerminate() {
        MySQLiteClass.getInstance().close();
        super.onTerminate();
    }

    public static Context getContext() {
        return context;
    }
}
