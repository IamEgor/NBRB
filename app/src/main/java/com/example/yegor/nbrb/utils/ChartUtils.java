package com.example.yegor.nbrb.utils;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.annimon.stream.Stream;
import com.example.yegor.nbrb.App;
import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.exceptions.ExchangeRateAssignsOnceInMonth;
import com.example.yegor.nbrb.models.CurrencyModel;
import com.example.yegor.nbrb.models.ExRatesDynModel;
import com.example.yegor.nbrb.storage.MySQLiteClass;
import com.example.yegor.nbrb.views.MyMarkerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

public final class ChartUtils {

    public static LineData getChartContent1(String abbr, String fromDate, String toDate)
            throws IOException {

        CurrencyModel currency = MySQLiteClass.getInstance().getCurrencyModelByAbbr(abbr, fromDate);
        List<ExRatesDynModel> content = SoapUtils.getRatesDyn(currency.getIdStr(), fromDate, toDate);

        if (expectedLength(fromDate, toDate) != content.size())
            throw new ExchangeRateAssignsOnceInMonth();

        int scale = currency.getScale();
        float[] floats = new float[content.size()];
        String[] strings = new String[content.size()];

        for (int i = 0; i < content.size(); i++) {
            strings[i] = content.get(i).getDate();
            floats[i] = content.get(i).getRate() / scale;
        }

        List<Entry> yVals = new ArrayList<>();

        for (int i = 0; i < floats.length; i++) {
            yVals.add(new Entry(floats[i], i));
        }

        LineDataSet set1 = new LineDataSet(yVals, null);
        set1.setDrawValues(false);
        set1.setFillAlpha(192);
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Utils.getColor(R.color.colorAccent));
        set1.setCircleColor(Utils.getColor(R.color.colorAccent));
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.disableDashedLine();

        if (Utils.getSDKInt() < 18)
            set1.setFillColor(Utils.getColor(R.color.colorPrimary));
        else {
            Drawable drawable = ContextCompat.getDrawable(App.getContext(), R.drawable.fade_primary);
            set1.setFillDrawable(drawable);
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        return new LineData(strings, dataSets);
    }

    public static LineData getChartContent2(String abbr, String fromDate, String toDate)
            throws IOException {

        CurrencyModel currency = MySQLiteClass.getInstance().getCurrencyModelByAbbr(abbr, fromDate);
        List<ExRatesDynModel> content = SoapUtils.getRatesDyn(
                currency.getIdStr(),
                DateUtils.getFirstDayOfMonth(fromDate),
                DateUtils.getFirstDayOfMonth(toDate));

        int scale = currency.getScale();
        float[]  floats;
        String[]   strings;

        List<InflatedDates> dates = new ArrayList<>(content.size());

        for (int i = 0; i < content.size(); i++)
            dates.add(new InflatedDates(content.get(i).getDate(), content.get(i).getRate() / scale));

        inflateSet(fromDate, toDate, dates);//TODO+++++

        floats = new float[dates.size()];
        strings = new String[dates.size()];

        for (int i = 0; i < dates.size(); i++) {
            floats[i] = dates.get(i).rate;
            strings[i] = DateUtils.format(dates.get(i).date);
        }

        List<Entry> yVals = new ArrayList<>();

        for (int i = 0; i < floats.length; i++) {
            yVals.add(new Entry(floats[i], i));
        }

        LineDataSet set1 = new LineDataSet(yVals, null);
        set1.setDrawValues(false);
        set1.setFillAlpha(192);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Utils.getColor(R.color.colorAccent));
        set1.setCircleColor(Utils.getColor(R.color.colorAccent));
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.disableDashedLine();

        if (Utils.getSDKInt() >= 18) {
            Drawable drawable = ContextCompat.getDrawable(App.getContext(), R.drawable.fade_primary);
            set1.setFillDrawable(drawable);
        } else {
            set1.setFillColor(Utils.getColor(R.color.colorPrimary));
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        return new LineData(strings, dataSets);
    }

    public static LineData getChartContent3(String abbr, String fromDate, String toDate)
            throws IOException {

        Calendar calendar1 = DateUtils.getCalendar(fromDate);
        Calendar calendar2 = DateUtils.getCalendar(toDate);

        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        calendar2.set(Calendar.DAY_OF_MONTH, 1);

        return getChartContent1(abbr, DateUtils.format(calendar1), DateUtils.format(calendar2));
    }

    public static void setUpChart(LineChart mChart, LineData content) {

        mChart.setData(content);
        mChart.invalidate();

        mChart.setDescription(null);
        mChart.getLegend().setEnabled(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
        mChart.getAxisRight().setEnabled(false);

        MyMarkerView mv = new MyMarkerView(App.getContext(), R.layout.custom_marker_view, content.getXVals());
        mChart.setMarkerView(mv);

    }

    public static void setDisabledColor(LineChart chart) {

        if (chart.getData() == null)
            return;

        List<ILineDataSet> sets = chart.getData().getDataSets();

        for (ILineDataSet iSet : sets) {

            LineDataSet set = (LineDataSet) iSet;
            set.setFillAlpha(128);
            set.setFillColor(Utils.getColor(R.color.dark_grey));
            set.setColor(Utils.getColor(R.color.midi_grey));
            set.setCircleColor(Utils.getColor(R.color.midi_grey));
        }
        chart.invalidate();
    }

    public static int expectedLength(String fromDate, String toDate) {

        long date1 = DateUtils.date2longSafe(fromDate);
        long date2 = DateUtils.date2longSafe(toDate);

        return (int) ((date2 - date1 + DateUtils.ONE_DAY) / (1000 * 60 * 60 * 24));
    }

    public static List<InflatedDates> inflateSet(String from, String to, List<InflatedDates> initialArray)
            throws IOException {

        int expectedLength = ChartUtils.expectedLength(from, to);
        long startDay = DateUtils.date2longSafe(from);

        List<InflatedDates> list = new ArrayList<>(expectedLength);

        float rate;

        try {
            for (int i = 0; i < expectedLength; i++, startDay += DateUtils.ONE_DAY) {

                final long finalStartDay = startDay;
                rate = Stream.of(initialArray)
                        .filter(dat1 -> finalStartDay >= dat1.date)
                        .min((lhs, rhs) -> Long.valueOf(rhs.date - finalStartDay).compareTo(lhs.date - finalStartDay))
                        .get()
                        .rate;

                list.add(new InflatedDates(startDay, rate));
            }
        } catch (NoSuchElementException e) {
            throw new ExchangeRateAssignsOnceInMonth();
        }

        initialArray.clear();
        initialArray.addAll(list);

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

        public InflatedDates(String date, float rate) {
            this.date = DateUtils.date2longSafe(date);
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

    /*
    private static void setUpChartTest(LineChart mChart, List<ExRatesDynModel> content) {

        float max = 0f, min = content.get(0).getRate();

        float[] floats = new float[content.size()];
        String[] strings = new String[content.size()];

        for (int i = 0; i < content.size(); i++) {
            floats[i] = content.get(i).getRate();
            strings[i] = content.get(i).getDate();
            if (floats[i] > max)
                max = floats[i];
            if (floats[i] < min)
                min = floats[i];
        }

        //Kind of normalize
        min = 0.998f * min;
        max = 1.001f * max;

        int count = 10;//floats.length ;
        int iMin = (int) min;
        int iMax = (int) max;
        //int division = (iMax - iMin) / count;

        do {
            if ((iMax - iMin) % count == 0)
                break;
            iMax++;
            if ((iMax - iMin) % count == 0)
                break;
            iMin--;
        } while (true);

        // create a dataset and give it a type
        List<Entry> yVals = new ArrayList<>();

        for (int i = 0; i < floats.length; i++) {
            yVals.add(new Entry(floats[i], i));
        }

        LineDataSet set1 = new LineDataSet(yVals, null);
        set1.setDrawValues(false);
        set1.setFillAlpha(110);
        set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.disableDashedLine();

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(App.getContext(), R.drawable.fade_primary);
            set1.setFillDrawable(drawable);
        } else {
            set1.setFillColor(Color.BLACK);
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData lineData = new LineData(strings, dataSets);

        // set data
        mChart.setData(lineData);
        mChart.invalidate();

        mChart.setDescription(null);
        mChart.getLegend().setEnabled(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
        mChart.getAxisRight().setEnabled(false);


        MyMarkerView mv = new MyMarkerView(App.getContext(), R.layout.custom_marker_view, strings);
        mChart.setMarkerView(mv);
    }
    */
}
