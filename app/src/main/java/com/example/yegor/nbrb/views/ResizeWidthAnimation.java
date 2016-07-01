package com.example.yegor.nbrb.views;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeWidthAnimation extends Animation {

    private int mWidth;
    private int mStartWidth;
    private View mView;

    public ResizeWidthAnimation(View view, int width) {

        mView = view;
        mWidth = width;
        mStartWidth = view.getWidth();

        setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        int newWidth = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);

        mView.getLayoutParams().width = newWidth;
        mView.requestLayout();
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

}