package com.yoyo.hobbyist.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.yoyo.hobbyist.Fragments.ChatFragment;
import com.yoyo.hobbyist.Fragments.DashboardFragment;
import com.yoyo.hobbyist.Fragments.SearchFragment;
import com.yoyo.hobbyist.Fragments.ProfilePageFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    private int mNumOfTabs;


    public PagerAdapter(FragmentManager fm, int numOfTabs) {
        super( fm );
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new DashboardFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new ChatFragment();
            case 3:
                return new ProfilePageFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
