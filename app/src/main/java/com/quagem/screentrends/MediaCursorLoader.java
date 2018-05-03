package com.quagem.screentrends;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.quagem.screentrends.data.Contract;

import java.util.ArrayList;
import java.util.List;

public class MediaCursorLoader  extends
        AsyncTaskLoader<List<MediaDataType>> {

    private static final String TAG = MediaCursorLoader.class.getSimpleName();

    public MediaCursorLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<MediaDataType> loadInBackground() {
        Log.i(TAG, "loadInBackground");

        Uri uri;
        Cursor cursor;

        uri = Contract.Movies.CONTENT_URI;
        cursor = getContext().getContentResolver().query(
                uri, null, null, null, null);

        MediaDataType mediaDataType;
        List<MediaDataType> listData = new ArrayList<>();

        if (cursor != null) {

            while (cursor.moveToNext()) {

                mediaDataType = new MediaDataType();

                mediaDataType.setId(cursor.getLong(cursor.getColumnIndex(
                        Contract.Movies.MOVIE_ID)));

                mediaDataType.setPosterPath(cursor.getString(cursor.getColumnIndex(
                        Contract.Movies.POSTER_PATH)));

                listData.add(mediaDataType);
            }

            cursor.close();
        }

        return listData;
    }
}
