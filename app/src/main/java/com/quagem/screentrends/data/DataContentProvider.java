package com.quagem.screentrends.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by quagem on 5/3/18.
 *
 */

public class DataContentProvider extends ContentProvider {

    public static final int MOVIES = 100;

    public static final UriMatcher sUriMatcher = BuildUriMatcher();

    private DbHelper mDbHelper;

    private static UriMatcher BuildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(Contract.AUTHORITY, Contract.PATH_MOVIES, MOVIES);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s,
                        @Nullable String[] strings1, @Nullable String s1) {

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String table;

        switch (sUriMatcher.match(uri)) {

            case MOVIES: table = Contract.Movies.TABLE_NAME; break;

            default: throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        Cursor cursor = db.query(table, strings, s, strings1, null, null, s1);

        if (getContext() != null)
            cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String table;
        Uri contentUri;

        switch (sUriMatcher.match(uri)) {

            case MOVIES:
                table = Contract.Movies.TABLE_NAME;
                contentUri = Contract.Movies.CONTENT_URI;
                break;

            default: throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        long id = db.insert(table, null, contentValues);

        if (id > 0 ) {

            if (getContext() != null)
                getContext().getContentResolver().notifyChange(uri, null);

            return ContentUris.withAppendedId(contentUri, id);

        } else throw new android.database.SQLException("Fail to insert row into: " + uri);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int tasksDelete = 0;

        if (strings != null && strings.length > 0) {

            final String table;

            switch (sUriMatcher.match(uri)) {

                case MOVIES:
                    table = Contract.Movies.TABLE_NAME;
                    break;

                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);

            }

            tasksDelete = db.delete(table, s, strings);
        }

        if (tasksDelete != 0) {

            if (getContext() != null)
                getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDelete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int tasksUpdated = 0;

        if (strings != null  && strings.length > 0) {

            final String table;

            switch (sUriMatcher.match(uri)) {

                case MOVIES:
                    table = Contract.Movies.TABLE_NAME;
                    break;

                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);

            }

            tasksUpdated = db.update(table,
                    contentValues, "_id=?", new String[]{strings[0]});
        }

        if (tasksUpdated != 0) {

            if (getContext() != null)
                getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksUpdated;
    }

}
