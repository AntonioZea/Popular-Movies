package com.quagem.screentrends.fragments;

import android.content.Intent;
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

import com.quagem.screentrends.MainActivity;
import com.quagem.screentrends.MediaDataType;
import com.quagem.screentrends.MediaDetailActivity;
import com.quagem.screentrends.MediaGridAdaptor;
import com.quagem.screentrends.R;
import com.quagem.screentrends.NetworkTools;
import com.quagem.screentrends.UrlLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TopRatedMoviesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<String>> {

    public static final String TAG = TopRatedMoviesFragment.class.getSimpleName();

    private final static int ULR_LOADER_ID = 2;

    private static final String JSON_RESULTS = "results";
    private static final String JSON_MOVIE_ID = "id";
    private static final String JSON_POSTER_PATH = "poster_path";

    private List<MediaDataType> listData;
    private MediaGridAdaptor mediaGridAdaptor;

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
        mediaGridAdaptor = new MediaGridAdaptor(getActivity(),listData);

        // GridVew.
        GridView gridView = rootView.findViewById(R.id.gridview);
        gridView.setAdapter(mediaGridAdaptor);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                if (getActivity() != null) {

                    String mediaId = Long.toString(id);

                    // check if favorite
                    boolean isFavorite = ((MainActivity)getActivity()).isInFavorites(mediaId);

                    Intent intent = new Intent(getContext(), MediaDetailActivity.class);
                    intent.putExtra(MediaDetailActivity.ARG_MEDIA_ID, mediaId);
                    intent.putExtra(MediaDetailActivity.ARG_IS_FAVORITE, isFavorite);

                    getActivity().startActivity(intent);
                }
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

            if (NetworkTools.isConnected(getActivity()))
                getActivity().getSupportLoaderManager().
                        initLoader(ULR_LOADER_ID, null, this);
        }
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public Loader<List<String>> onCreateLoader(int id, @Nullable Bundle args) {

        List<URL> urlList = new ArrayList<>();

        urlList.add(NetworkTools.getTopRatedListUrl(
                getResources().getString(R.string.TMDB_API_KEY)));

        return new UrlLoader(getContext(), urlList);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> data) {
        Log.i(TAG, "onLoadFinished: " + data);

        if (!data.get(0).isEmpty()) {

            listData.clear();

            try {

                final JSONObject results = new JSONObject(data.get(0));
                final JSONArray movies = results.getJSONArray(JSON_RESULTS);

                MediaDataType mediaDataType;

                for (int i = 0; i < movies.length(); i++) {

                    JSONObject movie = movies.getJSONObject(i);

                    mediaDataType = new MediaDataType();

                    mediaDataType.setId(movie.getLong(JSON_MOVIE_ID));
                    mediaDataType.setPosterPath(movie.getString(JSON_POSTER_PATH));

                    listData.add(mediaDataType);
                }

                mediaGridAdaptor.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String>> loader) {

    }
}

