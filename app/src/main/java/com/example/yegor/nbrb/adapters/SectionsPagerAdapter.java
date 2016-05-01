package com.example.yegor.nbrb.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.yegor.nbrb.fragments.CurrentRatesFragment;
import com.example.yegor.nbrb.fragments.RateByDateFragment;
import com.example.yegor.nbrb.fragments.RatesGraphicFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
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
        switch (position) {
            case 0:
                return "By date";
            case 1:
                return "Current rates";
            case 2:
                return "Graphic";
        }
        return null;
    }
}