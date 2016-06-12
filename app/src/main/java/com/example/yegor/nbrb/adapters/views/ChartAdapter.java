package com.example.yegor.nbrb.adapters.views;

import com.example.yegor.nbrb.utils.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.List;

public class ChartAdapter implements OnChartValueSelectedListener {

    private List<String> dates;
    private OnChartSelect onSelect;

    public ChartAdapter(LineChart chart, OnChartSelect onSelect) {
        this.onSelect = onSelect;

        chart.setOnChartValueSelectedListener(this);
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        onSelect.onChartValueSelected(String.valueOf(e.getVal()), dates.get(e.getXIndex()));
        Utils.logT("[ChartAdapter]", "Entry = ", " " + e.getData(), " " + e.getVal(), " " + e.getXIndex());
        Utils.logT("[ChartAdapter]", "date = " + dates.get(e.getXIndex()));
    }

    @Override
    public void onNothingSelected() {

    }

    public interface OnChartSelect {
        void onChartValueSelected(String rate, String date);
    }

}
