package com.yoyo.hobbyist.DataModels;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class UserProfile implements Serializable {

    private String mName;
    private String mEmail;
    private String mLastName;
    private  String mYear;
    private  String mMonth;
    private  String mDay;
    private String mAge;
    private  String mPictureUrl;
    private  String mUserAboutMe;
    private  String mCityName;
    private  String mGender;
    private  ArrayList<String> mHobbylist;
    //transient private  ArrayList<UserPost> mUserPostList;
    private String mUserToken;


    //public ArrayList<UserPost> getmUserPostList() {
     //   return mUserPostList;
    //}

    //public void setmUserPostList(ArrayList<UserPost> mUserPostList) {
      //  this.mUserPostList = mUserPostList;
    //}



    public String getmUserToken() {
        return mUserToken;
    }

    public void setmUserToken(String mUserToken) {
        this.mUserToken = mUserToken;
    }

    public UserProfile() {
    }

    public String getmName() {
        return mName;
    }

    public UserProfile setmName(String mName) {
        this.mName = mName;        return this;

    }

    public String getmAge() {
        return mAge;
    }

    public UserProfile setmAge(String mAge) {
        this.mAge = mAge;        return this;

    }

    public String getmLastName() {
        return mLastName;
    }

    public UserProfile setmLastName(String mLastName) {
        this.mLastName = mLastName;        return this;

    }

    public String getmEmail() {
        return mEmail;
    }

    public UserProfile setmEmail(String mEmail) {
        this.mEmail = mEmail;        return this;

    }

    public String getmPictureUrl() {
        return mPictureUrl;
    }

    public UserProfile setmPictureUrl(String mPictureUrl) {
        this.mPictureUrl = mPictureUrl;        return this;

    }

    public String getmUserAboutMe() {
        return mUserAboutMe;
    }

    public UserProfile setmUserAboutMe(String mUserAboutMe) {
        this.mUserAboutMe = mUserAboutMe;        return this;

    }

    public String getmCityName() {
        return mCityName;
    }

    public UserProfile setmCityName(String mCityName) {
        this.mCityName = mCityName;        return this;

    }

    public ArrayList<String> getmHobbylist() {
        return mHobbylist;
    }

    public UserProfile setmHobbylist(ArrayList<String> mHobbylist) {
        this.mHobbylist = mHobbylist;        return this;

    }

    public String getmGender() {
        return mGender;
    }

    public UserProfile setmGender(String mGender) {
        this.mGender = mGender;        return this;

    }

    public UserProfile setmYear(String mYear) {
        this.mYear = mYear;        return this;

    }

    public UserProfile setmMonth(String mMonth) {
        this.mMonth = mMonth;        return this;

    }

    public UserProfile setmDay(String mDay) {
        this.mDay = mDay;
        return this;
    }

    public String getmYear() {
        return mYear;
    }

    public String getmMonth() {
        return mMonth;
    }

    public String getmDay() {
        return mDay;
    }

}
