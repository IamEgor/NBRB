package com.example.yegor.nbrb.views;

import android.content.Context;
import android.widget.TextView;

import com.example.yegor.nbrb.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;

public class MyMarkerView extends MarkerView {

    private TextView tvContent;
    private String[] labels;

    public MyMarkerView(Context context, int layoutResource, String[] labels) {
        super(context, layoutResource);

        this.labels = labels;
        tvContent = (TextView) findViewById(R.id.tvContent);
    }


    public MyMarkerView(Context context, int layoutResource, List<String> labels) {
        super(context, layoutResource);

        this.labels = labels.toArray(new String[labels.size()]);
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true) + "\n"
                    + labels[e.getXIndex()]);
        } else {
            tvContent.setText(Utils.formatNumber(e.getVal(), 0, true) + "\n" +
                    labels[e.getXIndex()]);
        }
    }

    @Override
    public int getXOffset(float xpos) {
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        return -getHeight();
    }

}