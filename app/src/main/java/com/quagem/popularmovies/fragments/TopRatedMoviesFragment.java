package com.quagem.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.quagem.popularmovies.MovieDataType;
import com.quagem.popularmovies.MovieListAdaptor;
import com.quagem.popularmovies.R;
import com.quagem.popularmovies.TMDBNetworkTools;
import com.quagem.popularmovies.UrlLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TopRatedMoviesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<String> {

    public static final String TAG = TopRatedMoviesFragment.class.getSimpleName();

    private final static int ULR_LOADER_ID = 2;

    private static final String JSON_RESULTS = "results";
    private static final String JSON_MOVIE_ID = "id";
    private static final String JSON_POSTER_PATH = "poster_path";

    private List<MovieDataType> listData;
    private MovieListAdaptor movieListAdaptor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        View rootView;

        rootView = inflater.inflate(R.layout.grid_view, container, false);

        listData = new ArrayList<>();
        movieListAdaptor = new MovieListAdaptor(getActivity(),listData);

        // GridVew.
        GridView gridView = rootView.findViewById(R.id.gridview);
        gridView.setAdapter(movieListAdaptor);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "id: " + listData.get(position).getId(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {

            ActionBar actionBar;
            actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) actionBar.setSubtitle(R.string.top_rated_movies);

            getActivity().getSupportLoaderManager().
                    initLoader(ULR_LOADER_ID, null, this);
        }
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new UrlLoader(getContext(), TMDBNetworkTools.getTopRatedListUrl(
                getResources().getString(R.string.TMDB_API_KEY)));

    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Log.i(TAG, "onLoadFinished: " + data);

        if (data != null) {

            listData.clear();

            try {

                final JSONObject results = new JSONObject(data);
                final JSONArray movies = results.getJSONArray(JSON_RESULTS);

                MovieDataType movieDataType;

                for (int i = 0; i < movies.length(); i++) {

                    JSONObject movie = movies.getJSONObject(i);

                    movieDataType = new MovieDataType();

                    movieDataType.setId(movie.getLong(JSON_MOVIE_ID));
                    movieDataType.setPosterPath(movie.getString(JSON_POSTER_PATH));

                    listData.add(movieDataType);
                }

                movieListAdaptor.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}

