package com.ozan_kalan.popular_movies_stage1.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ozan_kalan.popular_movies_stage1.R;
import com.squareup.picasso.Picasso;

/**
 * Created by ozan.kalan on 4/28/17.
 */

public class RecyclerViewMovieAdapter extends RecyclerView.Adapter<RecyclerViewMovieAdapter.MovieAdapterViewHolder> {

    private String[] mPosterData = new String[6];

    /** Default constructor */
    public RecyclerViewMovieAdapter() {}

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    public void setData(String[] array) {
        mPosterData = array;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
//        String posterEndPoint = mPosterData[position];

        Picasso.with(holder.mPoster.getContext())
                .load(R.mipmap.ic_launcher)
                .into(holder.mPoster);

    }

    @Override
    public int getItemCount() {
        if(mPosterData == null)
            return 0;
        return mPosterData.length;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder {

        final ImageView mPoster;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mPoster = (ImageView) itemView.findViewById(R.id.item_main_poster);
        }
    }
}
