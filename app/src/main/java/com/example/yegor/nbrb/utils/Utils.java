package com.example.yegor.nbrb.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.StringRes;
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
import java.util.concurrent.atomic.AtomicInteger;

public final class Utils {

    public static final long START_DATE = 856137600 * 1000L;//17.02.1997
    public static final long WEEK_LENGTH = 7 * 24 * 60 * 60 * 1000L;

    private static final boolean DEBUG_MODE = true;
    private static final String TAG = "[APP_LOGS]";

    private static final AtomicInteger sNextGeneratedId;
    private static final SimpleDateFormat format;

    static {
        sNextGeneratedId = new AtomicInteger(1);
        format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        format.setLenient(false);
    }

    public static boolean need2Update() {
        return (AppPrefs.getLastUpdate() + WEEK_LENGTH - Calendar.getInstance().getTimeInMillis()) < 0;
    }

    public static boolean hasConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String getString(@StringRes int id) {
        return App.getContext().getString(id);
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

    public static long date2longUnSafe(String date) {
        try {
            return format.parse(date).getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    public static long getEdgeTime() {//23:59:59 of next day

        Calendar calendar2 = Calendar.getInstance();

        Calendar calendar1 = new GregorianCalendar(
                calendar2.get(Calendar.YEAR),
                calendar2.get(Calendar.MONTH),
                calendar2.get(Calendar.DAY_OF_MONTH));

        calendar1.roll(Calendar.DATE, 2);
        calendar1.add(Calendar.MILLISECOND, -1);

        return calendar1.getTimeInMillis();

    }

    public static Calendar setCalendar(Calendar calendar, String string) {
        try {
            calendar.setTimeInMillis(date2long(string));
            return calendar;
        } catch (ParseException e) {
            throw new RuntimeException();
        }
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

    public static boolean isPortrait(Activity activity) {

        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return size.x < size.y;

    }

    public static Calendar getCalendar(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date2longUnSafe(date));
        return calendar;
    }

    public static int getColor(int color) {
        if (getSDKInt() >= 23)
            return App.getContext().getColor(color);
        else
            return App.getContext().getResources().getColor(color);
    }

    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static void logT(String tag, Object... objects) {
        if (!DEBUG_MODE)
            return;

        String message = "";
        for (Object o : objects)
            message += o.toString() + " | ";
        System.out.println("[" + tag + "] " + message);
        Log.w(tag, message);
    }

    public static void log(Object... objects) {
        if (!DEBUG_MODE)
            return;

        String message = "";
        for (Object o : objects)
            message += o.toString() + " | ";
        System.out.println(TAG + " " + message);
        Log.w(TAG, message);
    }

}
