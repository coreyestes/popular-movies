package com.rcdev.popularmovies.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.rcdev.popularmovies.data.MovieContract.FavoriteEntry;


public class MovieDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME ="movie.db";

    public MovieDBHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate (SQLiteDatabase sqLiteDatabase){

        final String SQL_CREATE_TABLE = "CREATE TABLE " +
                FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_TITLE +" TEXT NOT NULL, "+
                FavoriteEntry.COLUMN_THUMB + " INTEGER NOT NULL, " +
                FavoriteEntry.COLUMN_BACK_DROP + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_POSTER + " TEXT NOT NULL, "+
                FavoriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_RATING + " REAL NOT NULL, " +
                FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL); " ;


        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}