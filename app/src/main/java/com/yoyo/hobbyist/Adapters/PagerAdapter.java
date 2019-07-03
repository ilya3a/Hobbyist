package com.yoyo.hobbyist.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.yoyo.hobbyist.Fragments.ChatsFragment;
import com.yoyo.hobbyist.Fragments.DashboardFragment;
import com.yoyo.hobbyist.Fragments.SearchFragment;
import com.yoyo.hobbyist.Fragments.ProfilePageFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    private int mNumOfTabs;
Fragment[] fragments = {new DashboardFragment(),new SearchFragment(),new ChatsFragment(),new ProfilePageFragment()};

    public PagerAdapter(FragmentManager fm, int numOfTabs) {
        super( fm );
        this.mNumOfTabs = numOfTabs;

    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return fragments[0];
            case 1:
                return fragments[1];
            case 2:
                return fragments[2];
            case 3:
                return fragments[3];
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
