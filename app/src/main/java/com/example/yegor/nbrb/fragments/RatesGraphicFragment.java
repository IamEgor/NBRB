package com.example.yegor.nbrb.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yegor.nbrb.R;

public class RatesGraphicFragment extends Fragment {

    public RatesGraphicFragment() {
    }

    public static RatesGraphicFragment newInstance() {
        return new RatesGraphicFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rates_graphic, container, false);
        return rootView;
    }
}
