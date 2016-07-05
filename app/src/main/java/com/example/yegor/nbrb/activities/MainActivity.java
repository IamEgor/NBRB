package com.example.yegor.nbrb.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.widget.TextView;

import com.example.yegor.nbrb.R;
import com.example.yegor.nbrb.adapters.MainPagerAdapter;

public class MainActivity extends BaseActivity {

    private final int[] tab_icons = {R.drawable.icon_tab_by_date_24dp,
            R.drawable.icon_tab_current_rates_24dp,
            R.drawable.icon_tab_graphic_24dp};

    private final int[] tab_labels = {R.string.main_pager_by_date,
            R.string.main_pager_current_rates,
            R.string.main_pager_graphic};

    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainPagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        TextView textView;
        for (int i = 0; i < tabLayout.getTabCount(); i++) {

            textView = new TextView(this);

            textView.setCompoundDrawablePadding(8);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setTextColor(getResources().getColorStateList(R.color.tab_text_color));

            textView.setText(tab_labels[i]);
            textView.setCompoundDrawablesWithIntrinsicBounds(tab_icons[i], 0, 0, 0);
            tabLayout.getTabAt(i).setCustomView(textView);
            //tabLayout.getTabAt(i).setIcon(R.drawable.icon_tab_by_date_24dp);
        }

        mViewPager.setCurrentItem(1);
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        mViewPager.setCurrentItem(item, smoothScroll);
    }

}
