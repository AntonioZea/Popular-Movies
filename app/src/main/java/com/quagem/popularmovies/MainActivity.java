package com.quagem.popularmovies;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<String> {

    public static final String TAG = MainActivity.class.getSimpleName();

    private final static int MOVIE_LIST_LOADER_ID = 1;

    private static final String TMDB_RESULTS = "results";
    private static final String TMDB_MOVIE_ID = "id";
    private static final String TMDB_POSTER_PATH = "poster_path";

    private GridView gridView;

    private List<MovieDataType> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        listData = new ArrayList<>();

        // Spinner.
        Spinner spinner = findViewById(R.id.spinner);
        String[] items = new String[]{"1", "2", "three"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        // GridVew.
        gridView = findViewById(R.id.gridview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "id: " + listData.get(position).getId(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        getSupportLoaderManager().
                initLoader(MOVIE_LIST_LOADER_ID, null, this);

    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {

        return new MovieListLoader(this,
                TMDBNetworkTools.getPopularListUrl(getString(R.string.TMDB_API_KEY)));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Log.i(TAG, "onLoadFinished: " + data);

        if (data != null) {

            try {

                final JSONObject results = new JSONObject(data);
                final JSONArray movies = results.getJSONArray(TMDB_RESULTS);

                MovieDataType movieDataType;

                for (int i = 0; i < movies.length(); i++) {

                    JSONObject movie = movies.getJSONObject(i);

                    movieDataType = new MovieDataType();

                    movieDataType.setId(movie.getLong(TMDB_MOVIE_ID));
                    movieDataType.setPosterPath(movie.getString(TMDB_POSTER_PATH));

                    listData.add(movieDataType);
                }

                gridView.setAdapter(new MovieListAdaptor(this, listData));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    private static class MovieListLoader extends AsyncTaskLoader<String> {

        private static final String TAG = MovieListLoader.class.getSimpleName();

        private URL url;
        private String results = "";

        MovieListLoader(@NonNull Context context, URL url) {
            super(context);
            this.url = url; // New data.
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

}
