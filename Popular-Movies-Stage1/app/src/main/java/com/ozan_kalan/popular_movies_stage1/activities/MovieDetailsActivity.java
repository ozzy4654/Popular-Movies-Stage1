package com.ozan_kalan.popular_movies_stage1.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ozan_kalan.popular_movies_stage1.Models.ReviewList;
import com.ozan_kalan.popular_movies_stage1.Models.ReviewResults;
import com.ozan_kalan.popular_movies_stage1.Models.VideoResults;
import com.ozan_kalan.popular_movies_stage1.Models.Videos;
import com.ozan_kalan.popular_movies_stage1.R;
import com.ozan_kalan.popular_movies_stage1.RecyclerView.RecyclerViewMovieAdapter;
import com.ozan_kalan.popular_movies_stage1.RecyclerView.RecyclerViewReviewAdapter;
import com.ozan_kalan.popular_movies_stage1.RecyclerView.RecyclerViewTrailerAdapter;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MovieDetailsActivity extends AppCompatActivity implements RecyclerViewTrailerAdapter.TrailerAdapterOnClickHandler, RecyclerViewReviewAdapter.ReviewAdapterOnClickHandler {

    public static final String MOVIE_POSTER = "poster_url";
    public static final String MOVIE_TITLE = "movie_title";
    public static final String MOVIE_RATING = "movie_rating";
    public static final String MOVIE_OVERVIEW = "movie_desc";
    public static final String MOVIE_DATE = "movie_date";
    public static final String MOVIE_ID = "movie_id";

    private RecyclerViewTrailerAdapter mTrailerAdapter;
    private RecyclerViewReviewAdapter mReviewAdapter;
    private Gson mGson;

    @BindView(R.id.movie_title_txt_view) TextView mTitle;
    @BindView(R.id.release_date_txt_view) TextView mReleaseDate;
    @BindView(R.id.rating_txt_view) TextView mRating;
    @BindView(R.id.movie_poster_img_view) ImageView mPoster;
    @BindView(R.id.overview_txt_view) TextView mOverview;

    @BindView(R.id.trailers_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.reviews_recycler_view) RecyclerView mReviewRecyclerView;

    private int mId = 0;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mGson = new GsonBuilder().create();
        setUpViews();
        queryVidsAndReivews(mId, getString(R.string.video_endpoint), getString(R.string.review_endpoint));



    }

    private void queryVidsAndReivews(int id, String vidEndPoint,  String reviewEndPoint) {
        try {
            String key = getString(R.string.key);
            run(id, vidEndPoint , key, true);
            run(id, reviewEndPoint, key, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(int id, String endpoint, String key, final boolean isTrailers) throws Exception {

        Request request = new Request.Builder()
                .url(getString(R.string.base_url) + id + endpoint + key)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("FUCKKKKKKK UUUU ");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                final String json = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isTrailers) {
                            Videos videos = mGson.fromJson(json, Videos.class);
                            mTrailerAdapter.setData(videos.getResults());
                        } else {
                            ReviewList reviews = mGson.fromJson(json, ReviewList.class);
                            mReviewAdapter.setData(reviews.getResults());
                        }
                    }
                });
            }
        });
    }


    private void setUpViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        LinearLayoutManager reviewLyoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mTrailerAdapter = new RecyclerViewTrailerAdapter(this);
        mRecyclerView.setAdapter(mTrailerAdapter);

        mReviewRecyclerView.setLayoutManager(reviewLyoutManager);
        mReviewAdapter = new RecyclerViewReviewAdapter(this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);


        Bundle bundle = getIntent().getExtras();
        String rating = String.valueOf(bundle.getDouble(MOVIE_RATING)) + getString(R.string.rating_out_of_ten);

        mRating.setText(rating);
        mTitle.setText(bundle.getString(MOVIE_TITLE));
        mOverview.setText(bundle.getString(MOVIE_OVERVIEW));
        mReleaseDate.setText(bundle.getString(MOVIE_DATE).substring(0, 4));
        mTitle.setText(bundle.getString(MOVIE_TITLE));
        mId = bundle.getInt(MOVIE_ID);
        Picasso.with(this).load(getString(R.string.image_base_url) + bundle.getString(MOVIE_POSTER))
                .into(mPoster);
    }

    @Override
    public void onClick(VideoResults videoResults) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.you_tube) + videoResults.getKey())));
    }

    @Override
    public void onClick(ReviewResults reviewResults) {
        //no op
    }
}
