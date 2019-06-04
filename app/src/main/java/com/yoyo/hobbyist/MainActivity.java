package com.yoyo.hobbyist;

import android.net.Uri;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements DashboardFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener, ChatFragment.OnFragmentInteractionListener,
        MenuFragment.OnFragmentInteractionListener {

    //    Toolbar mToolbar;
    TabLayout mTabLayout;
    ViewPager mPager;
    PagerAdapter mAdapter;
    TabItem tabItem1;
    TabItem tabItem2;
    TabItem tabItem3;
    TabItem tabItem4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

//        mToolbar = findViewById( R.id.toolbar );
//        mToolbar.setTitle( R.string.app_name );
//        setSupportActionBar( mToolbar );

        mTabLayout = findViewById( R.id.tab_layout );
        tabItem1 = findViewById( R.id.dashboard );
        tabItem2 = findViewById( R.id.search );
        tabItem3 = findViewById( R.id.chat );
        tabItem4 = findViewById( R.id.menu );
        mPager = findViewById( R.id.pager );

        mAdapter = new PagerAdapter( getSupportFragmentManager(), mTabLayout.getTabCount() );
        mPager.setAdapter( mAdapter );

        mTabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem( tab.getPosition() );

                if (tab.getPosition() == 0) {
                    mTabLayout.getTabAt( 0 ).setIcon( R.drawable.ic_dashboard_icon_selected );
                    mTabLayout.getTabAt( 1 ).setIcon( R.drawable.ic_loupe_icon );
                    mTabLayout.getTabAt( 2 ).setIcon( R.drawable.ic_chat_icon );
                    mTabLayout.getTabAt( 3 ).setIcon( R.drawable.ic_menu_icon );


                } else if (tab.getPosition() == 1) {
                    mTabLayout.getTabAt( 0 ).setIcon( R.drawable.ic_dashboard_icon );
                    mTabLayout.getTabAt( 1 ).setIcon( R.drawable.ic_loupe_icon_selected );
                    mTabLayout.getTabAt( 2 ).setIcon( R.drawable.ic_chat_icon );
                    mTabLayout.getTabAt( 3 ).setIcon( R.drawable.ic_menu_icon );

                } else if (tab.getPosition() == 2) {
                    mTabLayout.getTabAt( 0 ).setIcon( R.drawable.ic_dashboard_icon );
                    mTabLayout.getTabAt( 1 ).setIcon( R.drawable.ic_loupe_icon );
                    mTabLayout.getTabAt( 2 ).setIcon( R.drawable.ic_chat_icon_selected );
                    mTabLayout.getTabAt( 3 ).setIcon( R.drawable.ic_menu_icon );

                } else {
                    mTabLayout.getTabAt( 0 ).setIcon( R.drawable.ic_dashboard_icon );
                    mTabLayout.getTabAt( 1 ).setIcon( R.drawable.ic_loupe_icon );
                    mTabLayout.getTabAt( 2 ).setIcon( R.drawable.ic_chat_icon );
                    mTabLayout.getTabAt( 3 ).setIcon( R.drawable.ic_menu_icon_selected );

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        } );

        mPager.addOnPageChangeListener( new TabLayout.TabLayoutOnPageChangeListener( mTabLayout ) );
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
//                    mToolbar.setBackgroundColor( ContextCompat.getColor( MainActivity.this, android.R.color.holo_blue_dark) );
//                if (tab.getPosition() == 1) {
//
//                    mTabLayout.setBackgroundColor( ContextCompat.getColor( MainActivity.this, android.R.color.holo_blue_dark ) );
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        getWindow().setStatusBarColor( ContextCompat.getColor( MainActivity.this, android.R.color.holo_blue_dark ) );
//                    }
//                } else if (tab.getPosition() == 2) {
//                    mToolbar.setBackgroundColor( ContextCompat.getColor( MainActivity.this, android.R.color.darker_gray ) );
//                    mTabLayout.setBackgroundColor( ContextCompat.getColor( MainActivity.this, android.R.color.darker_gray ) );
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        getWindow().setStatusBarColor( ContextCompat.getColor( MainActivity.this, android.R.color.darker_gray ) );
//                    }
//
//                } else if (tab.getPosition() == 3) {
//                    mToolbar.setBackgroundColor( ContextCompat.getColor( MainActivity.this, android.R.color.holo_blue_bright) );
//                    mTabLayout.setBackgroundColor( ContextCompat.getColor( MainActivity.this, android.R.color.holo_blue_bright ) );
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        getWindow().setStatusBarColor( ContextCompat.getColor( MainActivity.this, android.R.color.holo_blue_bright ) );
//                    }
//
//                } else {
//                    mToolbar.setBackgroundColor( ContextCompat.getColor( MainActivity.this,android.R.color.holo_purple  ) );
//                    mTabLayout.setBackgroundColor( ContextCompat.getColor( MainActivity.this, android.R.color.holo_purple ) );
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        getWindow().setStatusBarColor( ContextCompat.getColor( MainActivity.this, android.R.color.holo_purple ) );
//                    }
