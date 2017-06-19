package com.ozan_kalan.popular_movies_stage1.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ozan_kalan.popular_movies_stage1.Models.ReviewResults;
import com.ozan_kalan.popular_movies_stage1.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by ozan.kalan on 6/16/17.
 */

public class RecyclerViewReviewAdapter extends RecyclerView.Adapter<RecyclerViewReviewAdapter.ReviewAdapterViewHolder> {

    private List<ReviewResults> mReviewData;
    private final ReviewAdapterOnClickHandler mClickHandler;

    public interface ReviewAdapterOnClickHandler { void onClick(ReviewResults reviewResults);}

    public RecyclerViewReviewAdapter(ReviewAdapterOnClickHandler clickHandler) { mClickHandler = clickHandler;}


    @Override
    public RecyclerViewReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.review_item, parent, false);
        return new ReviewAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewReviewAdapter.ReviewAdapterViewHolder holder, int position) {
        holder.mAuthor.setText(mReviewData.get(position).getAuthor());
        holder.mAutherReview.setText(mReviewData.get(position).getContent());
    }


    public void setData(List<ReviewResults> reviewList) {
        if (mReviewData != null)
            mReviewData.clear();
        mReviewData = reviewList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mReviewData == null)
            return 0;
        else
           return mReviewData.size();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mAuthor;
        final TextView mAutherReview;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            mAuthor = (TextView) itemView.findViewById(R.id.author_txt_view);
            mAutherReview = (TextView) itemView.findViewById(R.id.author_review_txt);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ReviewResults reviewResults = mReviewData.get(getAdapterPosition());
            mClickHandler.onClick(reviewResults);
        }
    }
}
