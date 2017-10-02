package com.ozan_kalan.popular_movies_stage1.activities;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ozan_kalan.popular_movies_stage1.R;
import com.ozan_kalan.popular_movies_stage1.models.ReviewList;
import com.ozan_kalan.popular_movies_stage1.models.ReviewResults;
import com.ozan_kalan.popular_movies_stage1.models.VideoResults;
import com.ozan_kalan.popular_movies_stage1.models.Videos;
import com.ozan_kalan.popular_movies_stage1.adapters.RecyclerViewReviewAdapter;
import com.ozan_kalan.popular_movies_stage1.adapters.RecyclerViewTrailerAdapter;
import com.ozan_kalan.popular_movies_stage1.data.FavMoviesContract;
import com.ozan_kalan.popular_movies_stage1.services.GetMoviesService;
import com.squareup.picasso.Picasso;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ozan_kalan.popular_movies_stage1.utils.NetworkUtils.isOnline;


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
    private boolean isError = false;
    private boolean isReg = false;
    private DetailsBroadcastReceiver detailsBroadcastReceiver;

    @BindView(R.id.details_scroll_view) ScrollView mScrollView;
    @BindView(R.id.movie_title_txt_view) TextView mTitle;
    @BindView(R.id.release_date_txt_view) TextView mReleaseDate;
    @BindView(R.id.rating_txt_view) TextView mRating;
    @BindView(R.id.movie_poster_img_view) ImageView mPoster;
    @BindView(R.id.overview_txt_view) TextView mOverview;
    @BindView(R.id.fav_btn) Button mFavBtn;

    @BindView(R.id.trailers_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.reviews_recycler_view) RecyclerView mReviewRecyclerView;

    @Override
    protected void onPause() {
        if(isReg) {
            isReg = true;
            unregisterReceiver(detailsBroadcastReceiver);
        }
        super.onPause();
    }

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
        if (isOnline(this)) {
            detailsBroadcastReceiver = new DetailsBroadcastReceiver();

            //register BroadcastReceiver
            IntentFilter intentFilter = new IntentFilter(GetMoviesService.ACTION_MyIntentService);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(detailsBroadcastReceiver, intentFilter);
            isReg = true;

            Intent mServiceIntent = new Intent(this, GetMoviesService.class);
            mServiceIntent.putExtra("mKey", getString(R.string.key));
            mServiceIntent.putExtra("id", id);
            mServiceIntent.putExtra("baseUrl", getString(R.string.base_url));
            mServiceIntent.putExtra("trailersEP", vidEndPoint);
            mServiceIntent.putExtra("reviewEP", reviewEndPoint);
            this.startService(mServiceIntent);

        }else
            showError();
    }

    private void showError() {
        isError = true;
        mTitle.setVisibility(View.INVISIBLE);
        mScrollView.setVisibility(View.INVISIBLE);
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

    private class DetailsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isError){
                mTitle.setVisibility(View.VISIBLE);
                mScrollView.setVisibility(View.VISIBLE);
            }

            Videos videos = mGson.fromJson(intent.
                    getStringExtra(GetMoviesService.TRAILER_RESULTS), Videos.class);
            mTrailerAdapter.setData(videos.getResults());

            ReviewList reviews = mGson.fromJson(intent.
                    getStringExtra(GetMoviesService.REVIEW_RESULTS), ReviewList.class);
            mReviewAdapter.setData(reviews.getResults());
        }
    }
}
