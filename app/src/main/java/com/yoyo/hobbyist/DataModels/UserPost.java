package com.yoyo.hobbyist.DataModels;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "posts_table")
public class UserPost {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long Id;

    @ColumnInfo(name = "user_profile_pic_url")
    String userProfilePicUrl;

    @ColumnInfo(name = "user_name")
    String userName;
    @ColumnInfo(name = "city_name")
    String cityName;

    @ColumnInfo(name = "is_online")
    boolean isOnline;

    @ColumnInfo(name = "date")
    String date;

    @ColumnInfo(name = "hobby")
    String hobby;

    @ColumnInfo(name = "user_token")
    String userToken;

    @ColumnInfo(name = "gender")
    String gender;

    @ColumnInfo(name = "posr_description")
    String postDescription;

    @ColumnInfo(name = "latitude")
    double latitude;

    @ColumnInfo(name = "longitude")
    double longitude;

    public UserPost(String userProfilePicUrl, String userName, String cityName, boolean isOnline, String date, String hobby, String userToken, String postDescription, double latitude, double longitude, String gender) {
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
        this.gender = gender;
    }


    public long getId() {
        return Id;
    }

    public void setId(long Id) {
        this.Id = Id;
    }

    public String getGender() {
        return gender;
    }

    public UserPost setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public UserPost() {
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
