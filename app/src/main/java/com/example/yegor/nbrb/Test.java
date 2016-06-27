package com.example.yegor.nbrb;

import com.annimon.stream.Stream;
import com.example.yegor.nbrb.utils.ChartUtils;
import com.example.yegor.nbrb.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    static String[] givenDates = {"2000-05-06", "2000-05-16", "2000-05-18", "2000-05-26"};
    static float[] givenRates = {2.5f, 11.15f, 6.25f, 7.15f};

    public static void main(String[] args) {

        String fromDate = "2016-06-10";
        String toDate = "2016-06-15";

        List<InflatedDates> list = new ArrayList<>(givenDates.length);
        /*
        for (int i = 0; i < givenDates.length; i++) {
            list.add(new InflatedDates(Utils.date2longSafe(givenDates[i]), givenRates[i]));
        }
        */
        list.add(new InflatedDates(DateUtils.date2longSafe("2016-06-01"), 178.85f));

        Calendar calendar1 = DateUtils.getCalendar(fromDate);
        Calendar calendar2 = DateUtils.getCalendar(toDate);

        calendar1.set(Calendar.DAY_OF_MONTH, 1);

        calendar2.set(Calendar.DAY_OF_MONTH, 1);


        System.out.println(fromDate + " " + DateUtils.format(calendar1));
        System.out.println(toDate + " " + DateUtils.format(calendar2));


        System.out.println(f(fromDate, toDate, list));

        Calendar calendar = Calendar.getInstance();
        System.out.println("Calendar.YEAR = " + calendar.get(Calendar.YEAR));


        String mydata = "1000 GFD";
        Pattern pattern = Pattern.compile("\\S{3}$");
        Matcher matcher = pattern.matcher(mydata);
        if (matcher.find()) {
            System.out.println("matcher = " + matcher.group(0));
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
