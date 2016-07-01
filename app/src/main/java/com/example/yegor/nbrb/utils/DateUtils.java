package com.example.yegor.nbrb.utils;

import com.example.yegor.nbrb.storage.AppPrefs;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {

    public static final long START_DATE = 856137600 * 1000L;//17.02.1997
    public static final long END_DATE = 2524608000L * 1000L;
    public static final long ONE_DAY = 24 * 60 * 60 * 1000L;
    public static final long WEEK_LENGTH = 7 * ONE_DAY;

    private static final SimpleDateFormat defaultFormat;
    private static final SimpleDateFormat unixFormat;
    private static final Calendar calendar;

    private static final SimpleDateFormat formatWeekdayAndDate;

    static {

        defaultFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        defaultFormat.setLenient(false);
        unixFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

        calendar = Calendar.getInstance();

        formatWeekdayAndDate = new SimpleDateFormat("EEE, dd MMMM",
                new DateFormatSymbols() {
                    @Override
                    public String[] getMonths() {
                        return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                                "июля", "августа", "сентября", "октября", "ноября", "декабря"};
                    }
                });

    }

    public static boolean need2Update() {
        return (AppPrefs.getLastUpdate() + WEEK_LENGTH - Calendar.getInstance().getTimeInMillis()) < 0;
    }

    public static String format(Calendar time) {
        return format(time.getTimeInMillis());
    }

    public static String format(GregorianCalendar time) {
        return format(time.getTimeInMillis());
    }

    public static String format(long time) {
        return defaultFormat.format(time);
    }

    public static String formatWeekdayAndDate(Calendar calendar) {
        return formatWeekdayAndDate.format(calendar.getTimeInMillis());
    }

    public static long date2long(String date) throws ParseException {
        return defaultFormat.parse(date).getTime();
    }

    public static long date2longSafe(String date) {
        try {
            return defaultFormat.parse(date).getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    public static long unixTime2longSafe(String date) {
        try {
            return unixFormat.parse(date).getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

    public static Calendar getCalendar(String date) {
        return getCalendar(date2longSafe(date));
    }

    public static Calendar getCalendar(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar;
    }

    //TODO------------
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

    public static Calendar getDateTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        return calendar;
    }

    public static String getFirstDayOfMonth(String date) {

        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return format(calendar);
    }

}
