/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.quagem.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkTools {

    private final static String TMDB_MEDIA_DETAIL_BASE_URL =
            "http://api.themoviedb.org/3/movie/";

    private final static String TMDB_POPULAR_LIST_BASE_URL =
            "http://api.themoviedb.org/3/movie/popular";

    private final static String TMDB_TOP_RATED_LIST_BASE_URL =
            "http://api.themoviedb.org/3/movie/top_rated";

    private final static String TMDB_PARAM_API_KEY = "api_key";

    private final static String TMDB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    public final static String TMDB_IMAGE_W92  = "w92";
    public final static String TMDB_IMAGE_W154 = "w154";
    public final static String TMDB_IMAGE_W185 = "w185";
    public final static String TMDB_IMAGE_W342 = "w342";
    public final static String TMDB_IMAGE_W500 = "w500";
    public final static String TMDB_IMAGE_W780 = "w780";
    public final static String TMDB_IMAGE_ORIGINAL = "original";

    public static boolean isConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)context.
                getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        }

        return false;
    }

    public static URL getPopularListUrl(String apiKey) {

        Uri uri = Uri.parse(TMDB_POPULAR_LIST_BASE_URL).buildUpon()
                .appendQueryParameter(TMDB_PARAM_API_KEY, apiKey).build();

        return buildUrl(uri);
    }

    public static URL getTopRatedListUrl(String apiKey) {

        Uri uri = Uri.parse(TMDB_TOP_RATED_LIST_BASE_URL).buildUpon()
                .appendQueryParameter(TMDB_PARAM_API_KEY, apiKey).build();

        return buildUrl(uri);
    }

    public static URL getMediaDetailUrl(String apiKey, String mediaId) {

        Uri uri = Uri.parse(TMDB_MEDIA_DETAIL_BASE_URL).buildUpon()
                .appendPath(mediaId)
                .appendQueryParameter(TMDB_PARAM_API_KEY, apiKey).build();

        return buildUrl(uri);
    }

    public static Uri getImageUri(String imagePath, String size) {
        return Uri.parse(TMDB_IMAGE_BASE_URL).buildUpon()
                .appendPath(size)
                .appendEncodedPath(imagePath).build();
    }

    private static URL buildUrl(Uri uri) {

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
}