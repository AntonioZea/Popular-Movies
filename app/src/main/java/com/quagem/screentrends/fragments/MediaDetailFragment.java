package com.quagem.screentrends.fragments;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.quagem.screentrends.R;
import com.quagem.screentrends.NetworkTools;
import com.quagem.screentrends.UrlLoader;
import com.quagem.screentrends.data.Contract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.quagem.screentrends.MediaDetailActivity.ARG_IS_FAVORITE;
import static com.quagem.screentrends.MediaDetailActivity.ARG_MEDIA_ID;

@SuppressLint("InflateParams")
public class MediaDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<String>> {

    public static final String TAG = MediaDetailFragment.class.getSimpleName();

    private final static int ULR_LOADER_ID = 3;

    private static final String JSON_KEY = "key";
    private static final String JSON_POSTER_PATH = "poster_path";
    private static final String JSON_BACKDROP_PATH = "backdrop_path";
    private static final String JSON_ORIGINAL_TITLE = "original_title";
    private static final String JSON_GENRES = "genres";
    private static final String JSON_NAME = "name";
    private static final String JSON_OVERVIEW = "overview";
    private static final String JSON_RELEASE_DATE = "release_date";
    private static final String JSON_VOTE_AVERAGE = "vote_average";
    private static final String JSON_VOTE_COUNT = "vote_count";
    private static final String JSON_RESULTS = "results";
    private static final String JSON_AUTHOR = "author";
    private static final String JSON_CONTENT = "content";

    private String movieId;
    private String posterPath;

    private LinearLayout reviewsContainer;
    private LinearLayout trailersContainer;
    private LinearLayout mainContainer;
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

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.media_detail, container, false);

        mainContainer = rootView.findViewById(R.id.container);
        mainContainer.setVisibility(View.GONE);

        reviewsContainer = rootView.findViewById(R.id.reviews_container);
        trailersContainer = rootView.findViewById(R.id.trailers_container);

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
    public Loader<List<String>> onCreateLoader(int id, @Nullable Bundle args) {

        progressBar.setVisibility(View.VISIBLE);

        List<URL> urlList = new ArrayList<>();

        // Media details.
        urlList.add(NetworkTools.getMediaDetailUrl(
                getResources().getString(R.string.TMDB_API_KEY), movieId));

        // Media trailers.
        urlList.add(NetworkTools.getMediaTrailers(
                getResources().getString(R.string.TMDB_API_KEY), movieId));

        // Media reviews.
        urlList.add(NetworkTools.getMediaReviews(
                getResources().getString(R.string.TMDB_API_KEY), movieId));

        return new UrlLoader(getContext(), urlList);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> data) {

        if (!data.get(0).isEmpty()) {

            try {

                final JSONObject results = new JSONObject(data.get(0));
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
                releaseDate.setText(results.getString(JSON_RELEASE_DATE));

                StringBuilder stringBuilder = new StringBuilder();

                // Genres
                if (genresList.length() > 0) {

                    for (int i = 0; i < genresList.length(); i++) {

                        if (genresList.length() > 0 && i > 0) stringBuilder.append(", ");

                        stringBuilder.append(
                                genresList.getJSONObject(i).getString(JSON_NAME));
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

                addReviews(data.get(2));
                addTrailers(data.get(1));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addReviews(String json) {

        if (reviewsContainer.getChildCount() > 0) reviewsContainer.removeAllViews();

        if (!json.isEmpty()) {

            TextView author;
            TextView content;

            LayoutInflater inflater = getLayoutInflater();

            inflater.inflate(R.layout.reviews_header, reviewsContainer);

            try {

                final JSONObject results = new JSONObject(json);
                final JSONArray reviewList = results.getJSONArray(JSON_RESULTS);

                for (int i = 0; i < reviewList.length(); i++) {

                    View rootView = inflater.inflate(R.layout.media_reviews, null);

                    author = rootView.findViewById(R.id.tv_review_author);
                    content = rootView.findViewById(R.id.tv_review_content);

                    author.setText(reviewList.getJSONObject(i).getString(JSON_AUTHOR));
                    content.setText(reviewList.getJSONObject(i).getString(JSON_CONTENT));

                    reviewsContainer.addView(rootView);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addTrailers(String json) {

        if (trailersContainer.getChildCount() > 0) trailersContainer.removeAllViews();

        if (!json.isEmpty()) {

            TextView name;

            LayoutInflater inflater = getLayoutInflater();

            inflater.inflate(R.layout.trailers_header, trailersContainer);

            try {

                final JSONObject results = new JSONObject(json);
                final JSONArray reviewList = results.getJSONArray(JSON_RESULTS);

                for (int i = 0; i < reviewList.length(); i++) {

                    View rootView = inflater.inflate(R.layout.media_trailers, null);

                    name = rootView.findViewById(R.id.tv_trailer_name);

                    name.setText(reviewList.getJSONObject(i).getString(JSON_NAME));

                    final String videoId = reviewList.getJSONObject(i).getString(JSON_KEY);

                    rootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (getContext() != null) {

                                Intent intent;
                                String youTubeUri = "http://www.youtube.com/watch?v=" + videoId;

                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youTubeUri));

                                intent.setComponent(
                                        new ComponentName("com.google.android.youtube",
                                                "com.google.android.youtube.PlayerActivity"));

                                try {
                                    getContext().startActivity(intent);
                                } catch (ActivityNotFoundException ex) {

                                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youTubeUri));
                                    getContext().startActivity(intent);
                                }
                            }
                        }
                    });

                    trailersContainer.addView(rootView);
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String>> loader) {

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
