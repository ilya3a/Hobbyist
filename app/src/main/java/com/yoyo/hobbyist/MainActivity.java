package com.yoyo.hobbyist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yoyo.hobbyist.Adapters.PagerAdapter;
import com.yoyo.hobbyist.Adapters.PostsRecyclerViewAdapter;
import com.yoyo.hobbyist.Adapters.UserAdapter;
import com.yoyo.hobbyist.DataModels.UserPost;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.Fragments.CreatePostFragment;
import com.yoyo.hobbyist.Fragments.DashboardFragment;
import com.yoyo.hobbyist.Fragments.MenuFragment;
import com.yoyo.hobbyist.Fragments.MessageFragment;
import com.yoyo.hobbyist.Fragments.PostFragmentForMap;
import com.yoyo.hobbyist.Fragments.ProfilePageFragment;
import com.yoyo.hobbyist.Fragments.SearchFragment;
import com.yoyo.hobbyist.Utilis.DataStore;

import java.util.ArrayList;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;


public class MainActivity extends AppCompatActivity implements DashboardFragment.OnFragmentInteractionListener,
        SearchFragment.onSearchFragmentListener, MenuFragment.OnFragmentInteractionListener, UserAdapter.RecyclerCallBack,
        CreatePostFragment.OnFragmentInteractionListener, ProfilePageFragment.ProfileFragmentListener, PostsRecyclerViewAdapter.RecyclerCallBack, PostFragmentForMap.OnPostForMapListener {

    final int CAMERA_REQUEST = 1;
    final String MESSAGE_FRAGMENT_TAG = "message_fragment_tag";
    final String POST_FRAGMENT_FOR_MAP = "post_fragment_for_map";
    TabLayout mTabLayout;
    ViewPager mPager;
    PagerAdapter mAdapter;
    TabItem tabItem1;
    TabItem tabItem2;
    TabItem tabItem3;
    TabItem tabItem4;
    PostFragmentForMap mPostFragmentForMap;
    FirebaseAuth mFireBaseAuth;
    UserProfile mUserProfile;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    FirebaseUser mFireBaseUser;
    FloatingActionButton mFab;
    FragmentManager mFragmentManager;
    FirebaseMessaging topicMessegingAlert = FirebaseMessaging.getInstance();
    final String CREATE_POST_FRAGMENT_TAG = "create_post_fragment_tag";

    @Override
    public void logOut() {
        mFireBaseAuth.signOut();
        Intent intent = new Intent( this, LoginSignUpActivity.class );
        startActivity( intent );
        finish();
    }

    @Override
    public void notifCheckChange(boolean isChecked) {
        DataStore.getInstance( this ).setNotifOk( isChecked );
        if (isChecked) {
            mUserProfile = DataStore.getInstance( this ).getUser();
            if (mUserProfile != null) {
                for (String sub : mUserProfile.getmHobbylist()) {
                    topicMessegingAlert.subscribeToTopic( sub.replace( " ", "" ) );

                }
            }
        } else {
            mUserProfile = DataStore.getInstance( this ).getUser();
            if (mUserProfile != null) {
                for (String sub : mUserProfile.getmHobbylist()) {
                    topicMessegingAlert.unsubscribeFromTopic( sub.replace( " ", "" ) );

                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        if (DataStore.getInstance( this ).isNotifOk()) {
            mUserProfile = DataStore.getInstance( this ).getUser();
            if (mUserProfile != null) {
                for (String sub : mUserProfile.getmHobbylist()) {
                    topicMessegingAlert.subscribeToTopic( sub.replace( " ", "" ) );

                }
            }
        }

        setContentView( R.layout.activity_main );
        mFragmentManager = getSupportFragmentManager();
        mFireBaseAuth = FirebaseAuth.getInstance();
        mFireBaseUser = mFireBaseAuth.getCurrentUser();
        mFab = findViewById( R.id.fab );
        mFab.setOnClickListener( new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                mFragmentManager = getSupportFragmentManager();
                DialogFragment dialogFragment = new CreatePostFragment();
                dialogFragment.show( mFragmentManager, CREATE_POST_FRAGMENT_TAG );
            }
        } );


        mTabLayout = findViewById( R.id.tab_layout );

        tabItem1 = findViewById( R.id.dashboard );
        tabItem2 = findViewById( R.id.search );
        tabItem3 = findViewById( R.id.chat );
        tabItem4 = findViewById( R.id.menu );
        mPager = findViewById( R.id.pager );

        mAdapter = new PagerAdapter( getSupportFragmentManager(), mTabLayout.

                getTabCount() );
        mPager.setAdapter( mAdapter );
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        }

        ;
        mTabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int itemPos = tab.getPosition();
                mPager.setCurrentItem( itemPos );


                if (itemPos == 0) {
//                    mSwipeLeftLottie.setVisibility( View.GONE );
//                    mSwipeRightLottie.setVisibility( View.VISIBLE );
                    mTabLayout.getTabAt( 0 ).setIcon( R.drawable.ic_dashboard_icon_selected );
                    mTabLayout.getTabAt( 1 ).setIcon( R.drawable.ic_loupe_icon );
                    mTabLayout.getTabAt( 2 ).setIcon( R.drawable.ic_chat_icon );
                    mTabLayout.getTabAt( 3 ).setIcon( R.drawable.ic_menu_icon );
                    mFab.setVisibility( View.VISIBLE );


                } else if (itemPos == 1) {
//                    mSwipeLeftLottie.setVisibility( View.VISIBLE );
//                    mSwipeRightLottie.setVisibility( View.VISIBLE );
                    mTabLayout.getTabAt( 0 ).setIcon( R.drawable.ic_dashboard_icon );
                    mTabLayout.getTabAt( 1 ).setIcon( R.drawable.ic_loupe_icon_selected );
                    mTabLayout.getTabAt( 2 ).setIcon( R.drawable.ic_chat_icon );
                    mTabLayout.getTabAt( 3 ).setIcon( R.drawable.ic_menu_icon );
                    mFab.setVisibility( View.VISIBLE );

                } else if (itemPos == 2) {
//                    mSwipeLeftLottie.setVisibility( View.VISIBLE );
//                    mSwipeRightLottie.setVisibility( View.VISIBLE );
                    mTabLayout.getTabAt( 0 ).setIcon( R.drawable.ic_dashboard_icon );
                    mTabLayout.getTabAt( 1 ).setIcon( R.drawable.ic_loupe_icon );
                    mTabLayout.getTabAt( 2 ).setIcon( R.drawable.ic_chat_icon_selected );
                    mTabLayout.getTabAt( 3 ).setIcon( R.drawable.ic_menu_icon );
                    mFab.setVisibility( View.VISIBLE );
                } else {
//                    mSwipeLeftLottie.setVisibility( View.VISIBLE );
//                    mSwipeRightLottie.setVisibility( View.GONE );
                    mTabLayout.getTabAt( 0 ).setIcon( R.drawable.ic_dashboard_icon );
                    mTabLayout.getTabAt( 1 ).setIcon( R.drawable.ic_loupe_icon );
                    mTabLayout.getTabAt( 2 ).setIcon( R.drawable.ic_chat_icon );
                    mTabLayout.getTabAt( 3 ).setIcon( R.drawable.ic_menu_icon_selected );
                    mFab.setVisibility( View.GONE );

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


    @Override
    public void updateImage(Intent intent, View view) {
        if (Build.VERSION.SDK_INT >= 23) {
            callPermissions( intent );
        } else {
            startActivityForResult( intent, CAMERA_REQUEST );
        }
    }


    @Override
    public void onItemClicked(String userId) {
        MessageFragment messageFragment = MessageFragment.newInstance( userId );
        //fragment for chat getting user id
        mFragmentManager.beginTransaction().add( R.id.main_layout, messageFragment, MESSAGE_FRAGMENT_TAG ).addToBackStack( null ).commit();
    }


    @Override
    public void onMarkerInfoClicked(UserPost userPost) {

        mPostFragmentForMap = new PostFragmentForMap( userPost.getUserProfilePicUrl(), userPost.getUserName(), userPost.getCityName(),
                userPost.getPostDescription(), userPost.getHobby(), userPost.getDate() );

        PostFragmentForMap postFragmentForMap = (PostFragmentForMap) mFragmentManager.findFragmentByTag( POST_FRAGMENT_FOR_MAP );

        if (postFragmentForMap == null) {
            mFragmentManager.beginTransaction().add( R.id.post_info_container, mPostFragmentForMap, POST_FRAGMENT_FOR_MAP ).addToBackStack( null ).commit();
        } else {

            mFragmentManager.beginTransaction().remove( postFragmentForMap ).commit();
            mFragmentManager.beginTransaction().add( R.id.post_info_container, mPostFragmentForMap, POST_FRAGMENT_FOR_MAP ).addToBackStack( null ).commit();
        }

    }

    @Override
    public void onSwishedTab() {
        if (mFragmentManager.findFragmentByTag( POST_FRAGMENT_FOR_MAP ) != null) {
            mFragmentManager.beginTransaction().remove( mPostFragmentForMap ).commit();
            for(int i = 0; i < mFragmentManager.getBackStackEntryCount(); ++i) {
                mFragmentManager.popBackStack();
            }
        }
    }

    @Override
    public void onChatBtnClicked(String userId) {
        MessageFragment messageFragment = MessageFragment.newInstance( userId );
        //fragment for chat getting user id
        mFragmentManager.beginTransaction().add( R.id.main_layout, messageFragment, MESSAGE_FRAGMENT_TAG ).addToBackStack( null ).commit();

    }

    interface MainActivityDashboardFragmentDataPass {
        void setData(String data);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFireBaseAuth.addAuthStateListener( mAuthStateListener );
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFireBaseAuth.removeAuthStateListener( mAuthStateListener );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == CAMERA_REQUEST) {
                String filePath = data.getStringExtra( ImageSelectActivity.RESULT_FILE_PATH );
                Fragment fragment = mAdapter.getItem( 3 );
                ((ProfilePageFragment) fragment).updateUserImage( filePath );
            }
        }


    }

    public void callPermissions(final Intent intent) {
        String[] string = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle( "Info" )
                .setSettingsDialogTitle( "Warning" );
        PermissionHandler permissionHandler = new PermissionHandler() {
            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied( context, deniedPermissions );
                callPermissions( intent );
            }

            @Override
            public void onGranted() {
                startActivityForResult( intent, 1 );
            }
        };
        Permissions.check( this, string, "you must give those permissions to take a photo", options, permissionHandler );


    }


}

