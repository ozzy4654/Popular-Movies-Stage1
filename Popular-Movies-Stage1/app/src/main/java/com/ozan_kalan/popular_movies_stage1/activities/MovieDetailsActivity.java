package com.ozan_kalan.popular_movies_stage1.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ozan_kalan.popular_movies_stage1.R;

import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
