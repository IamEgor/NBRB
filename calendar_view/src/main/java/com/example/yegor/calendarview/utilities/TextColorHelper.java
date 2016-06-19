package com.example.yegor.calendarview.utilities;

import android.content.res.ColorStateList;

public class TextColorHelper {

    public static ColorStateList resolveMaterialHeaderTextColor() {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_activated},
                new int[]{}
        };

        int[] colors = new int[]{
                SUtils.COLOR_TEXT_PRIMARY,
                SUtils.COLOR_TEXT_SECONDARY
        };

        return new ColorStateList(states, colors);
    }
}