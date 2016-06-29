package com.example.yegor.nbrb.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import com.example.yegor.nbrb.utils.Utils;

public class TabPagerShadowBehavior extends CoordinatorLayout.Behavior<View> {

    private Rect rect;

    public TabPagerShadowBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        rect = new Rect();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        if (dependency instanceof AppBarLayout) {
            dependency.getGlobalVisibleRect(rect);
            child.setY(rect.bottom - Utils.getStatusBarHeight());
        }

        return true;
    }


}
