package com.example.yegor.nbrb;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Test {

    public static void main(String[] args) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();

        System.out.println(format.format(calendar.getTimeInMillis()));
        calendar.roll(Calendar.MONTH, false);
        System.out.println(format.format(calendar.getTimeInMillis()));


        float min = 19214.0f, max = 20254.0f;

        int iMin, iMax, division, count = 31;

        iMin = (int) min;
        iMax = (int) max;

        division = (iMax - iMin) / count;

        System.out.println("iMin - " + iMin + ", iMax - " + iMax + ", division - " + division);

        int iters = 0;

        do {
            if ((iMax - iMin) % count == 0)
                break;
            iMax++;
            iters++;
            if ((iMax - iMin) % count == 0)
                break;
            iMin--;
            iters++;
        } while (true);

        System.out.println("iMin - " + iMin + ", iMax - " + iMax +
                ", division - " + division + ", iters - " + iters);
    }

}
