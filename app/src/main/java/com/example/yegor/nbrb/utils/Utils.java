package com.example.yegor.nbrb.utils;


import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.yegor.nbrb.App;
import com.example.yegor.nbrb.storage.AppPrefs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public final class Utils {

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

    public static String format(GregorianCalendar time) {
        return format(time.getTimeInMillis());
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isPortrait(Activity activity){

        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return size.x < size.y;

    }

    public static void logT(String tag, Object... objects) {
        String message = "";
        for (Object o : objects)
            message += o.toString() + " | ";
        System.out.println("[" + tag + "] " + message);
        Log.w(tag, message);
    }

    public static final String TAG = "[APP_LOGS]";

    public static void log(Object... objects) {
        String message = "";
        for (Object o : objects)
            message += o.toString() + " | ";
        System.out.println(TAG + " " + message);
        Log.w(TAG, message);
    }

}
