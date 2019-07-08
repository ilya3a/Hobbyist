package com.yoyo.hobbyist.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.yoyo.hobbyist.Fragments.ChatsFragment;
import com.yoyo.hobbyist.Fragments.SearchFragment;
import com.yoyo.hobbyist.Fragments.ProfilePageFragment;

import java.util.ArrayList;

public class PagerAdapter extends FragmentPagerAdapter {

    ProfilePageFragment profilePageFragment = new ProfilePageFragment();
    private int mNumOfTabs;
    ArrayList<String> subscriptions;


    public PagerAdapter(FragmentManager fm, int numOfTabs, ArrayList<String> subscriptions) {
        super( fm );
        this.mNumOfTabs = numOfTabs;
        this.subscriptions = subscriptions;


    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new SearchFragment();
            case 1:
                return new ChatsFragment();
            case 2:
                return profilePageFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
