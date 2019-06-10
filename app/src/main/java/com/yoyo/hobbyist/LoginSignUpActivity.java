package com.yoyo.hobbyist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yoyo.hobbyist.SignFragments.SignButtonsFragment;
import com.yoyo.hobbyist.SignFragments.SignUpFragment;
import com.yoyo.hobbyist.SignFragments.SigninFragment;
import com.yoyo.hobbyist.SignFragments.UpdateUserProfileFragment;

public class LoginSignUpActivity extends AppCompatActivity implements SignUpFragment.SignUpFragmentListener,
        SignButtonsFragment.OnFragmentInteractionListener, SigninFragment.LoginFragmentListener {

    final String LOGIN_FRAGMENT_TAG = "sign_in_fragment";
    final String SIGN_UP_FRAGMENT_TAG = "sign_up_fragment";
    final String SING_BUTTONS_FRAGMENT_TAG = "sign_buttons_fragment";
    final String UPDATE_USER_FRAGMENT_TAG="update_user_fragment";

    FirebaseAuth mFireBaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser mCurrentUser;

    FragmentManager mFragmentManager;
    SigninFragment mLoginFragment;
    SignUpFragment mSignUpFragment;
    LottieAnimationView mLottieAnimationView;
    SignButtonsFragment mSignBottnsFragment;
    UpdateUserProfileFragment mUpdateUserProfileFragment;

    SharedPreferences mSp;
    SharedPreferences.Editor mSpEditor;

    String mEmail;
    String mPassword;

    CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);
        mCoordinatorLayout = findViewById(R.id.login_signup_coordinatorLayout);
        mSp = getApplicationContext().getSharedPreferences("login", MODE_PRIVATE);
        mSpEditor = mSp.edit();
        mFireBaseAuth = FirebaseAuth.getInstance();
        mEmail = mSp.getString("userEmail", "noEmail");
        mPassword = mSp.getString("userPassword", "noPassword");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };

        if (!mEmail.equals("noEmail") && !mPassword.equals("noPassword")) {
            logIn();
            goToMainActivity();

        } else {
            mUpdateUserProfileFragment =new UpdateUserProfileFragment();
            mLottieAnimationView = findViewById(R.id.lottie_animation);
            mLottieAnimationView.setAnimation(R.raw.animation_test);
            mFragmentManager = getSupportFragmentManager();
            mLoginFragment = new SigninFragment();
            mSignUpFragment = new SignUpFragment();
            //mCurrentUser = mFireBaseAuth.getCurrentUser();
            mSignBottnsFragment = new SignButtonsFragment();

            mFragmentManager.beginTransaction().add(R.id.main_container, mSignBottnsFragment, SING_BUTTONS_FRAGMENT_TAG)
                    .addToBackStack(null).commit();

        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void logIn() {
        mFireBaseAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mCurrentUser = mFireBaseAuth.getCurrentUser();
                } else {
                    Snackbar.make(mCoordinatorLayout, "Could not auto connect", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    logIn();
                                }
                            }).show();
                }
            }
        });
    }

    @Override
    public void afterSignInUserUpdate(final FirebaseUser user) {
        mCurrentUser = user;
        mLottieAnimationView.setVisibility(View.VISIBLE);
        mLottieAnimationView.playAnimation();
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                mFragmentManager.beginTransaction().remove(mLoginFragment).commit();
                mLottieAnimationView.setVisibility(View.GONE);
                goToMainActivity();
            }
        };
        handler.postDelayed(runnable, 3100);
    }

    @Override
    public void afterSignUpUserUpdate(FirebaseUser user) {
        mCurrentUser = user;
        goToMainActivity();
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

    @Override
    public void SignButtonsOnClick(int btnId) {
        switch (btnId) {
            case R.id.sign_in_btn: {
                mFragmentManager.beginTransaction().remove(mSignBottnsFragment).add(R.id.main_container, mLoginFragment, LOGIN_FRAGMENT_TAG)
                        .addToBackStack(null).commit();
                break;
            }
            case R.id.sign_up_btn: {
                mFragmentManager.beginTransaction().remove(mSignBottnsFragment).add(R.id.main_container, mSignUpFragment, SIGN_UP_FRAGMENT_TAG)
                        .addToBackStack(null).commit();
//                mFragmentManager.beginTransaction().remove(mSignBottnsFragment).add(R.id.main_container, mUpdateUserProfileFragment,
//                        UPDATE_USER_FRAGMENT_TAG)
//                        .addToBackStack(null).commit();
                //FOR TEST
                break;
            }
        }
    }
}
