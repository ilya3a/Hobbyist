package com.yoyo.hobbyist.DataBase;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yoyo.hobbyist.DataModels.UserPost;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface UserPostDao {

    @Query("SELECT * FROM posts_table ORDER BY user_token DESC")
    LiveData<List<UserPost>> getAllPosts();

    @Query("SELECT * FROM posts_table WHERE user_token=:token")
    Flowable<UserPost> getPostByToken(String token);

    @Insert
    void insert(UserPost... userPosts);

    @Update
    void update(UserPost userPost);

    @Delete
    void delete(UserPost userPost);
}
