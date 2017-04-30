package com.ozan_kalan.popular_movies_stage1.activities;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ozan_kalan.popular_movies_stage1.Models.MovieList;
import com.ozan_kalan.popular_movies_stage1.Models.MovieResult;
import com.ozan_kalan.popular_movies_stage1.R;
import com.ozan_kalan.popular_movies_stage1.RecyclerView.RecyclerViewMovieAdapter;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ozan_kalan.popular_movies_stage1.activities.MovieDetailsActivity.MOVIE_DATE;
import static com.ozan_kalan.popular_movies_stage1.activities.MovieDetailsActivity.MOVIE_OVERVIEW;
import static com.ozan_kalan.popular_movies_stage1.activities.MovieDetailsActivity.MOVIE_POSTER;
import static com.ozan_kalan.popular_movies_stage1.activities.MovieDetailsActivity.MOVIE_RATING;
import static com.ozan_kalan.popular_movies_stage1.activities.MovieDetailsActivity.MOVIE_TITLE;

public class MainActivity extends AppCompatActivity implements RecyclerViewMovieAdapter.MovieAdapterOnClickHandler {

    private static final String SEARCH_CATEGORY = "category";
    private static String BASE_URL;

    private RecyclerViewMovieAdapter mMovieAdapter;
    private Gson mGson;

    private MovieList mMovieList;
    private String mTopRated;
    private String mPopMovies;
    private String mKey;

    @BindView(R.id.movies_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.network_error)
    TextView mError;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.top_movie_title);
        ButterKnife.bind(this);

        BASE_URL = getString(R.string.base_url);

        mTopRated = getString(R.string.top_rated);
        mPopMovies = getString(R.string.popular);
        mKey = getString(R.string.key);

        mGson = new GsonBuilder().create();

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);
        mMovieAdapter = new RecyclerViewMovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        queryMovieAPI(mTopRated, mKey);
    }

    /**
     * if we have a network connection will
     * build and run our query otherwise we will
     * notify the user of a network issue
     */
    private void queryMovieAPI(String query, String key) {
        try {

            if (isOnline())
                run(query, key);
            else
                showError();

        } catch (Exception e) {
            e.printStackTrace();
            showError();
        }
    }


    /**
     * In the event a network error occurred
     * this methond will tell notify the user
     */
    private void showError() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mError.setVisibility(View.VISIBLE);
        mError.setText(getString(R.string.error));
    }

    /**
     * This method allow the app to check for network changes
     * so in the event of Network/wifi is down or in airplane mode
     * the app will not crash
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * This method will build our query and request
     * to call the Api to retrieve the data.
     * it will also handle a request failure
     */
    public void run(String endPoint, String key) throws Exception {

        Request request = new Request.Builder()
                .url(BASE_URL + endPoint + key)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showError();
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
                        setAdapter(json);
                    }
                });
            }
        });
    }

    /**
     * Now that we have the data from API
     * we can set our adapter and show the posters
     * to the user
     */
    private void setAdapter(String json) {
        mRecyclerView.setVisibility(View.VISIBLE);
        mError.setVisibility(View.INVISIBLE);

        mMovieList = mGson.fromJson(json, MovieList.class);
        mMovieAdapter.setData(mMovieList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_pop_movies) {

            setTitle(R.string.pop_movie_title);
            queryMovieAPI(mPopMovies, mKey);

            return true;
        }
        if (item.getItemId() == R.id.menu_top_rated) {

            setTitle(R.string.top_movie_title);
            queryMovieAPI(mTopRated, mKey);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles clicking a poster and sending
     * the user to the details activity
     */
    @Override
    public void onClick(MovieResult movieResult) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);

        Bundle bundle = new Bundle();

        if (getTitle().equals(getString(R.string.top_movie_title))) {
            bundle.putString(SEARCH_CATEGORY, mTopRated);

        } else
            bundle.putString(SEARCH_CATEGORY, mPopMovies);


        bundle.putString(MOVIE_POSTER, movieResult.posterPath);
        bundle.putString(MOVIE_OVERVIEW, movieResult.overview);
        bundle.putString(MOVIE_TITLE, movieResult.originalTitle);
        bundle.putString(MOVIE_DATE, movieResult.releaseDate);
        bundle.putDouble(MOVIE_RATING, movieResult.voteAverage);

        intent.putExtras(bundle);

        startActivity(intent);
    }
}
