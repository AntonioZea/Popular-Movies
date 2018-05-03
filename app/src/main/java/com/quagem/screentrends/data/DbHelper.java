package com.quagem.screentrends.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by quagem on 5/3/18.
 *
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "screenTrends.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String SQL_CREATE_TABLE;

        SQL_CREATE_TABLE = "CREATE TABLE " +
                Contract.Movies.TABLE_NAME + " (" +
                Contract.Movies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Contract.Movies.MEDIA_ID + " INTEGER NOT NULL," +
                Contract.Movies.TITLE + " TEXT NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.Movies.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
