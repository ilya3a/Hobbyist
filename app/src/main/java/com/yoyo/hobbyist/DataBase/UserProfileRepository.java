package com.yoyo.hobbyist.DataBase;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.yoyo.hobbyist.DataModels.UserProfile;

import java.util.List;

import io.reactivex.Flowable;

public class UserProfileRepository implements UserProfileData {

    private UserProfileDao userProfileDao;
    private LiveData<List<UserProfile>> allUsers;

    public UserProfileRepository(Application application) {

        AppDataBase appDataBase = AppDataBase.getmInstance( application );

        userProfileDao = appDataBase.userProfileDao();
        allUsers = userProfileDao.getAllUsers();
    }

    @Override
    public LiveData<List<UserProfile>> getAllUsers() {
        return userProfileDao.getAllUsers();
    }

    @Override
    public void insert(UserProfile... userProfiles) {
        new InsertUserProfileAsyncTask( userProfileDao ).execute( userProfiles );
    }

    @Override
    public void delete(UserProfile userProfile) {
        new DeleteUserProfileAsyncTask( userProfileDao ).execute( userProfile );
    }

    @Override
    public void update(UserProfile userProfile) {
        new UpdateUserProfileAsyncTask( userProfileDao ).execute( userProfile );
    }

    @Override
    public Flowable<UserProfile> getUserByToken(String token) {

        return userProfileDao.getPostByToken( token );
    }


    private static class InsertUserProfileAsyncTask extends AsyncTask<UserProfile, Void, Void> {

        private UserProfileDao userProfileDao;

        private InsertUserProfileAsyncTask(UserProfileDao userProfileDao) {
            this.userProfileDao = userProfileDao;
        }

        @Override
        protected Void doInBackground(UserProfile... userProfiles) {
            userProfileDao.insert( userProfiles[0] );
            return null;
        }
    }

    private static class DeleteUserProfileAsyncTask extends AsyncTask<UserProfile, Void, Void> {

        private UserProfileDao userProfileDao;

        private DeleteUserProfileAsyncTask(UserProfileDao userProfileDao) {
            this.userProfileDao = userProfileDao;
        }

        @Override
        protected Void doInBackground(UserProfile... userProfiles) {
            userProfileDao.delete( userProfiles[0] );
            return null;
        }
    }

    private static class UpdateUserProfileAsyncTask extends AsyncTask<UserProfile, Void, Void> {

        private UserProfileDao userProfileDao;

        private UpdateUserProfileAsyncTask(UserProfileDao userProfileDao) {
            this.userProfileDao = userProfileDao;
        }

        @Override
        protected Void doInBackground(UserProfile... userProfiles) {
            userProfileDao.update( userProfiles[0] );
            return null;
        }
    }
}
