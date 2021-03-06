package com.ozan_kalan.popular_movies_stage1.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.ozan_kalan.popular_movies_stage1.data.FavMoviesContract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ozan_kalan.popular_movies_stage1.activities.MovieDetailsActivity.MOVIE_DATE;
import static com.ozan_kalan.popular_movies_stage1.activities.MovieDetailsActivity.MOVIE_ID;
import static com.ozan_kalan.popular_movies_stage1.activities.MovieDetailsActivity.MOVIE_OVERVIEW;
import static com.ozan_kalan.popular_movies_stage1.activities.MovieDetailsActivity.MOVIE_POSTER;
import static com.ozan_kalan.popular_movies_stage1.activities.MovieDetailsActivity.MOVIE_RATING;
import static com.ozan_kalan.popular_movies_stage1.activities.MovieDetailsActivity.MOVIE_TITLE;

public class MainActivity extends AppCompatActivity implements
        RecyclerViewMovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String SEARCH_CATEGORY = "category";
    public static final String SHARED_PRFS = "movies";
    public static final String SHARED_POP = "pop";
    public static final String SHARED_FAV = "fav";
    public static final String SHARED_TITLE = "title";
    public static final String SHARED_SEARCH = "search";

    private static final int FAV_MOVIE_LOADER_ID = 0;
    private static String BASE_URL;

    private RecyclerViewMovieAdapter mMovieAdapter;
    private Gson mGson;
    private GridLayoutManager layoutManager;

    private MovieList mMovieList;
    private String mTopRated;
    private String mPopMovies;
    private String mKey;
    private SharedPreferences.Editor editor;

    @BindView(R.id.movies_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.network_error) TextView mError;
    @BindView(R.id.no_favs_txt_view) TextView mNoFavs;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        BASE_URL = getString(R.string.base_url);
        editor = getSharedPreferences(SHARED_PRFS, MODE_PRIVATE).edit();
        mTopRated = getString(R.string.top_rated);
        mPopMovies = getString(R.string.popular);
        mKey = getString(R.string.key);
        mGson = new GsonBuilder().create();

        layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mMovieAdapter = new RecyclerViewMovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        if (savedInstanceState != null) {
            setTitle(savedInstanceState.getString(SHARED_TITLE, getString(R.string.top_movie_title)));
            ArrayList<MovieResult> items = savedInstanceState.getParcelableArrayList(SEARCH_CATEGORY);
            mMovieAdapter.setData(items);

        }
        if (!getSharedPreferences(SHARED_PRFS, MODE_PRIVATE).getBoolean(SHARED_FAV, false)) {
            if (getSharedPreferences(SHARED_PRFS, MODE_PRIVATE).getBoolean(SHARED_POP, false)) {
                setTitle(R.string.pop_movie_title);
                queryMovieAPI(getString(R.string.popular), mKey);
            } else {
                setTitle(R.string.top_movie_title);
                queryMovieAPI(getString(R.string.top_rated), mKey);
            }
        } else {
            setTitle(R.string.favorites);
            getSupportLoaderManager().initLoader(FAV_MOVIE_LOADER_ID, null, this);
        }

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
        mNoFavs.setVisibility(View.INVISIBLE);
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
                if (!response.isSuccessful()) throw new IOException(getString(R.string.unexpected) + response);

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

    @Override
    protected void onResume() {
        super.onResume();

        if (getTitle().toString().equalsIgnoreCase(getString(R.string.favorites)))
            getSupportLoaderManager().restartLoader(FAV_MOVIE_LOADER_ID, null, this);
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
        mMovieAdapter.setData(mMovieList.movieResults);
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
            editor.putBoolean(SHARED_POP, true).apply();
            editor.putBoolean(SHARED_FAV, false).apply();

            return true;
        }
        if (item.getItemId() == R.id.menu_top_rated) {

            setTitle(R.string.top_movie_title);
            queryMovieAPI(mTopRated, mKey);
            editor.putBoolean(SHARED_POP, false).apply();
            editor.putBoolean(SHARED_FAV, false).apply();

            return true;
        }

        if (item.getItemId() == R.id.menu_favorites) {

            setTitle(R.string.favorites);
            getSupportLoaderManager().restartLoader(FAV_MOVIE_LOADER_ID, null, this);
            editor.putBoolean(SHARED_FAV, true).apply();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle output) {
        super.onSaveInstanceState(output);

        if (getTitle().toString().equalsIgnoreCase(getString(R.string.favorites)))
            return;

        output.putParcelableArrayList(SEARCH_CATEGORY, new ArrayList<Parcelable>(mMovieAdapter.getData()));

        if (getTitle().toString().equalsIgnoreCase(getString(R.string.pop_movie_title))) {
            output.putString(SHARED_SEARCH, getString(R.string.popular));
            output.putString(SHARED_TITLE, getString(R.string.pop_movie_title));
        } else {
            output.putString(SHARED_SEARCH, getString(R.string.top_rated));
            output.putString(SHARED_TITLE, getString(R.string.top_movie_title));
        }
    }

    /**
     * Handles clicking a poster and sending
     * the user to the details activity
     */
    @Override
    public void onClick(MovieResult movieResult) {
        Intent intent = new Intent(getApplicationContext(), MovieDetailsActivity.class);

        Bundle bundle = new Bundle();

        bundle.putString(MOVIE_POSTER, movieResult.posterPath);
        bundle.putString(MOVIE_OVERVIEW, movieResult.overview);
        bundle.putString(MOVIE_TITLE, movieResult.originalTitle);
        bundle.putString(MOVIE_DATE, movieResult.releaseDate);
        bundle.putDouble(MOVIE_RATING, movieResult.voteAverage);
        bundle.putInt(MOVIE_ID, movieResult.id);

        intent.putExtras(bundle);

        startActivity(intent);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(getApplicationContext()) {

            Cursor mMovieData = null;

            @Override
            protected void onStartLoading() {
                if (mMovieData != null)
                    deliverResult(mMovieData);
                forceLoad();
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(FavMoviesContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            FavMoviesContract.MovieEntry.COLUMN_ORIG_TITLE);
                } catch (Exception e) {
                    Log.e(TAG, getString(R.string.failed));
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.getCount() == 1) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mNoFavs.setVisibility(View.VISIBLE);
        } else
            mNoFavs.setVisibility(View.INVISIBLE);

        int posterPathIndex = data.getColumnIndex(FavMoviesContract.MovieEntry.COLUMN_POSTER_PATH);
        int titlePathIndex = data.getColumnIndex(FavMoviesContract.MovieEntry.COLUMN_ORIG_TITLE);
        int overviewPathIndex = data.getColumnIndex(FavMoviesContract.MovieEntry.COLUMN_OVERVIEW);
        int movIdPathIndex = data.getColumnIndex(FavMoviesContract.MovieEntry.COLUMN_MOVIE_ID);
        int releasePathIndex = data.getColumnIndex(FavMoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
        int ratingPathIndex = data.getColumnIndex(FavMoviesContract.MovieEntry.COLUMN_RATING);

        List<MovieResult> mPosterData = new ArrayList<>();

        data.moveToFirst();

        while (data.moveToNext()) {
            mPosterData.add(new MovieResult(
                    data.getString(titlePathIndex),
                    data.getString(posterPathIndex),
                    data.getString(overviewPathIndex),
                    data.getString(releasePathIndex),
                    data.getInt(movIdPathIndex),
                    data.getDouble(ratingPathIndex)));
        }

        mMovieAdapter.setData(mPosterData);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //no op
    }

}
