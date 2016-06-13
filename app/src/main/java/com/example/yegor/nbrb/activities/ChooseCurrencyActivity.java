package com.example.yegor.nbrb.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.fragments.ChooseCurrencyFragment;

public class ChooseCurrencyActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ChooseCurrencyFragment.newInstance(null))
                    .commit();
        }

    }

}
