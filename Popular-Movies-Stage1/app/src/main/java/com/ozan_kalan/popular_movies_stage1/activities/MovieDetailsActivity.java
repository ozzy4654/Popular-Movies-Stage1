package com.ozan_kalan.popular_movies_stage1.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ozan_kalan.popular_movies_stage1.Models.ReviewList;
import com.ozan_kalan.popular_movies_stage1.Models.ReviewResults;
import com.ozan_kalan.popular_movies_stage1.Models.VideoResults;
import com.ozan_kalan.popular_movies_stage1.Models.Videos;
import com.ozan_kalan.popular_movies_stage1.R;
import com.ozan_kalan.popular_movies_stage1.RecyclerView.RecyclerViewReviewAdapter;
import com.ozan_kalan.popular_movies_stage1.RecyclerView.RecyclerViewTrailerAdapter;
import com.ozan_kalan.popular_movies_stage1.data.FavMoviesContract;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MovieDetailsActivity extends AppCompatActivity implements RecyclerViewTrailerAdapter.TrailerAdapterOnClickHandler, RecyclerViewReviewAdapter.ReviewAdapterOnClickHandler {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();
    public static final String MOVIE_POSTER = "poster_url";
    public static final String MOVIE_TITLE = "movie_title";
    public static final String MOVIE_RATING = "movie_rating";
    public static final String MOVIE_OVERVIEW = "movie_desc";
    public static final String MOVIE_DATE = "movie_date";
    public static final String MOVIE_ID = "movie_id";

    private RecyclerViewTrailerAdapter mTrailerAdapter;
    private RecyclerViewReviewAdapter mReviewAdapter;
    private Gson mGson;
    private int mId = 0;
    private Bundle bundle;

    @BindView(R.id.movie_title_txt_view) TextView mTitle;
    @BindView(R.id.release_date_txt_view) TextView mReleaseDate;
    @BindView(R.id.rating_txt_view) TextView mRating;
    @BindView(R.id.movie_poster_img_view) ImageView mPoster;
    @BindView(R.id.overview_txt_view) TextView mOverview;
    @BindView(R.id.fav_btn) Button mFavBtn;

    @BindView(R.id.trailers_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.reviews_recycler_view) RecyclerView mReviewRecyclerView;

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
                        Log.e(TAG, getString(R.string.failed));
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
        LinearLayoutManager reviewLyoutManager = new LinearLayoutManager(this);
        mReviewRecyclerView.setLayoutManager(reviewLyoutManager);
        mReviewAdapter = new RecyclerViewReviewAdapter(this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mTrailerAdapter = new RecyclerViewTrailerAdapter(this);
        mRecyclerView.setAdapter(mTrailerAdapter);

        bundle = getIntent().getExtras();
        String rating = String.valueOf(bundle.getDouble(MOVIE_RATING)) + getString(R.string.rating_out_of_ten);

        mRating.setText(rating);
        mTitle.setText(bundle.getString(MOVIE_TITLE));
        mOverview.setText(bundle.getString(MOVIE_OVERVIEW));
        mReleaseDate.setText(bundle.getString(MOVIE_DATE).substring(0, 4));
        mTitle.setText(bundle.getString(MOVIE_TITLE));
        mId = bundle.getInt(MOVIE_ID);
        isInFavs();

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


    @OnClick(R.id.fav_btn)
    public void onClick() {
        if (mFavBtn.getText().toString().equalsIgnoreCase(getString(R.string.mark_fav)))
            addFav();
        else
            removeFav();
    }


    /**
     * This method checks to see if this movie is already in the DB
     * if the getCount is greater than 0 than true else false which sets the fav_btn text
     **/
    private void isInFavs() {
        Uri uri = FavMoviesContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(mId)).build();

        Cursor mCursor = getContentResolver().query(uri, null,
                null,
                null,
                null);
        mCursor.moveToFirst();
        int posterPathIndex = mCursor.getColumnIndex(FavMoviesContract.MovieEntry.COLUMN_POSTER_PATH);

        boolean exists = (mCursor.getCount() > 0);

        if (exists)
            mFavBtn.setText(R.string.remove_fav);
        else
            mFavBtn.setText(R.string.mark_fav);

        mCursor.close();
    }

    /**
     * For inserting a new Fav movie to the DB
     **/
    private void addFav() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(FavMoviesContract.MovieEntry.COLUMN_MOVIE_ID, mId);
        contentValues.put(FavMoviesContract.MovieEntry.COLUMN_ORIG_TITLE, mTitle.getText().toString());
        contentValues.put(FavMoviesContract.MovieEntry.COLUMN_RELEASE_DATE, mReleaseDate.getText().toString());
        contentValues.put(FavMoviesContract.MovieEntry.COLUMN_OVERVIEW, mOverview.getText().toString());
        contentValues.put(FavMoviesContract.MovieEntry.COLUMN_POSTER_PATH, bundle.getString(MOVIE_POSTER));
        contentValues.put(FavMoviesContract.MovieEntry.COLUMN_RATING, bundle.getDouble(MOVIE_RATING));

        Uri uri = getContentResolver().insert(FavMoviesContract.MovieEntry.CONTENT_URI, contentValues);

        if(uri != null)
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();

        mFavBtn.setText(R.string.remove_fav);
    }

    /**
     * For deleting a Fav movie from the DB
     **/
    private void removeFav() {

        Uri uri = FavMoviesContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(mId)).build();
        getContentResolver().delete(uri, null, null);
        mFavBtn.setText(R.string.mark_fav);
    }

}
