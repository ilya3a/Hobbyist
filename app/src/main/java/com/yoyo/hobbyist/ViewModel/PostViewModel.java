package com.yoyo.hobbyist.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.yoyo.hobbyist.DataModels.UserPost;

import java.util.List;

public class PostViewModel extends ViewModel {

    private MutableLiveData<List<UserPost>> postsList;

    public LiveData<List<UserPost>> getPosts() {
        if (postsList == null) {
            postsList = new MutableLiveData<>();

            loadPosts();
        }
        return postsList;
    }

    private void loadPosts() {

    }
}
