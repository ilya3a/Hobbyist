package com.yoyo.hobbyist.DataModels;

import com.google.android.gms.maps.model.LatLng;

public class UserPost {
    String userName;
    String userProfilePicUrl;
    String userToken;
    String birthDate;
    String age;
    boolean isOnline;
    String content;
    double latitude;
    double longitude;


    public UserPost() {
    }

    ;

    public UserPost(String userName, String userProfilePicUrl, String userToken, String birthDate, String age, boolean isOnline, String content, double latitude, double longitude) {
        this.userName = userName;
        this.userProfilePicUrl = userProfilePicUrl;
        this.userToken = userToken;
        this.birthDate = birthDate;
        this.age = age;
        this.isOnline = isOnline;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;


    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

    public void setUserProfilePicUrl(String userProfilePicUrl) {
        this.userProfilePicUrl = userProfilePicUrl;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
