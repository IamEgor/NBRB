package com.example.yegor.nbrb;

import com.annimon.stream.Stream;
import com.example.yegor.nbrb.utils.ChartUtils;
import com.example.yegor.nbrb.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    static String[] givenDates = {"2000-05-06", "2000-05-16", "2000-05-18", "2000-05-26"};
    static float[] givenRates = {2.5f, 11.15f, 6.25f, 7.15f};

    static String[] givenDates2 = {"2007-11-30T00:00:00+03:00", "2016-06-30T00:00:00+03:00",
            "2050-01-01T00:00:00+03:00", "2013-05-31T00:00:00+03:00"};

    static SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static void main(String[] args) {

        String mydata = "1000 GFD";
        Pattern pattern = Pattern.compile("\\S{3}$");
        Matcher matcher = pattern.matcher(mydata);

        if (matcher.find())
            System.out.println("matcher = " + matcher.group(0));

        for (String s : givenDates2)
            ff(s);

    }

    private static void ff(String s) {

        try {

            Date date1 = format1.parse(s);
            Date date2 = format2.parse(s);

            System.out.println("format1 = " + format1.format(date1) + " " + format1.format(date2));
            System.out.println("format2 = " + format2.format(date1) + " " + format2.format(date2));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    static List<InflatedDates> f(String from, String to, List<InflatedDates> initialArray) {

        int expectedLength = ChartUtils.expectedLength(from, to);
        long startDay = DateUtils.date2longSafe(from);

        List<InflatedDates> list = new ArrayList<>(expectedLength);

        float rate;

        for (int i = 0; i < expectedLength; i++, startDay += DateUtils.ONE_DAY) {

            final long finalStartDay = startDay;
            rate = Stream.of(initialArray)
                    .filter(dat1 -> finalStartDay >= dat1.date)
                    .min((lhs, rhs) -> Long.valueOf(rhs.date - finalStartDay).compareTo(lhs.date - finalStartDay))
                    .get()
                    .rate;

            list.add(new InflatedDates(startDay, rate));
        }

        return list;
    }

    static class InflatedDates {

        long date;
        float rate;

        public InflatedDates() {
        }

        public InflatedDates(long date, float rate) {
            this.date = date;
            this.rate = rate;
        }

        @Override
        public String toString() {
            return "InflatedDates{" +
                    "date='" + DateUtils.format(date) + '\'' +
                    ", rate=" + rate +
                    '}';
        }

    }

}
