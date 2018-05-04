package com.quagem.screentrends.fragments;

import android.content.ContentValues;
import android.net.Uri;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.quagem.screentrends.MediaDetailActivity;
import com.quagem.screentrends.R;
import com.quagem.screentrends.NetworkTools;
import com.quagem.screentrends.UrlLoader;
import com.quagem.screentrends.data.Contract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.quagem.screentrends.MediaDetailActivity.ARG_IS_FAVORITE;
import static com.quagem.screentrends.MediaDetailActivity.ARG_MEDIA_ID;

public class MediaDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<String> {

    public static final String TAG = MediaDetailFragment.class.getSimpleName();

    private final static int ULR_LOADER_ID = 3;

    private static final String JSON_MOVIE_ID = "id";
    private static final String JSON_POSTER_PATH = "poster_path";
    private static final String JSON_BACKDROP_PATH = "backdrop_path";
    private static final String JSON_ORIGINAL_TITLE = "original_title";
    private static final String JSON_GENRES = "genres";
    private static final String JSON_GENRES_NAME = "name";
    private static final String JSON_OVERVIEW = "overview";
    private static final String JSON_RELEASE_DATE = "release_date";
    private static final String JSON_VOTE_AVERAGE = "vote_average";
    private static final String JSON_VOTE_COUNT = "vote_count";

    private String movieId;
    private String posterPath;

    private View mainContainer;
    private ProgressBar progressBar;

    private boolean isFavorite;

    private ImageView backdrop;
    private TextView title;
    private TextView genres;
    private TextView overview;
    private TextView releaseDate;
    private TextView averageRating;
    private TextView basedOn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        setRetainInstance(true);

        if (getArguments() != null) {
            movieId = getArguments().getString(ARG_MEDIA_ID);
            isFavorite = getArguments().getBoolean(ARG_IS_FAVORITE);
        } else errorLoadingData();

        Log.i(TAG, "movieId: " + movieId);
        Log.i(TAG, "isFavorite: " + isFavorite);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        mainContainer = rootView.findViewById(R.id.container);
        mainContainer.setVisibility(View.GONE);

        progressBar = rootView.findViewById(R.id.progressbar);

        CheckBox favoriteCheckBox = rootView.findViewById(R.id.cb_favorite);
        favoriteCheckBox.setChecked(isFavorite);

        favoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleFavorite(isChecked);
            }
        });

        backdrop = rootView.findViewById(R.id.iv_backdrop);
        title = rootView.findViewById(R.id.tv_title);
        genres = rootView.findViewById(R.id.tv_genres);
        overview = rootView.findViewById(R.id.tv_overview);
        releaseDate = rootView.findViewById(R.id.tv_release_date);
        averageRating = rootView.findViewById(R.id.tv_average_rating);
        basedOn = rootView.findViewById(R.id.tv_based_on);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {

            if (NetworkTools.isConnected(getActivity()))
                getActivity().getSupportLoaderManager().
                        initLoader(ULR_LOADER_ID, null, this);
        }
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {

        progressBar.setVisibility(View.VISIBLE);

        return new UrlLoader(getContext(), NetworkTools.getMediaDetailUrl(
                getResources().getString(R.string.TMDB_API_KEY), movieId));

    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Log.i(TAG, "onLoadFinished: " + data);

        if (data != null) {

            try {

                final JSONObject results = new JSONObject(data);
                final JSONArray genresList = results.getJSONArray(JSON_GENRES);

                posterPath = results.getString(JSON_POSTER_PATH);

                Picasso.with(getContext())
                        .load(NetworkTools.getImageUri(
                                results.getString(JSON_BACKDROP_PATH),
                                NetworkTools.TMDB_IMAGE_W780))
                        .error(R.drawable.ic_launcher_background) // TODO: 4/27/18
                        .into(backdrop, new Callback() {
                            @Override
                            public void onSuccess() {
                                mainContainer.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                mainContainer.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                title.setText(results.getString(JSON_ORIGINAL_TITLE));

                if (!results.getString(JSON_RELEASE_DATE).isEmpty()) {
                    releaseDate.setText(results.getString(JSON_RELEASE_DATE));
                    releaseDate.setVisibility(View.VISIBLE);
                }

                StringBuilder stringBuilder = new StringBuilder();

                // Genres
                if (genresList.length() > 0) {

                    for (int i = 0; i < genresList.length(); i++) {

                        if (genresList.length() > 0 && i > 0) stringBuilder.append(", ");

                        stringBuilder.append(
                                genresList.getJSONObject(i).getString(JSON_GENRES_NAME));
                    }
                    stringBuilder.append(".");
                    genres.setText(stringBuilder);

                } else genres.setVisibility(View.GONE);

                if (!results.getString(JSON_VOTE_AVERAGE).isEmpty()) {

                    averageRating.setText(results.getString(JSON_VOTE_AVERAGE));
                    averageRating.setVisibility(View.VISIBLE);

                    stringBuilder.setLength(0);
                    stringBuilder.append(getResources().getString(R.string.based_on));
                    stringBuilder.append(" ");
                    stringBuilder.append(results.getString(JSON_VOTE_COUNT));
                    stringBuilder.append(" ");
                    stringBuilder.append(getResources().getString(R.string.critics));
                    stringBuilder.append(".");

                    basedOn.setText(stringBuilder.toString().toUpperCase());
                    basedOn.setVisibility(View.VISIBLE);
                }


                overview.setText(results.getString(JSON_OVERVIEW));

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

    private void toggleFavorite(boolean state) {
        Log.i(TAG, "toggleFavorite: " + state);

        isFavorite = state;

        if (isFavorite) {
            if (addedToFavorites()) showToast(R.string.saved_to_favorites);
            else showToast(R.string.error_saving_data);
        } else {
            if (removedFromFavorites()) showToast(R.string.removed_from_favorites);
            else showToast(R.string.error_saving_data);
        }
    }

    private boolean addedToFavorites() {

        ContentValues cv = new ContentValues();

        cv.put(Contract.Movies.MOVIE_ID, movieId);
        cv.put(Contract.Movies.TITLE, title.getText().toString());
        cv.put(Contract.Movies.POSTER_PATH, posterPath);

        Uri results = null;

        if (getContext() != null) {
            results = getContext().getContentResolver().insert(Contract.Movies.CONTENT_URI, cv);
        }

        return results != null;
    }

    private boolean removedFromFavorites() {

        int results = 0;

        if (getContext() != null) {

            String where = Contract.Movies.MOVIE_ID + "=?";
            String whereArgs[] = {movieId};

            results = getContext().getContentResolver().delete(
                    Contract.Movies.CONTENT_URI, where, whereArgs);
        }

        return results != 0;
    }

    private void showToast(int stringId) {
        Toast.makeText(getContext(), stringId, Toast.LENGTH_LONG).show();
    }
}
