package com.example.yegor.nbrb.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.yegor.nbrb.App;
import com.example.yegor.nbrb.storage.AppPrefs;

import java.util.Calendar;

public class Utils {

    public static final long WEEK_LENGTH = 7 * 24 * 60 * 60 * 1000L;

    public static boolean need2Update() {
        Log.w("[Utils]", "AppPrefs.getLastUpdate() - " + AppPrefs.getLastUpdate() +
                ", TimeInMillis -  " + Calendar.getInstance().getTimeInMillis() +
                " difference - " + (AppPrefs.getLastUpdate() + WEEK_LENGTH - Calendar.getInstance().getTimeInMillis()));
        return (AppPrefs.getLastUpdate() + WEEK_LENGTH - Calendar.getInstance().getTimeInMillis()) < 0;
    }

    public static boolean hasConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
