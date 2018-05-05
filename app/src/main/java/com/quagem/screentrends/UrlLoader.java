package com.quagem.screentrends;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UrlLoader extends AsyncTaskLoader<List<String>> {

    private static final String TAG = UrlLoader.class.getSimpleName();

    private List<URL> urlList;
    private List<String> results = new ArrayList<>();

    public UrlLoader(@NonNull Context context, List<URL> urlList) {
        super(context);
        this.urlList = urlList;
    }

    @Override
    protected void onStartLoading() {
        Log.i(TAG, "onStartLoadingData");

        if (results.isEmpty()) forceLoad(); // New data.
        else deliverResult(results);        // Cached data.
    }

    @Nullable
    @Override
    public List<String> loadInBackground() {
        Log.i(TAG, "loadInBackground");

        HttpURLConnection connection;

        try {

            for (URL url : urlList) {

                connection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = connection.getInputStream();

                Scanner scanner = new Scanner(inputStream);
                scanner.useDelimiter("\\A");

                if (scanner.hasNext()) results.add(scanner.next());
                else results.add(""); // Add empty string to keep request order.

            }

            return results;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
