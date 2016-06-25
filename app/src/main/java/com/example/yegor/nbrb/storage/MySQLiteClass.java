package com.example.yegor.nbrb.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.example.yegor.nbrb.App;
import com.example.yegor.nbrb.models.CurrencyModel;
import com.example.yegor.nbrb.models.SpinnerModel;
import com.example.yegor.nbrb.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MySQLiteClass {

    private static final String DATABASE_NAME = "CURRENCY_DB";
    private static final int DATABASE_VERSION = 1;
    private static final String CURRENCY_TABLE = "CURRENCY_TABLE";

    private Context context;
    private DBHelp dbhelp;
    private SQLiteDatabase thisDataBase;

    private MySQLiteClass(Context context) {
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

        Collections.sort(currencies, (lhs, rhs) -> lhs.getName().compareTo(rhs.getName()));

        for (CurrencyModel currency : currencies)
            addCurrency(currency);

    }

    public List<SpinnerModel> getCurrenciesDescription() {

        List<SpinnerModel> list = new ArrayList<>();

        Cursor cursor = thisDataBase.query(
                CURRENCY_TABLE,
                new String[]{CurrencyModel.ABBR, CurrencyModel.NAME, CurrencyModel.SCALE, CurrencyModel.DATE_END},
                null, null,
                CurrencyModel.PARENT_ID, null,
                CurrencyModel.NAME);

        if (cursor.moveToFirst()) {
            do {
                list.add(
                        new SpinnerModel(cursor.getString(0),
                                cursor.getString(1),
                                cursor.getInt(2),
                                cursor.getLong(3)));

            } while (cursor.moveToNext());
        }

        cursor.close();

        return list;

    }

    public Cursor getCurrenciesDescriptionCursor() {

        return thisDataBase.query(
                CURRENCY_TABLE,
                new String[]{CurrencyModel.ID + " as _id", CurrencyModel.ABBR, CurrencyModel.NAME, CurrencyModel.DATE_END},
                null, null,
                CurrencyModel.PARENT_ID, null,
                CurrencyModel.NAME);
    }

    public Cursor getCurrenciesDescriptionCursor(String string) {

        if (TextUtils.isEmpty(string))
            return null;

        return thisDataBase.query(
                CURRENCY_TABLE,
                new String[]{CurrencyModel.ID + " as _id", CurrencyModel.ABBR, CurrencyModel.NAME, CurrencyModel.DATE_END},
                CurrencyModel.ABBR + " like('%?%') OR " + CurrencyModel.NAME + " like('%?%')",
                new String[]{string},
                CurrencyModel.PARENT_ID, null,
                CurrencyModel.NAME);
    }

    public CurrencyModel getCurrencyModelByAbbr(String abbr, String time) {

        // TODO: 21.06.16 проверить, почему сравнивалась дата с милисекундами
        time = String.valueOf(DateUtils.date2longSafe(time));

        Cursor cursor = thisDataBase.query(
                CURRENCY_TABLE,
                new String[]{
                        CurrencyModel.ID, CurrencyModel.QUOT_NAME, CurrencyModel.QUOT_NAME_ENG,
                        CurrencyModel.SCALE, CurrencyModel.CODE, CurrencyModel.ABBR,
                        CurrencyModel.NAME, CurrencyModel.NAME_ENG, CurrencyModel.DATE_START,
                        CurrencyModel.DATE_END, CurrencyModel.PARENT_ID},
                CurrencyModel.ABBR + " =? AND (" + CurrencyModel.DATE_END + " >? OR " + CurrencyModel.DATE_END + " = '-1')",
                new String[]{abbr, time},
                null, null, null);

        cursor.moveToFirst();

        CurrencyModel model = new CurrencyModel.Builder()
                .setId(cursor.getInt(0))
                .setQuotName(cursor.getString(1))
                .setQuotNameEng(cursor.getString(2))
                .setScale(cursor.getInt(3))
                .setCode(cursor.getString(4))
                .setAbbr(cursor.getString(5))
                .setName(cursor.getString(6))
                .setNameEng(cursor.getString(7))
                .setDateStart(cursor.getLong(8))
                .setDateEnd(cursor.getLong(9))
                .setParentId(cursor.getInt(10))
                .create();


        cursor.close();

        return model;
    }

    //дата < чем дата окончания и > чем дата начала
    public boolean isDateValid(String abbr, String dateString) {

        Cursor cursor = thisDataBase.query(
                CURRENCY_TABLE,
                new String[]{CurrencyModel.DATE_START, CurrencyModel.DATE_END},
                CurrencyModel.ABBR + " =? ",
                new String[]{abbr},
                null, null,
                CurrencyModel.DATE_START);

        long[][] dates = new long[cursor.getCount()][2];

        if (cursor.moveToFirst()) {
            int i = 0;
            do {
                dates[i][0] = cursor.getLong(0);
                dates[i++][1] = cursor.getLong(1);
            } while (cursor.moveToNext());
        }

        cursor.close();

        long specifiedDate = DateUtils.date2longSafe(dateString);


        if (dates.length == 1 && dates[0][dates.length] == -1)
            return true;
        else if (specifiedDate >= dates[0][0] && dates[dates.length - 1][1] == -1)
            return true;
        else if (specifiedDate >= dates[0][0] && specifiedDate <= dates[dates.length - 1][1])
            return true;
        else
            return false;

        //TODO что это?
        //return dateL <- dateEnd;
    }

    private static MySQLiteClass instance;

    public static MySQLiteClass getInstance() {

        if (instance == null)
            instance = new MySQLiteClass(App.getContext());

        return instance.open(true);

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
                        CurrencyModel.DATE_START + " INTEGER, " +
                        CurrencyModel.DATE_END + " INTEGER, " +
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
