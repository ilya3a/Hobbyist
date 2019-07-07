package com.yoyo.hobbyist.DataModels;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "posts_table")
public class UserPost {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

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
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserProfilePicUrl() {
        return userProfilePicUrl;
    }

    public void setUserProfilePicUrl(String userProfilePicUrl) {
        this.userProfilePicUrl = userProfilePicUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
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
}
