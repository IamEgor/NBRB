package com.example.yegor.nbrb.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.fragments.ChooseCurrencyFragment;

public class ChooseCurrencyActivity extends AppCompatActivity {

    private final static int ENTER = R.anim.push_up_in;
    private final static int EXIT = R.anim.push_down_out;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        overridePendingTransition(ENTER, EXIT);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ChooseCurrencyFragment.newInstance())
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
        overridePendingTransition(ENTER, EXIT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (android.R.id.home == item.getItemId()) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
