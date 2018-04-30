package com.quagem.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class UrlLoader extends AsyncTaskLoader<String> {

    private static final String TAG = UrlLoader.class.getSimpleName();

    private URL url;
    private String results = "";

    public UrlLoader(@NonNull Context context, URL url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(TAG, "onStartLoadingData");

        if (results.isEmpty()) forceLoad(); // New data.
        else deliverResult(results);        // Cached data.
    }

    @Nullable
    @Override
    public String loadInBackground() {
        Log.i(TAG, "loadInBackground");

        HttpURLConnection connection;

        try {

            connection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = connection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) return results = scanner.next();
            else return null;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
