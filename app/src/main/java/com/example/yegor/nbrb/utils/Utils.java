package com.example.yegor.nbrb.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.yegor.nbrb.App;
import com.example.yegor.nbrb.storage.AppPrefs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Utils {

    private static final SimpleDateFormat format =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

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

    public static String format(long time) {
        return format.format(time);
    }

    public static long date2long(String date) throws ParseException {
        return format.parse(date).getTime();
    }

    public static boolean isLollipop() {
        return getSDKInt() >= android.os.Build.VERSION_CODES.LOLLIPOP;
    }

    public static int getSDKInt() {
        return android.os.Build.VERSION.SDK_INT;
    }
}
