package com.example.yegor.nbrb.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.yegor.nbrb.models.CurrencyModel;

import java.util.ArrayList;
import java.util.List;

public class MySQLiteClass {

    private static final String DATABASE_NAME = "CURRENCY_DB";
    private static final int DATABASE_VERSION = 1;
    private static final String CURRENCY_TABLE = "CURRENCY_TABLE";

    private Context context;
    private DBHelp dbhelp;
    private SQLiteDatabase thisDataBase;

    public MySQLiteClass(Context context) {
        this.context = context;
    }

    public MySQLiteClass open(boolean writable) throws SQLiteException {

        dbhelp = new DBHelp(context);

        if (writable)
            thisDataBase = dbhelp.getWritableDatabase();
        else
            thisDataBase = dbhelp.getReadableDatabase();
        return this;
    }

    public void close() {
        dbhelp.close();
    }

    public void addCurrency(CurrencyModel currency) {

        ContentValues values = new ContentValues();

        values.put(CurrencyModel.ID, currency.getId());
        values.put(CurrencyModel.QUOT_NAME, currency.getQuotName());
        values.put(CurrencyModel.QUOT_NAME_ENG, currency.getQuotNameEng());
        values.put(CurrencyModel.SCALE, currency.getScale());
        values.put(CurrencyModel.CODE, currency.getCode());
        values.put(CurrencyModel.ABBR, currency.getAbbr());
        values.put(CurrencyModel.NAME, currency.getName());
        values.put(CurrencyModel.NAME_ENG, currency.getNameEng());
        values.put(CurrencyModel.DATE_START, currency.getDateStart());
        values.put(CurrencyModel.DATE_END, currency.getDateEnd());
        values.put(CurrencyModel.PARENT_ID, currency.getParentId());

        thisDataBase.insert(CURRENCY_TABLE, null, values);

    }

    public void addCurrencies(List<CurrencyModel> currencies) {

        open(true);

        for (CurrencyModel currency : currencies)
            addCurrency(currency);

        close();

    }

    public List<CurrencyModel> getAllCurrencies() {

        List<CurrencyModel> currencies = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + CURRENCY_TABLE;

        open(false);

        Cursor cursor = thisDataBase.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                CurrencyModel contact = new CurrencyModel.Builder()
                        .setId(Integer.parseInt(cursor.getString(0)))
                        .setQuotName(cursor.getString(1))
                        .setQuotNameEng(cursor.getString(2))
                        .setScale(Integer.parseInt(cursor.getString(3)))
                        .setCode(cursor.getString(4))
                        .setAbbr(cursor.getString(5))
                        .setName(cursor.getString(6))
                        .setNameEng(cursor.getString(7))
                        .setDateStart(cursor.getString(8))
                        .setDateEnd(cursor.getString(9))
                        .setParentId(Integer.parseInt(cursor.getString(10)))
                        .create();

                currencies.add(contact);

            } while (cursor.moveToNext());

        }

        cursor.close();
        close();

        return currencies;
    }

    public List<String> getCurrenciesAbbr() {

        List<String> list = new ArrayList<>(64);

        open(false);

        Cursor cursor = thisDataBase.query(
                CURRENCY_TABLE,
                new String[]{CurrencyModel.ABBR},
                null,//CurrencyModel.DATE_END + " = \"\" OR  " + CurrencyModel.DATE_END + " IS NULL",
                null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        close();

        return list;

    }


    public List<String[]> getCurrenciesNames2() {

        List<String[]> list = new ArrayList<>(64);

        open(false);

        Cursor cursor = thisDataBase.query(
                CURRENCY_TABLE,
                new String[]{CurrencyModel.ABBR, CurrencyModel.NAME},
                CurrencyModel.DATE_END + " != ",
                new String[]{"NULL"},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(new String[]{cursor.getString(0), cursor.getString(1)});
            } while (cursor.moveToNext());
        }

        cursor.close();
        close();

        return list;

    }


    private class DBHelp extends SQLiteOpenHelper {

        private final String CREATE_CURRENCY_TABLE =
                "CREATE TABLE " + CURRENCY_TABLE + "(" +
                        CurrencyModel.ID + " INTEGER PRIMARY KEY, " +
                        CurrencyModel.QUOT_NAME + " TEXT NOT NULL, " +
                        CurrencyModel.QUOT_NAME_ENG + " TEXT NOT NULL, " +
                        CurrencyModel.SCALE + " INTEGER, " +
                        CurrencyModel.CODE + " TEXT NOT NULL, " +
                        CurrencyModel.ABBR + " TEXT NOT NULL, " +
                        CurrencyModel.NAME + " TEXT NOT NULL, " +
                        CurrencyModel.NAME_ENG + " TEXT NOT NULL, " +
                        CurrencyModel.DATE_START + " TEXT NOT NULL, " +
                        CurrencyModel.DATE_END + " TEXT NOT NULL, " +
                        CurrencyModel.PARENT_ID + " INTEGER);";

        public DBHelp(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_CURRENCY_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

}
