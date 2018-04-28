package com.quagem.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieListAdaptor extends BaseAdapter {

    public static final String TAG = MovieListAdaptor.class.getSimpleName();

    private Activity activity;
    private List<MovieDataType> listData;

    public MovieListAdaptor(Activity activity, List<MovieDataType> listData) {
        this.activity = activity;
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return listData.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ImageView imageView;

        if (view == null)
            view = activity.getLayoutInflater().inflate(
                    R.layout.list_item_movie, viewGroup, false);

        imageView = view.findViewById(R.id.iv_movie_poster);

        Picasso.with(activity)
                .load(TMDBNetworkTools.getPosterUri(listData.get(i).getPosterPath()))
                .placeholder(R.drawable.ic_launcher_background) // TODO: 4/27/18
                .error(R.drawable.ic_launcher_background) // TODO: 4/27/18
                .into(imageView);

        return imageView;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
    
}
