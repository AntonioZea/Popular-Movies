package com.quagem.screentrends;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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

        if (view == null)
            view = activity.getLayoutInflater().inflate(
                    R.layout.grid_item, viewGroup, false);

        final ImageView imageView = view.findViewById(R.id.iv_movie_poster);

        Picasso.with(activity)
                .load(NetworkTools.getImageUri(listData.get(i).getPosterPath(),
                        NetworkTools.TMDB_IMAGE_W185))
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_error)
                .into(imageView);

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
    
}
