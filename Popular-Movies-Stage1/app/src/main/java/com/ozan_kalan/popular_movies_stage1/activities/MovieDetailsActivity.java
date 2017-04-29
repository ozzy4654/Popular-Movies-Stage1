package com.ozan_kalan.popular_movies_stage1.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.ozan_kalan.popular_movies_stage1.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String MOVIE_POSTER = "poster_url";
    public static final String MOVIE_TITLE = "movie_title";
    public static final String MOVIE_RATING = "movie_ratiing";
    public static final String MOVIE_OVERVIEW = "movie_desc";
    public static final String MOVIE_DATE = "movie_date";



    @BindView(R.id.movie_title_txt_view) TextView mTitle;
    @BindView(R.id.release_date_txt_view) TextView mReleaseDate;
    @BindView(R.id.rating_txt_view) TextView mRating;
    @BindView(R.id.movie_poster_img_view) ImageView mPoster;
    @BindView(R.id.overview_txt_view) TextView mOverview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpViews();

    }

    private void setUpViews() {
        Bundle bundle = getIntent().getExtras();

        ;

        mTitle.setText(bundle.getString(MOVIE_TITLE));
        mOverview.setText(bundle.getString(MOVIE_OVERVIEW));
        mReleaseDate.setText(bundle.getString(MOVIE_DATE).substring(0, 4));
        mTitle.setText(bundle.getString(MOVIE_TITLE));


        Picasso.with(this).load(getString(R.string.image_base_url) + bundle.getString(MOVIE_POSTER))
                .into(mPoster);

    }

}
