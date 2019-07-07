package com.yoyo.hobbyist.DataModels;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yoyo.hobbyist.Utilis.Converters;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "users_table")
public class UserProfile implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "last_name")
    private String lastName;

    @ColumnInfo(name = "year")
    private String year;

    @ColumnInfo(name = "month")
    private String month;

    @ColumnInfo(name = "day")
    private String day;

    @ColumnInfo(name = "age")
    private String age;

    @ColumnInfo(name = "picture_url")
    private String pictureUrl;

    @ColumnInfo(name = "user_about_me")
    private String userAboutMe;

    @ColumnInfo(name = "city_name")
    private String cityName;

    @ColumnInfo(name = "gender")
    private String gender;

    @ColumnInfo(name = "status")
    private String status;

    @TypeConverters(Converters.class)
    @ColumnInfo(name = "hobby_list")
    private ArrayList<String> hobbyList;

    @TypeConverters(Converters.class)
    @ColumnInfo(name = "user_post_list")
    private ArrayList<UserPost> userPostList;

    @ColumnInfo(name = "user_token")
    private String userToken;


    public UserProfile(String name, String lastName, String age, String pictureUrl, String cityName, String gender, ArrayList<String> hobbyList, ArrayList<UserPost> userPostList, String userToken) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.pictureUrl = pictureUrl;
        this.cityName = cityName;
        this.gender = gender;
        this.hobbyList = hobbyList;
        this.userPostList = userPostList;
        this.userToken = userToken;
    }

    public UserProfile() {
    }

    public long getId() {
        return id;
    }

    public void setId(long mId) {
        this.id = mId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String mEmail) {
        this.email = mEmail;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String mYear) {
        this.year = mYear;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String mMonth) {
        this.month = mMonth;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getUserAboutMe() {
        return userAboutMe;
    }

    public void setUserAboutMe(String mUserAboutMe) {
        this.userAboutMe = mUserAboutMe;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getHobbyList() {
        return hobbyList;
    }

    public void setHobbyList(ArrayList<String> hobbyList) {
        this.hobbyList = hobbyList;
    }

    public ArrayList<UserPost> getUserPostList() {
        return userPostList;
    }

    public void setUserPostList(ArrayList<UserPost> userPostList) {
        this.userPostList = userPostList;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    @TypeConverter
    public static List<UserPost> userPostsFromString(String value) {
        Type listType = new TypeToken<List<UserPost>>() {
        }.getType();
        return new Gson().fromJson( value, listType );
    }

    @TypeConverter
    public static String userPostsFromList(List<UserPost> mUserPostList) {
        Gson gson = new Gson();
        String json = gson.toJson( mUserPostList );
        return json;
    }
}
