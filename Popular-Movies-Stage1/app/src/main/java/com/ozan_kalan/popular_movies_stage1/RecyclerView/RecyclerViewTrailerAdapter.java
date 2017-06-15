package com.ozan_kalan.popular_movies_stage1.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ozan_kalan.popular_movies_stage1.Models.MovieResult;
import com.ozan_kalan.popular_movies_stage1.Models.VideoResults;
import com.ozan_kalan.popular_movies_stage1.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by ozan.kalan on 6/13/17.
 */

public class RecyclerViewTrailerAdapter extends RecyclerView.Adapter<RecyclerViewTrailerAdapter.TrailerAdapterViewHolder>{

    private List<VideoResults> mTrailerData;
    private final TrailerAdapterOnClickHandler mClickHandler;

    public interface TrailerAdapterOnClickHandler {
        void onClick(VideoResults videoResults);
    }

    public RecyclerViewTrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }



    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.trailer_item, parent, false);
        return new TrailerAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        holder.mTrailerTitle.setText(mTrailerData.get(position).getName()  );

    }
    public void setData(List<VideoResults> videoList) {
        if (mTrailerData != null)
            mTrailerData.clear();
        mTrailerData = videoList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if (mTrailerData == null)
            return 0;
        return mTrailerData.size();
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mTrailerTitle;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mTrailerTitle = (TextView) itemView.findViewById(R.id.trailer_item_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            VideoResults videoResult = mTrailerData.get(getAdapterPosition());
            mClickHandler.onClick(videoResult);

        }
    }
}
