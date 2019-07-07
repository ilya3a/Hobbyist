package com.yoyo.hobbyist.DataBase;


import android.arch.lifecycle.LiveData;

import com.yoyo.hobbyist.DataModels.UserPost;

import java.util.List;

import io.reactivex.Flowable;

public  interface UserPostData {

    LiveData<List<UserPost>> getAllPosts();

    Flowable<UserPost> getPostByToken(String token);

    void insert(UserPost... userPosts);

    void delete(UserPost userPost);

    void update(UserPost userPost);
}
