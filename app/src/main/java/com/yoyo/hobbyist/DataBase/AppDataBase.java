package com.yoyo.hobbyist.DataBase;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.yoyo.hobbyist.DataModels.UserPost;
import com.yoyo.hobbyist.DataModels.UserProfile;

import static com.yoyo.hobbyist.DataBase.AppDataBase.DATABASE_VERSION;


@Database(entities = {UserPost.class, UserProfile.class}, version = DATABASE_VERSION, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "EDMT-Database-Room";

    public abstract UserPostDao userPostDao();
    public abstract UserProfileDao userProfileDao();

    private static AppDataBase mInstance;

    public static synchronized AppDataBase getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder( context, AppDataBase.class, DATABASE_NAME )
                    .addCallback( roomCallback )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return mInstance;
    }

    private static Callback roomCallback = new Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate( db );
            new PopulateDBAsyncTask( mInstance ).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {

        private UserPostDao userPostDao;
        private UserProfileDao userProfileDao;

        private PopulateDBAsyncTask(AppDataBase db) {
            userPostDao = db.userPostDao();
            userProfileDao = db.userProfileDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }
}