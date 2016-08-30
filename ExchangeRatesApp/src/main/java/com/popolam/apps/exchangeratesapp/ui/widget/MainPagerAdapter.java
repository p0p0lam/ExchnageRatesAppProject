package com.popolam.apps.exchangeratesapp.ui.widget;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.popolam.apps.exchangeratesapp.ui.fragment.RateListFragment;
import com.popolam.apps.exchangeratesapp.ui.fragment.RateListType;

/**
 * Created by user on 15.10.13.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    private static final int NUM_PAGES =2;

    final RateListFragment[] fragments = new RateListFragment[2];
    final String[] mTitles;

    public MainPagerAdapter(FragmentManager fm, String... titles) {
        super(fm);
        mTitles = titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    public RateListFragment findFragment(int position){
        return fragments[position];
    }
    @Override
    public RateListFragment getItem(int i) {
        switch (i){
            case 0:
                fragments[i] = RateListFragment.newInstance(RateListType.ASK);
                return fragments[i];
            case 1:
                fragments[i] = RateListFragment.newInstance(RateListType.BID);
                return fragments[i];
        }
        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RateListFragment fr = (RateListFragment) super.instantiateItem(container, position);
        fragments[position] = fr;
        return fr;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }


}
