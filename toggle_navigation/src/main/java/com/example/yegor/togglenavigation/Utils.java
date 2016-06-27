package com.example.yegor.togglenavigation;

import android.util.DisplayMetrics;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

class Utils {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static int generateViewId() {

        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }


    public static void setPaddingMoreThan(View view, int dp) {

        int paddingLeft = view.getPaddingLeft();
        int paddingTop = view.getPaddingTop();
        int paddingRight = view.getPaddingRight();
        int paddingBottom = view.getPaddingBottom();

        DisplayMetrics metrics = view.getContext().getResources().getDisplayMetrics();

        view.setPadding(getMoreOrEqualsThanPx(paddingLeft, dp, metrics),
                getMoreOrEqualsThanPx(paddingTop, dp, metrics),
                getMoreOrEqualsThanPx(paddingRight, dp, metrics),
                getMoreOrEqualsThanPx(paddingBottom, dp, metrics));

    }

    private static int getMoreOrEqualsThanPx(int real, int desiredDips, DisplayMetrics metrics) {
        desiredDips = desiredDips * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return real > desiredDips ? real : desiredDips;
    }

}
