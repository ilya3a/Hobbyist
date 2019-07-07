package com.yoyo.hobbyist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yoyo.hobbyist.DataModels.UserProfile;
import com.yoyo.hobbyist.SignFragments.NewSignInScreenFragment;

import com.yoyo.hobbyist.SignFragments.SignUpFragment;

import com.yoyo.hobbyist.SignFragments.UpdateUserProfileFragment;
import com.yoyo.hobbyist.Utilis.DataStore;

import java.util.ArrayList;

import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

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
    FirebaseUser mFireBaseUser;

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

    DataStore mDataStore;


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
        mFireBaseUser = mFireBaseAuth.getCurrentUser();

        mExistingUser = DataStore.getInstance(this).getUser();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };

        if (mFireBaseUser != null && !mFireBaseUser.getDisplayName().equals("null") && mExistingUser != null) {

            goToMainActivity(mExistingUser.getHobbyList().toArray(new String[mExistingUser.getHobbyList().size()]));
        } else {
            mFireBaseAuth.signOut();
            mFireBaseUser = mFireBaseAuth.getCurrentUser();
            mUpdateUserProfileFragment = new UpdateUserProfileFragment();
            mLottieAnimationView = findViewById(R.id.lottie_animation);
            mLottieAnimationView.setAnimation(R.raw.animation_test);

            mFragmentManager = getSupportFragmentManager();
            //mLoginFragment = new SigninFragment();
            mSignUpFragment = new SignUpFragment();
            mNewSignInScreenFragment = new NewSignInScreenFragment();
            //add the new mainlogin
            mFragmentManager.beginTransaction().add(R.id.main_container, mNewSignInScreenFragment, NEW_LOGIN_FRAGMENT_TAG)
                    .commit();
        }
    }

    private void goToMainActivity(String[] hobbies) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("subs", hobbies);
        startActivity(intent);
        finish();
    }

    @Override
    public void afterSignInUserUpdate(final FirebaseUser user, Button login_fragment_btn,LottieAnimationView mLottieTv) {
        if (user.getDisplayName().equals("null")) {
            mLottieTv.cancelAnimation();
            login_fragment_btn.setVisibility(View.VISIBLE);
            mLottieTv.setVisibility(View.GONE);
            callUpdateUser();
        } else {
            mFireBaseUser = user;
            //mLottieAnimationView.setVisibility(View.VISIBLE);
            //mLottieAnimationView.playAnimation();
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    mFragmentManager.beginTransaction().commit();
                    mLottieAnimationView.setVisibility(View.GONE);
                    getUserProfile();
                }
            };
            handler.postDelayed(runnable, 3100);
        }
    }

    @Override
    public void callSignUp() {
        mFragmentManager.beginTransaction().remove(mNewSignInScreenFragment).add(R.id.main_container, mSignUpFragment, SIGN_UP_FRAGMENT_TAG)
                .addToBackStack(null).commit();
    }

    @Override
    public void afterSignUpUserUpdate(FirebaseUser user) {
        mFireBaseUser = user;
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
        if (Build.VERSION.SDK_INT >= 23) {
            callPermissions(intent);
        } else {
            startActivityForResult(intent, CAMERA_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == CAMERA_REQUEST) {
                String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                Fragment fragment = mFragmentManager.findFragmentByTag(UPDATE_USER_FRAGMENT_TAG);
                assert fragment != null;
                ((UpdateUserProfileFragment) fragment).updateUserImage(filePath);
            }
        }
    }

    public void callPermissions(final Intent intent) {
        String[] string = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        Permissions.Options options = new Permissions.Options()
                .setRationaleDialogTitle(getString(R.string.info) )
                .setSettingsDialogTitle(getString(R.string.warning) );
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
        Permissions.check(this, string, R.string.must_give_photo_pemission, options, permissionHandler);


    }

    public void getUserProfile() {
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("appUsers").child(mFireBaseUser.getUid());
        Query usersQuery = mDatabaseReference.orderByKey();

        usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mExistingUser = dataSnapshot.getValue(UserProfile.class);
                mDataStore = DataStore.getInstance(getApplicationContext());
                mDataStore.saveUser(mExistingUser);
                goToMainActivity(mExistingUser.getHobbyList().toArray(new String[mExistingUser.getHobbyList().size()]));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
