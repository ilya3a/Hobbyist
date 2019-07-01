package com.yoyo.hobbyist;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.himanshurawat.imageworker.Extension;
import com.himanshurawat.imageworker.ImageWorker;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.SignFragments.NewSignInScreenFragment;

import com.yoyo.hobbyist.SignFragments.SignUpFragment;

import com.yoyo.hobbyist.SignFragments.UpdateUserProfileFragment;
import com.yoyo.hobbyist.Utilis.DataStore;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.security.AccessController.getContext;

public class LoginSignUpActivity extends AppCompatActivity implements SignUpFragment.SignUpFragmentListener,
        NewSignInScreenFragment.LoginFragmentListener, UpdateUserProfileFragment.UpdateUserProfileFragmentListener {

    final int CAMERA_REQUEST = 1;
    final int WRITE_TO_EXTERNAL_PERMISSION_REQUEST = 2;
    private final int SELECT_IMAGE_REQ = 3;

    final String NEW_LOGIN_FRAGMENT_TAG = "new_sign_in_fragment";

    final String LOGIN_FRAGMENT_TAG = "sign_in_fragment";
    final String SIGN_UP_FRAGMENT_TAG = "sign_up_fragment";
    final String SING_BUTTONS_FRAGMENT_TAG = "sign_buttons_fragment";
    final String UPDATE_USER_FRAGMENT_TAG = "update_user_fragment";

    FirebaseAuth mFireBaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseUser mCurrentUser;

    FragmentManager mFragmentManager;
    //SigninFragment mLoginFragment;
    SignUpFragment mSignUpFragment;
    LottieAnimationView mLottieAnimationView;
    NewSignInScreenFragment mNewSignInScreenFragment;
    UpdateUserProfileFragment mUpdateUserProfileFragment;

    SharedPreferences mSp;
    SharedPreferences.Editor mSpEditor;

    String mEmail;
    String mPassword;

    CoordinatorLayout mCoordinatorLayout;
    UserProfile mExistingUser;

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
        mCurrentUser = mFireBaseAuth.getCurrentUser();

        mExistingUser = DataStore.getInstance(this).getUser();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };

        if (mCurrentUser != null && !(mCurrentUser.getDisplayName().equals("null"))) {

            goToMainActivity(mExistingUser.getmHobbylist().toArray(new String[mExistingUser.getmHobbylist().size()]));
        } else {
            mFireBaseAuth.signOut();
            mCurrentUser = mFireBaseAuth.getCurrentUser();
            mUpdateUserProfileFragment = new UpdateUserProfileFragment();
            mLottieAnimationView = findViewById(R.id.lottie_animation);
            mLottieAnimationView.setAnimation(R.raw.animation_test);
            mFragmentManager = getSupportFragmentManager();
            //mLoginFragment = new SigninFragment();
            mSignUpFragment = new SignUpFragment();
            mNewSignInScreenFragment=new NewSignInScreenFragment();
            //add the new mainlogin
            mFragmentManager.beginTransaction().add(R.id.main_container, mNewSignInScreenFragment, NEW_LOGIN_FRAGMENT_TAG)
                    .commit();
        }
    }

    private void goToMainActivity(String[] hobbies) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("subs",hobbies);
        startActivity(intent);
        finish();
    }

    @Override
    public void afterSignInUserUpdate(final FirebaseUser user) {
        if (user.getDisplayName().equals("null")) {
            callUpdateUser();
        } else {
            mCurrentUser = user;
            mLottieAnimationView.setVisibility(View.VISIBLE);
            mLottieAnimationView.playAnimation();
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    mFragmentManager.beginTransaction().commit();
                    mLottieAnimationView.setVisibility(View.GONE);
                    goToMainActivity( mExistingUser.getmHobbylist().toArray(new String[mExistingUser.getmHobbylist().size()]));
                }
            };
            handler.postDelayed(runnable, 3100);
        }
    }

    @Override
    public void callSignUp() {
        mFragmentManager.beginTransaction().add(R.id.main_container, mSignUpFragment, SIGN_UP_FRAGMENT_TAG)
                       .addToBackStack(null).commit();
    }

    @Override
    public void afterSignUpUserUpdate(FirebaseUser user) {
        mCurrentUser = user;
        //goToMainActivity();
        mFragmentManager.beginTransaction().add(R.id.main_container, mUpdateUserProfileFragment,
                UPDATE_USER_FRAGMENT_TAG).addToBackStack(null).commit();
        //mFragmentManager.beginTransaction().remove(mSignUpFragment).commit();
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
    public void afterUpdateUserUpdate(Boolean isUserUpdated, String[] hobbies) {
        if (isUserUpdated) {
            goToMainActivity(hobbies);
        }
        if (!isUserUpdated) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, 1);
            }

        }
    }

    void callUpdateUser() {
        mFragmentManager.beginTransaction().add(R.id.main_container, mUpdateUserProfileFragment,
                UPDATE_USER_FRAGMENT_TAG)
                .addToBackStack(null).commit();
    }

    @Override
    public void updateImage(Intent intent, View view) {
        if(Build.VERSION.SDK_INT>=23) {
                callPermissions(intent);
        }
        else {
            startActivityForResult(intent, CAMERA_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Fragment fragment =mFragmentManager.findFragmentByTag(UPDATE_USER_FRAGMENT_TAG);
        assert fragment != null;
        ((UpdateUserProfileFragment) fragment).updateUserImage();
    }
    public void callPermissions(final Intent intent) {
        String[] string = { Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle("Info")
                .setSettingsDialogTitle("Warning");
        PermissionHandler permissionHandler = new PermissionHandler() {
            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
                callPermissions(intent);
            }

            @Override
            public void onGranted() {
                startActivityForResult(intent, 1);
            }
        };
        Permissions.check(this, string, "you must give those permissions to take a photo",options, permissionHandler);


    }

}
