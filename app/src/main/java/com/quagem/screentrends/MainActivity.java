package com.quagem.screentrends;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.quagem.screentrends.fragments.FavoriteMoviesFragment;
import com.quagem.screentrends.fragments.PopularMoviesFragment;
import com.quagem.screentrends.fragments.TopRatedMoviesFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_container);

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, new PopularMoviesFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment fragment;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (item.getItemId()) {

            case R.id.action_sort_top_rated: fragment = new TopRatedMoviesFragment(); break;
            case R.id.action_sort_favorites: fragment = new FavoriteMoviesFragment(); break;

            default: fragment = new PopularMoviesFragment(); break;
        }

        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in , android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.container, fragment).commit();

        return true;
    }

}
