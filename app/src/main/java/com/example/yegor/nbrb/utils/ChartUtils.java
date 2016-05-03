package com.example.yegor.nbrb.utils;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.example.yegor.nbrb.App;
import com.example.yegor.nbrb.MyMarkerView;
import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.models.ExRatesDynModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public final class ChartUtils {

    public static void setUpChart(LineChart mChart, List<ExRatesDynModel> content) {

        float[] floats = new float[content.size()];
        String[] strings = new String[content.size()];

        for (int i = 0; i < content.size(); i++) {
            floats[i] = content.get(i).getRate();
            strings[i] = content.get(i).getDate();
        }

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
            Drawable drawable = ContextCompat.getDrawable(App.getContext(), R.drawable.fade_red);
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
            Drawable drawable = ContextCompat.getDrawable(App.getContext(), R.drawable.fade_red);
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

}
