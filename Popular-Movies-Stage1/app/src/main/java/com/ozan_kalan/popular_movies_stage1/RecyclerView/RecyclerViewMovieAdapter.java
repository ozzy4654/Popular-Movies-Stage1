package com.ozan_kalan.popular_movies_stage1.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ozan_kalan.popular_movies_stage1.Models.MovieList;
import com.ozan_kalan.popular_movies_stage1.Models.MovieResult;
import com.ozan_kalan.popular_movies_stage1.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ozan.kalan on 4/28/17.
 */

public class RecyclerViewMovieAdapter extends RecyclerView.Adapter<RecyclerViewMovieAdapter.MovieAdapterViewHolder> {
    private List<MovieResult> mPosterData;

    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(MovieResult movieResult);
    }

    public RecyclerViewMovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    public void setData(MovieList movieList) {
        if (mPosterData != null)
            mPosterData.clear();
        mPosterData = movieList.movieResults;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {

        Picasso.with(holder.mPoster.getContext())
                .load(holder.itemView.getContext().getString(R.string.image_base_url)
                        + mPosterData.get(position).posterPath)
                .into(holder.mPoster);
    }

    @Override
    public int getItemCount() {
        if (mPosterData == null)
            return 0;
        return mPosterData.size();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView mPoster;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mPoster = (ImageView) itemView.findViewById(R.id.item_main_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int x = getAdapterPosition();
            MovieResult movieResult = mPosterData.get(x);
            mClickHandler.onClick(movieResult);

        }
    }
}
