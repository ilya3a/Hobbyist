package com.yoyo.hobbyist;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SignUpFragment.SignUpFragmentListener, LoginFragment.LoginFragmentListener {

    final String LOGIN_FRAGMENT_TAG = "login_fragment";
    final String SIGN_UP_FRAGMENT_TAG = "sign_up_fragment";

    FirebaseAuth mFireBaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser mCurrentUser;

    FragmentManager mFragmentManager;
    LoginFragment mLoginFragment;
    SignUpFragment mSignUpFragment;
    LottieAnimationView mLottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mFireBaseAuth = FirebaseAuth.getInstance();
        Button mLoginBtn = findViewById(R.id.login_btn);
        final Button mSignUpBtn = findViewById(R.id.sign_up_btn);

        mLottieAnimationView = findViewById(R.id.lottie_animation);
        mLottieAnimationView.setAnimation(R.raw.animation_test);
        mFragmentManager = getSupportFragmentManager();
        mLoginFragment = new LoginFragment();
        mSignUpFragment = SignUpFragment.newInstance().newInstance();
        mCurrentUser = mFireBaseAuth.getCurrentUser();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentManager.beginTransaction().add(R.id.main_container, mLoginFragment, LOGIN_FRAGMENT_TAG)
                        .addToBackStack(LOGIN_FRAGMENT_TAG).commit();
            }
        });

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentManager.beginTransaction().add(R.id.main_container, mSignUpFragment, SIGN_UP_FRAGMENT_TAG)
                        .addToBackStack(SIGN_UP_FRAGMENT_TAG).commit();
            }
        });
    }

    @Override
    public void userUpdate(final FirebaseUser user) {
        mCurrentUser = user;
        mLottieAnimationView.setVisibility(View.VISIBLE);
        mLottieAnimationView.playAnimation();
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                mFragmentManager.beginTransaction().remove(mLoginFragment).commit();
                mLottieAnimationView.setVisibility(View.GONE);
            }
        };
        handler.postDelayed(runnable, 3100);
    }

    @Override
    public void registerdUser(FirebaseUser user) {
        mCurrentUser = user;

        mFragmentManager.beginTransaction().remove(mSignUpFragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

}
