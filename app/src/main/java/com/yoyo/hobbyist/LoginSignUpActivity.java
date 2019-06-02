package com.yoyo.hobbyist;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginSignUpActivity extends AppCompatActivity implements SignUpFragment.SignUpFragmentListener, LoginFragment.LoginFragmentListener {

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
        setContentView(R.layout.activity_login_sign_up);

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
        mCurrentUser =user;
        mLottieAnimationView.setVisibility(View.VISIBLE);
        mLottieAnimationView.playAnimation();
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {

                mFragmentManager.beginTransaction().remove(mLoginFragment).commit();
                mLottieAnimationView.setVisibility(View.GONE);
            }
        };
        handler.postDelayed(runnable,3100);
    }

    @Override
    public void registerdUser(FirebaseUser user) {
        mCurrentUser =user;

        mFragmentManager.beginTransaction().remove(mSignUpFragment).commit();
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
