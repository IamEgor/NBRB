package com.example.yegor.nbrb;

import android.app.Application;
import android.content.Context;

import com.example.yegor.nbrb.storage.DatabaseManager;

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        DatabaseManager.initializeInstance(this);

    }

    public static Context getContext() {
        return context;
    }

}
