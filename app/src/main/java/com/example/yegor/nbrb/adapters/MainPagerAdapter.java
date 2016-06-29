package com.example.yegor.nbrb.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.fragments.CurrentRatesFragment;
import com.example.yegor.nbrb.fragments.RateByDateFragment;
import com.example.yegor.nbrb.fragments.RatesGraphicFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    private String[] tabTitles;
    private int[] imageResId;

    public MainPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        this.context = context;

        tabTitles = context.getResources().getStringArray(R.array.tab_text);
        TypedArray imgs = context.getResources().obtainTypedArray(R.array.tab_icon);

        int length = imgs.length();
        imageResId = new int[length];

        for (int i = 0; i < length; i++)
            imageResId[i] = imgs.getResourceId(i, 0);

        imgs.recycle();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                return RateByDateFragment.newInstance();

            case 1:
                return CurrentRatesFragment.newInstance();

            case 2:
                return RatesGraphicFragment.newInstance();

        }

        return null;

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        Drawable image = context.getResources().getDrawable(imageResId[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        // Replace blank spaces with image icon
        SpannableString sb = new SpannableString("   " + tabTitles[position]);
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

}