package com.quagem.screentrends;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.quagem.screentrends.fragments.MediaDetailFragment;

public class MediaDetailActivity extends AppCompatActivity {

    public static final String ARG_MEDIA_ID = "argMediaId";
    public static final String ARG_IS_FAVORITE = "isFavorite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_container);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {

            String mediaId = getIntent().getStringExtra(MediaDetailActivity.ARG_MEDIA_ID);
            boolean isFavorite = getIntent().getBooleanExtra(ARG_IS_FAVORITE, false);

            // TODO: 5/3/2018 show error and quit if args are missing.

            Bundle bundle = new Bundle();
            bundle.putString(ARG_MEDIA_ID, mediaId);
            bundle.putBoolean(ARG_IS_FAVORITE, isFavorite);

            Fragment fragment = new MediaDetailFragment();

            fragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment).commit();
        }
    }

}
