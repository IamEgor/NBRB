package com.example.yegor.nbrb.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.yegor.nbrb.App;

public final class Utils {

    private static final boolean DEBUG_MODE = true;
    private static final String TAG = "[APP_LOGS]";

    public static String getString(@StringRes int id) {
        return App.getContext().getString(id);
    }

    public static int getColor(int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return App.getContext().getColor(color);
        else
            return App.getContext().getResources().getColor(color);
    }

    public static int getSDKInt() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static boolean hasConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isPortrait(Activity activity) {

        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return size.x < size.y;
    }

    public static int getStatusBarHeight() {

        int result = 0;
        int resourceId = App.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0)
            result = App.getContext().getResources().getDimensionPixelSize(resourceId);

        return result;
    }

    public static void hideKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();

        if (view == null)
            view = new View(activity);

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
