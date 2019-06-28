package com.yoyo.hobbyist.DataModels;

public class UserPost {

    String userProfilePicUrl;
    String userName;
    String cityName;
    boolean isOnline;
    String date;
    String hobby;
    String userToken;
    String postDescription;
    double latitude;
    double longitude;

    public UserPost(String userProfilePicUrl, String userName, String cityName, boolean isOnline, String date, String hobby, String userToken, String postDescription, double latitude, double longitude) {
        this.userProfilePicUrl = userProfilePicUrl;
        this.userName = userName;
        this.cityName = cityName;
        this.isOnline = isOnline;
        this.date = date;
        this.hobby = hobby;
        this.userToken = userToken;
        this.postDescription = postDescription;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

    public UserPost setUserProfilePicUrl(String userProfilePicUrl) {
        this.userProfilePicUrl = userProfilePicUrl;
        return this;

    }

    public String getUserName() {
        return userName;
    }

    public UserPost setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public UserPost setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public UserPost setOnline(boolean online) {
        isOnline = online;
        return this;
    }

    public String getDate() {
        return date;
    }

    public UserPost setDate(String date) {
        this.date = date;
        return this;
    }

    public String getHobby() {
        return hobby;

    }

    public UserPost setHobby(String hobby) {
        this.hobby = hobby;
        return this;
    }

    public String getUserToken() {
        return userToken;
    }

    public UserPost setUserToken(String userToken) {
        this.userToken = userToken;
        return this;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public UserPost setPostDescription(String postDescription) {
        this.postDescription = postDescription;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public UserPost setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public UserPost setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }
}
