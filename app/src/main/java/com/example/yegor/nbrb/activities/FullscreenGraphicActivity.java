package com.example.yegor.nbrb.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.fragments.FullscreenGraphicFragment;


public class FullscreenGraphicActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_graphic);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, FullscreenGraphicFragment.newInstance(extras))
                .commit();
    }

}
