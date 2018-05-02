package com.quagem.popularmovies;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class MediaGridAdaptor extends BaseAdapter {

    private Activity activity;
    private List<MediaDataType> listData;

    public MediaGridAdaptor(Activity activity, List<MediaDataType> listData) {
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

        //if (view == null)
            view = activity.getLayoutInflater().inflate(
                    R.layout.grid_item, viewGroup, false);

        final ImageView imageView = view.findViewById(R.id.iv_movie_poster);
        final ProgressBar progressBar = view.findViewById(R.id.progressbar);

        Picasso.with(activity)
                .load(TMDBNetworkTools.getImageUri(listData.get(i).getPosterPath(),
                        TMDBNetworkTools.TMDB_IMAGE_W185))

                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageView.setImageDrawable(new BitmapDrawable(
                                activity.getResources(), bitmap));
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                       progressBar.setVisibility(View.VISIBLE);
                    }
                });

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
    
}
