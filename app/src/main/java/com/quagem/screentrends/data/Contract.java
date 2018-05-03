package com.quagem.screentrends.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by quagem on 5/3/18.
 *
 */

public class Contract {

    static final String AUTHORITY = "com.quagem.screentrends";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    static final String PATH_MOVIES = "movies";

    public static final class Movies implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";
        public static final String MEDIA_ID = "id";
        public static final String TITLE = "name";
    }
}
