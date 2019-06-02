package com.yoyo.hobbyist.DataModels;

import java.util.ArrayList;

public class UserProfile {

    String mName;
    String mAge;
    String mLastName;
    String mEmail;
    String mPictureUrl;
    String mUserAboutMe;
    String mCityName;
    ArrayList<String> mHobbylist;

    public UserProfile(String mName, String mAge, String mLastName, String mEmail, String mPictureUrl, String mCityName, ArrayList<String> mHobbylist) {
        this.mName = mName;
        this.mAge = mAge;
        this.mLastName = mLastName;
        this.mEmail = mEmail;
        this.mPictureUrl = mPictureUrl;
        this.mCityName = mCityName;
        this.mHobbylist = mHobbylist;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmAge() {
        return mAge;
    }

    public void setmAge(String mAge) {
        this.mAge = mAge;
    }

    public String getmLastName() {
        return mLastName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPictureUrl() {
        return mPictureUrl;
    }

    public void setmPictureUrl(String mPictureUrl) {
        this.mPictureUrl = mPictureUrl;
    }

    public String getmUserAboutMe() {
        return mUserAboutMe;
    }

    public void setmUserAboutMe(String mUserAboutMe) {
        this.mUserAboutMe = mUserAboutMe;
    }

    public String getmCityName() {
        return mCityName;
    }

    public void setmCityName(String mCityName) {
        this.mCityName = mCityName;
    }

    public ArrayList<String> getmHobbylist() {
        return mHobbylist;
    }

    public void setmHobbylist(ArrayList<String> mHobbylist) {
        this.mHobbylist = mHobbylist;
    }
}
