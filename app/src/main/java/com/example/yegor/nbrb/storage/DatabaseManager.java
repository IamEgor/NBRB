package com.example.yegor.nbrb.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.example.yegor.nbrb.models.CurrencyModel;
import com.example.yegor.nbrb.models.SpinnerModel;
import com.example.yegor.nbrb.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseManager {

    private static final String DATABASE_NAME = "CURRENCY_DB";
    private static final int DATABASE_VERSION = 1;
    private static final String CURRENCY_TABLE = "CURRENCY_TABLE";

    private SQLiteDatabase thisDataBase;

    private DatabaseManager() {
    }

    private synchronized SQLiteDatabase getDatabase(boolean writable) {
        return writable ? dbhelp.getWritableDatabase() : dbhelp.getReadableDatabase();
    }

    private void addCurrencyUnsafe(CurrencyModel currency) {

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

    @WorkerThread
    public void addCurrency(CurrencyModel currency) {

        thisDataBase = getDatabase(true);
        addCurrencyUnsafe(currency);
        thisDataBase.close();
    }

    private void addCurrenciesBulkUnsafe(List<CurrencyModel> currencies) {

        Collections.sort(currencies, (lhs, rhs) -> lhs.getName().compareTo(rhs.getName()));

        String sql = "INSERT INTO " + CURRENCY_TABLE + " VALUES (?,?,?,?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = thisDataBase.compileStatement(sql);

        thisDataBase.beginTransaction();

        for (CurrencyModel model : currencies) {

            statement.clearBindings();
            statement.bindLong(1, model.getId());
            statement.bindString(2, model.getQuotName());
            statement.bindString(3, model.getQuotNameEng());
            statement.bindLong(4, model.getScale());
            statement.bindString(5, model.getCode());
            statement.bindString(6, model.getAbbr());
            statement.bindString(7, model.getName());
            statement.bindString(8, model.getNameEng());
            statement.bindLong(9, model.getDateStart());
            statement.bindLong(10, model.getDateEnd());
            statement.bindLong(11, model.getParentId());

            statement.execute();
        }

        thisDataBase.setTransactionSuccessful();
        thisDataBase.endTransaction();
    }

    @WorkerThread
    public void addCurrenciesBulk(List<CurrencyModel> currencies) {

        thisDataBase = getDatabase(true);
        addCurrenciesBulkUnsafe(currencies);
        thisDataBase.close();
    }

    private List<SpinnerModel> getCurrenciesDescriptionUnsafe() {

        List<SpinnerModel> list = new ArrayList<>();

        Cursor cursor = thisDataBase.query(
                CURRENCY_TABLE,
                new String[]{CurrencyModel.ABBR, CurrencyModel.NAME, CurrencyModel.SCALE, CurrencyModel.DATE_END},
                null, null,
                CurrencyModel.PARENT_ID, "MAX(" + CurrencyModel.DATE_END + ")", null);

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

    @WorkerThread
    public List<SpinnerModel> getCurrenciesDescription() {

        thisDataBase = getDatabase(false);
        List<SpinnerModel> descriptionModels = getCurrenciesDescriptionUnsafe();
        thisDataBase.close();

        return descriptionModels;
    }

    private Cursor getCurrenciesDescriptionCursor() {

        return thisDataBase.query(
                CURRENCY_TABLE,
                new String[]{CurrencyModel.ID + " as _id", CurrencyModel.ABBR, CurrencyModel.NAME, CurrencyModel.DATE_END},
                null, null,
                CurrencyModel.PARENT_ID, null,
                CurrencyModel.NAME);
    }

    private Cursor getCurrenciesDescriptionCursor(String string) {

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

    private CurrencyModel getCurrencyModelByAbbrUnsafe(String abbr, String time) {

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

    @WorkerThread
    public CurrencyModel getCurrencyModelByAbbr(String abbr, String time) {

        thisDataBase = getDatabase(false);
        CurrencyModel currencyModel = getCurrencyModelByAbbrUnsafe(abbr, time);
        thisDataBase.close();

        return currencyModel;
    }

    //дата < чем дата окончания и > чем дата начала
    private boolean isDateValidUnsafe(String abbr, String dateString) {

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
    }

    @WorkerThread
    public boolean isDateValid(String abbr, String dateString) {

        thisDataBase = getDatabase(false);
        boolean isDateValid = isDateValidUnsafe(abbr, dateString);
        thisDataBase.close();

        return isDateValid;
    }

    private static DatabaseManager instance;
    private static DBHelp dbhelp;

    public static synchronized void initializeInstance(Context context) {

        if (instance == null) {
            instance = new DatabaseManager();
            dbhelp = new DBHelp(context);
        }
    }

    public static synchronized DatabaseManager getInstance() {

        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initialize(Context) method first.");
        }

        return instance;
    }

    private interface Executor<T> {
        T safeExecute();
    }

    private static class DBHelp extends SQLiteOpenHelper {

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
