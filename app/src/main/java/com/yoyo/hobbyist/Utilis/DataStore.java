package com.yoyo.hobbyist.Utilis;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yoyo.hobbyist.DataModels.UserPost;
import com.yoyo.hobbyist.DataModels.UserProfile;

import java.util.ArrayList;

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
        mSharedPref = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        mEditor = mSharedPref.edit();
    }

    public SharedPreferences getSharedPref() {
        return mSharedPref;
    }

    public  void saveCurrenttalkingUser(String userId){
        mEditor.putString("talk_to", userId);
        mEditor.apply();
    }
    public  String getCurrenttalkingUser(){
        return mSharedPref.getString("talk_to", "none");
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
    public void saveUserPostList(ArrayList<UserPost> posts) {
        String postString = mGson.toJson(posts);
        mEditor.putString("posts", postString);
        mEditor.apply();
    }
    public ArrayList<UserPost> getPostList() {

        String userPosts = mSharedPref.getString("posts", null);
        return mGson.fromJson(userPosts,new TypeToken<ArrayList<UserPost>>(){}.getType());
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

    public boolean isNotifOk(){
        return  mSharedPref.getBoolean("notif", true);
    }
    public void setNotifOk(boolean ok){
        mEditor.putBoolean("notif",ok).commit();
    }
}
