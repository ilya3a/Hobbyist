package com.yoyo.hobbyist.Utilis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.yoyo.hobbyist.DataModels.UserPost;
import com.yoyo.hobbyist.DataModels.UserProfile;

public class DataStore {

    private static DataStore sInstance;
    private static final String PREFS = "sharedPref";
    private static final String SHARED_KEY_NEW_USER = "shared_key_new_user";
    private static final String SHARED_KEY_NEW_POST = "shared_key_new_post";

    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mEditor;
    private Gson mGson = new Gson();


    public synchronized static DataStore getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataStore(context);
        }
        return sInstance;
    }

    @SuppressLint("CommitPrefEdits")
    private DataStore(Context context) {
        mSharedPref = context.getSharedPreferences(PREFS, context.MODE_PRIVATE);
        mEditor = mSharedPref.edit();
    }

    public SharedPreferences getSharedPref() {
        return mSharedPref;
    }

    public void saveUser(UserProfile user) {
        String userString = mGson.toJson(user);
        mEditor.putString(SHARED_KEY_NEW_USER, userString);
        mEditor.apply();
    }

    public void saveUserPost(UserPost post) {
        String postString = mGson.toJson(post);
        mEditor.putString(SHARED_KEY_NEW_POST, postString);
        mEditor.apply();
    }

    public void clearAllData() {
        mEditor.clear();
        mEditor.commit();
    }

    public UserProfile getUser() {

        String userJson = mSharedPref.getString(SHARED_KEY_NEW_USER, "");
        if (userJson.equals("")) {

            return null;
        }
        return mGson.fromJson(userJson, UserProfile.class);
    }

    public UserPost getPost() {

        String userPost = mSharedPref.getString(SHARED_KEY_NEW_POST, "");
        return mGson.fromJson(userPost, UserPost.class);
    }
}
