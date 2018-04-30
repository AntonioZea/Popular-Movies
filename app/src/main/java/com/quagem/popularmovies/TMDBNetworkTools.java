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

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class TMDBNetworkTools {

    public final static String ARG_POPULAR_LIST = "arg_popular_list";
    public final static String ARG_TOP_RATED_LIST = "arg_top_rated_list";

    private final static String TMDB_POPULAR_LIST_BASE_URL =
            "http://api.themoviedb.org/3/movie/popular";

    private final static String TMDB_TOP_RATED_LIST_BASE_URL =
            "http://api.themoviedb.org/3/movie/top_rated";

    private final static String TMDB_PARAM_API_KEY = "api_key";

    private final static String TMDB_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    // Available sizes: "w92", "w154", "w185", "w342", "w500", "w780", "original".
    private final static String TMDB_IMAGE_SIZE = "w185";

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

    static Uri getPosterUri(String posterPath) {
        return Uri.parse(TMDB_IMAGE_BASE_URL).buildUpon()
                .appendPath(TMDB_IMAGE_SIZE)
                .appendEncodedPath(posterPath).build();
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