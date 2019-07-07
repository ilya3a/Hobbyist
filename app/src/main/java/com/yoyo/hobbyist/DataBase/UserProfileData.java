package com.yoyo.hobbyist.DataBase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yoyo.hobbyist.DataModels.UserPost;
import com.yoyo.hobbyist.DataModels.UserProfile;

import java.util.List;

import io.reactivex.Flowable;


public interface UserProfileData {

    LiveData<List<UserProfile>> getAllUsers();

    Flowable<UserProfile> getUserByToken(String token);

    void insert(UserProfile... userProfiles);

    void delete(UserProfile userProfile);

    void update(UserProfile userProfile);
}
