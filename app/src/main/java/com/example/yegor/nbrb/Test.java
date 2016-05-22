package com.example.yegor.nbrb;

import com.example.yegor.nbrb.utils.Utils;

import java.text.SimpleDateFormat;

public class Test {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static void main(String[] args) {


        long[][] datesEndStart1 = {              //Турецкая лира TRY
                {1196460000000L, 1280696400000L},
                {1280782800000L, -1L}
        };

        long[][] datesEndStart2 = {              //Суданский динар SDD
                {662677200000L, 1280523600000L}
        };

        long[][] datesEndStart3 = {              //Новозеландский доллар NZD
                {662677200000L, 1388437200000L},
                {1388523600000L, -1L}
        };

        long[][] datesEndStart4 = {              //Евро EUR
                {915141600000L, -1L}
        };

        long specifiedDate = Utils.date2longUnSafe("2007-12-01");

        System.out.println(sdf.format(datesEndStart1[0][0]) + "\t" + sdf.format(specifiedDate));

        System.out.println(print(datesEndStart1) +
                "\t\t\t" + Utils.format(specifiedDate) +
                "\t\t\t" + f(datesEndStart1, specifiedDate));


    }

    private static boolean f(long[][] dates, long specifiedDate) {
        if (dates.length == 1 && dates[0][dates.length] == -1)
            return true;
        else if (specifiedDate >= dates[0][0] && dates[dates.length - 1][1] == -1)
            return true;
        else if (specifiedDate >= dates[0][0] && specifiedDate <= dates[dates.length - 1][1])
            return true;
        else
            return false;
    }

    private static String print(long[][] array) {

        for (long[] longs : array)
            System.out.print(
                    "\n[Start] - " + Utils.format(longs[0]) +
                            ", [End] - " + (longs[1] == -1 ? "Nothing   " : Utils.format(longs[1])));
        return "";

    }



}
