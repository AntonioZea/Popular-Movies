package com.quagem.popularmovies.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.quagem.popularmovies.MediaDataType;
import com.quagem.popularmovies.MediaDetailActivity;
import com.quagem.popularmovies.R;
import com.quagem.popularmovies.TMDBNetworkTools;
import com.quagem.popularmovies.UrlLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MediaDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<String> {

    public static final String TAG = TopRatedMoviesFragment.class.getSimpleName();

    private final static int ULR_LOADER_ID = 3;

    private static final String JSON_RESULTS = "results";
    private static final String JSON_MOVIE_ID = "id";
    private static final String JSON_POSTER_PATH = "poster_path";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        View rootView;
        rootView = inflater.inflate(R.layout.movie_detail, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("SIS", savedInstanceState == null ? "TRUE" : "False");
        if (getActivity() != null) {

            getActivity().getSupportLoaderManager().
                    initLoader(ULR_LOADER_ID, null, this);
        }
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {

        String mediaId = getActivity().getIntent().getStringExtra(MediaDetailActivity.ARG_MEDIA_ID);
        if (mediaId == null) errorLoadingData();

        return new UrlLoader(getContext(), TMDBNetworkTools.getMediaDetailUrl(
                getResources().getString(R.string.TMDB_API_KEY), mediaId));

    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Log.i(TAG, "onLoadFinished: " + data);

        if (data != null) {

            try {

                final JSONObject results = new JSONObject(data);
                final JSONArray movies = results.getJSONArray(JSON_RESULTS);

                MediaDataType mediaDataType;

                for (int i = 0; i < movies.length(); i++) {

                    JSONObject movie = movies.getJSONObject(i);

                    mediaDataType = new MediaDataType();

                    mediaDataType.setId(movie.getLong(JSON_MOVIE_ID));
                    mediaDataType.setPosterPath(movie.getString(JSON_POSTER_PATH));


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    private void errorLoadingData(){

        Toast.makeText(getContext(),
                getResources().getString(R.string.error_loading_data), Toast.LENGTH_LONG).show();

        if (getActivity() != null) getActivity().finish();
    }
}
