package com.example.yegor.nbrb.views;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.example.yegor.nbrb.utils.ChartUtils;
import com.example.yegor.nbrb.utils.Utils;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.Arrays;
import java.util.List;

public class ParcelableLineData implements Parcelable {

    public static final Parcelable.Creator<ParcelableLineData> CREATOR = new Parcelable.Creator<ParcelableLineData>() {
        @Override
        public ParcelableLineData createFromParcel(Parcel source) {
            return new ParcelableLineData(source);
        }

        @Override
        public ParcelableLineData[] newArray(int size) {
            return new ParcelableLineData[size];
        }
    };

    private String[] xVals;
    private List<Entry> yVals;

    public ParcelableLineData(String[] xVals, List<Entry> yVals) {
        this.xVals = xVals;
        this.yVals = yVals;
    }

    public ParcelableLineData(LineData lineData) {
        String[] xVals = lineData.getXVals().toArray(new String[lineData.getXVals().size()]);

        LineDataSet set = (LineDataSet) lineData.getDataSetByIndex(0);
        List<Entry> yVals = set.getYVals();

        this.xVals = xVals;
        this.yVals = yVals;
    }

    public ParcelableLineData(Parcel in) {
        this.xVals = in.createStringArray();
        this.yVals = in.createTypedArrayList(Entry.CREATOR);
    }

    public LineData getLineData() {
        Utils.logT("getLineData", "xVals = " + TextUtils.join(", ", xVals));
        Utils.logT("getLineData", "yVals = " + TextUtils.join(", ", yVals));
        Utils.logT("getLineData", "ChartUtils.getILineDataSet(yVals) = " +
                TextUtils.join(", ", ChartUtils.getILineDataSet(yVals)));

        return new LineData(xVals, ChartUtils.getILineDataSet(yVals));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(this.xVals);
        dest.writeTypedList(this.yVals);
    }

    @Override
    public String toString() {
        return "ParcelableLineData{" +
                "xVals=" + Arrays.toString(xVals) +
                ", yVals=" + yVals +
                '}';
    }

}
