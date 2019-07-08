package com.yoyo.hobbyist.DataBase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yoyo.hobbyist.DataModels.UserPost;
import com.yoyo.hobbyist.DataModels.UserProfile;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserProfileDao {

    @Query("SELECT * FROM users_table ORDER BY user_token DESC")
    LiveData<List<UserProfile>> getAllUsers();

    @Query("SELECT * FROM posts_table WHERE user_token=:token")
    Flowable<UserProfile> getPostByToken(String token);

    @Insert
    void insert(UserProfile... userProfiles);

    @Update
    void update(UserProfile userProfile);

    @Delete
    void delete(UserProfile userProfile);
}
