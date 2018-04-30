package com.quagem.popularmovies;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.quagem.popularmovies.fragments.MediaDetailFragment;

public class MediaDetailActivity extends AppCompatActivity {

    public static final String ARG_MEDIA_ID = "argMediaId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_container);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, new MediaDetailFragment()).commit();
    }

}
