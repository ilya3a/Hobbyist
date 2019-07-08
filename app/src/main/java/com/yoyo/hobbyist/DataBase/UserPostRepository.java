package com.yoyo.hobbyist.DataBase;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.yoyo.hobbyist.DataModels.UserPost;

import java.util.List;

import io.reactivex.Flowable;

public class UserPostRepository implements UserPostData {
    private UserPostDao userPostDao;
    private LiveData<List<UserPost>> allPosts;

    public UserPostRepository(Application application) {

        AppDataBase appDataBase = AppDataBase.getmInstance( application );
        userPostDao = appDataBase.userPostDao();
        allPosts = userPostDao.getAllPosts();

    }

    public LiveData<List<UserPost>> getAllPosts() {
        return userPostDao.getAllPosts();
    }

    public void insert(UserPost... userPosts) {
        new InsertUserPostAsyncTask( userPostDao ).execute( userPosts );
    }

    public void delete(UserPost userPost) {
        new DeleteUserPostAsyncTask( userPostDao ).execute( userPost );
    }


    public void deleteAllPosts() {
        new DeleteAllUserPostAsyncTask( userPostDao ).execute( );
    }

    public void update(UserPost userPost) {
        new UpdateUserPostAsyncTask( userPostDao ).execute( userPost );
    }


    public Flowable<UserPost> getPostByToken(String token) {
        return userPostDao.getPostByToken( token );
    }


    private static class InsertUserPostAsyncTask extends AsyncTask<UserPost, Void, Void> {

        private UserPostDao userPostDao;

        private InsertUserPostAsyncTask(UserPostDao userPostDao) {
            this.userPostDao = userPostDao;
        }

        @Override
        protected Void doInBackground(UserPost... userPosts) {
            userPostDao.insert( userPosts[0] );
            return null;
        }
    }

    private static class DeleteUserPostAsyncTask extends AsyncTask<UserPost, Void, Void> {

        private UserPostDao userPostDao;

        private DeleteUserPostAsyncTask(UserPostDao userPostDao) {
            this.userPostDao = userPostDao;
        }

        @Override
        protected Void doInBackground(UserPost... userPosts) {
            userPostDao.delete( userPosts[0] );
            return null;
        }
    }

    private static class DeleteAllUserPostAsyncTask extends AsyncTask<Void, Void, Void> {

        private UserPostDao userPostDao;

        private DeleteAllUserPostAsyncTask(UserPostDao userPostDao) {
            this.userPostDao = userPostDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userPostDao.deleteAllPosts();
            return null;
        }
    }

    private static class UpdateUserPostAsyncTask extends AsyncTask<UserPost, Void, Void> {

        private UserPostDao userPostDao;

        private UpdateUserPostAsyncTask(UserPostDao userPostDao) {
            this.userPostDao = userPostDao;
        }

        @Override
        protected Void doInBackground(UserPost... userPosts) {
            userPostDao.update( userPosts[0] );
            return null;
        }
    }
}
