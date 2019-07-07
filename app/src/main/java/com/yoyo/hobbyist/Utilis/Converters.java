package com.yoyo.hobbyist.Utilis;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yoyo.hobbyist.DataModels.UserPost;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Converters implements Serializable {
    @TypeConverter
    public static ArrayList<UserPost> userPostsFromString(String value) {
        Type listType = new TypeToken<ArrayList<UserPost>>() {
        }.getType();
        return new Gson().fromJson( value, listType );
    }

    @TypeConverter
    public static String userPostsFromList(ArrayList<UserPost> list) {
        Gson gson = new Gson();
        String json = gson.toJson( list );
        return json;
    }

    @TypeConverter
    public static ArrayList<String> hobbyFromString(String value) {
        Type listType = new TypeToken<List<UserPost>>() {
        }.getType();
        return new Gson().fromJson( value, listType );
    }

    @TypeConverter
    public static String hobbyFromList(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson( list );
        return json;
    }
}