package com.yoyo.hobbyist;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.yoyo.hobbyist.Adapters.PagerAdapter;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.Fragments.ChatFragment;
import com.yoyo.hobbyist.Fragments.CreatePostFragment;
import com.yoyo.hobbyist.Fragments.DashboardFragment;
import com.yoyo.hobbyist.Fragments.MenuFragment;
import com.yoyo.hobbyist.Fragments.SearchFragment;
import com.yoyo.hobbyist.Utilis.DataStore;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements DashboardFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener, ChatFragment.OnFragmentInteractionListener,
        MenuFragment.OnFragmentInteractionListener, CreatePostFragment.OnFragmentInteractionListener {

    //    Toolbar mToolbar;
    TabLayout mTabLayout;
    ViewPager mPager;
    PagerAdapter mAdapter;
    TabItem tabItem1;
    TabItem tabItem2;
    TabItem tabItem3;
    TabItem tabItem4;
    FirebaseAuth mFireBaseAuth;
    UserProfile mUserProfile;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser mFireBaseUser;
    DataStore mDataStore;
    //    LottieAnimationView mSwipeLeftLottie, mSwipeRightLottie;
    FloatingActionButton mFab;
    FragmentManager mFragmentManager;
    final String CREATE_POST_FRAGMENT_TAG = "create_post_fragment_tag";

    final String API_TOKEN_KEY = "";
    protected static boolean isVisible = true;
    FirebaseMessaging topicMessegingAlert = FirebaseMessaging.getInstance();


    public static void setIsVisible(boolean isVisible) {
        MainActivity.isVisible = isVisible;
    }

    public static boolean isIsVisible() {
        return isVisible;
    }

    @Override
    protected void onPause() {
        super.onPause();
        setIsVisible(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setIsVisible(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserProfile = DataStore.getInstance(this).getUser();
        for (String sub : mUserProfile.getmHobbylist()){
            topicMessegingAlert.subscribeToTopic(sub);

        }

        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();
        mFireBaseAuth = FirebaseAuth.getInstance();
        mFireBaseUser = mFireBaseAuth.getCurrentUser();
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                mFragmentManager = getSupportFragmentManager();
                DialogFragment dialogFragment = new CreatePostFragment();
                dialogFragment.show(mFragmentManager, CREATE_POST_FRAGMENT_TAG);

                getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialogFragment.setCancelable(false);
            }
        });

//        Button buttonLogOut = findViewById(R.id.logout);
//        buttonLogOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mFireBaseAuth.signOut();
//               Intent  intent = new Intent(MainActivity.this,LoginSignUpActivity.class);
//               startActivity(intent);
//            }
//        });

        mTabLayout = findViewById(R.id.tab_layout);

        tabItem1 = findViewById(R.id.dashboard);
        tabItem2 = findViewById(R.id.search);
        tabItem3 = findViewById(R.id.chat);
        tabItem4 = findViewById(R.id.menu);
        mPager = findViewById(R.id.pager);
//        mSwipeLeftLottie = findViewById( R.id.swipe_left );
//        mSwipeLeftLottie.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                mPager.setCurrentItem( mTabLayout.getSelectedTabPosition() - 1 );
//            }
//        } );
//        mSwipeRightLottie =
//
//                findViewById( R.id.swipe_right );
//        mSwipeRightLottie.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPager.setCurrentItem( mTabLayout.getSelectedTabPosition() + 1 );
//            }
//        } );


        mAdapter = new

                PagerAdapter(getSupportFragmentManager(), mTabLayout.

                getTabCount());
        mPager.setAdapter(mAdapter);
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        }

        ;
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int itemPos = tab.getPosition();
                mPager.setCurrentItem(itemPos);


                if (itemPos == 0) {
//                    mSwipeLeftLottie.setVisibility( View.GONE );
//                    mSwipeRightLottie.setVisibility( View.VISIBLE );
                    mTabLayout.getTabAt(0).setIcon(R.drawable.ic_dashboard_icon_selected);
                    mTabLayout.getTabAt(1).setIcon(R.drawable.ic_loupe_icon);
                    mTabLayout.getTabAt(2).setIcon(R.drawable.ic_chat_icon);
                    mTabLayout.getTabAt(3).setIcon(R.drawable.ic_menu_icon);


                } else if (itemPos == 1) {
//                    mSwipeLeftLottie.setVisibility( View.VISIBLE );
//                    mSwipeRightLottie.setVisibility( View.VISIBLE );
                    mTabLayout.getTabAt(0).setIcon(R.drawable.ic_dashboard_icon);
                    mTabLayout.getTabAt(1).setIcon(R.drawable.ic_loupe_icon_selected);
                    mTabLayout.getTabAt(2).setIcon(R.drawable.ic_chat_icon);
                    mTabLayout.getTabAt(3).setIcon(R.drawable.ic_menu_icon);

                } else if (itemPos == 2) {
//                    mSwipeLeftLottie.setVisibility( View.VISIBLE );
//                    mSwipeRightLottie.setVisibility( View.VISIBLE );
                    mTabLayout.getTabAt(0).setIcon(R.drawable.ic_dashboard_icon);
                    mTabLayout.getTabAt(1).setIcon(R.drawable.ic_loupe_icon);
                    mTabLayout.getTabAt(2).setIcon(R.drawable.ic_chat_icon_selected);
                    mTabLayout.getTabAt(3).setIcon(R.drawable.ic_menu_icon);

                } else {
//                    mSwipeLeftLottie.setVisibility( View.VISIBLE );
//                    mSwipeRightLottie.setVisibility( View.GONE );
                    mTabLayout.getTabAt(0).setIcon(R.drawable.ic_dashboard_icon);
                    mTabLayout.getTabAt(1).setIcon(R.drawable.ic_loupe_icon);
                    mTabLayout.getTabAt(2).setIcon(R.drawable.ic_chat_icon);
                    mTabLayout.getTabAt(3).setIcon(R.drawable.ic_menu_icon_selected);

                }
            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        //todo: wtf???? why to get users here???
//        getUserProfiles();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMapCreate() {

    }

    interface MainActivityDashboardFragmentDataPass {
        void setData(String data);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFireBaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFireBaseAuth.removeAuthStateListener(mAuthStateListener);
    }
//todo: wtf???
    public void getUserProfiles() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("appUsers").child(mFireBaseUser.getUid());
        Query usersQuery = mDatabaseReference.orderByKey();

        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserProfile = dataSnapshot.getValue(UserProfile.class);
                mDataStore = DataStore.getInstance(getApplicationContext());
                mDataStore.saveUser(mUserProfile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}

