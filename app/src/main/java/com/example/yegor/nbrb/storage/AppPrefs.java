package com.example.yegor.nbrb.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.yegor.nbrb.App;

public class AppPrefs {

    public static final String APP_PREFS = "app_prefs";

    public static final String LAST_UPDATE = "last_update";

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    static {
        sharedPreferences = App.getContext().getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void put(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBool(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public static void put(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public static void put(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(String key) {
        return sharedPreferences.getLong(key, -1L);
    }

    public static long getLastUpdate() {
        return sharedPreferences.getLong(LAST_UPDATE, -1L);
    }

    public static void setLastUpdate(long lastUpdate) {
        editor.putLong(LAST_UPDATE, lastUpdate);
        editor.apply();
    }

}
