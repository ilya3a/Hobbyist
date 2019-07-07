package com.yoyo.hobbyist.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

//import com.yoyo.hobbyist.DataBase.UserPostRepository;
//import com.yoyo.hobbyist.DataBase.UserProfileRepository;
//import com.yoyo.hobbyist.DataModels.UserPost;
//import com.yoyo.hobbyist.DataModels.UserProfile;

import java.util.List;

public class DataViewModel extends AndroidViewModel {

    //    private UserPostRepository userPostRepository;
//    private UserProfileRepository userProfileRepository;
//
//    private LiveData<List<UserPost>> allPosts;
//    private LiveData<List<UserProfile>> allProfiles;
//
    public DataViewModel(Application application) {
        super( application );
//
//        userPostRepository = new UserPostRepository( application );
//        userProfileRepository = new UserProfileRepository( application );
//
//        allPosts = userPostRepository.getAllPosts();
//        allProfiles = userProfileRepository.getAllUsers();
//
//    }
//
//    public void insert(UserPost userPost) {
//        userPostRepository.insert( userPost );
//    }
//
//    public void insert(UserProfile userProfile) {
//        userProfileRepository.insert( userProfile );
//    }
//
//
//    public void delete(UserPost userPost) {
//        userPostRepository.delete( userPost );
//    }
//
//    public void delete(UserProfile userProfile) {
//        userProfileRepository.delete( userProfile );
//    }
//
//
////    public void update(UserPost userPost) {
////        userPostRepository.update( userPost );
////    }
////
////    public void update(UserProfile userProfile) {
////        userProfileRepository.update( userProfile );
////    }
//
//    public void getPostByToken(String token) {
//        userPostRepository.getPostByToken( token );
//    }
//
//    public void getProfileByToken(String token) {
//        userProfileRepository.getUserByToken( token );
//    }
//
//
//    public LiveData<List<UserPost>> getAllPosts() {
//        return allPosts;
//    }
//
//    public LiveData<List<UserProfile>> getAllProfiles() {
//        return allProfiles;
    }
}
