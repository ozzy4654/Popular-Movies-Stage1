package com.ozan_kalan.popular_movies_stage1.activities;

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

public class MainActivity extends AppCompatActivity implements RecyclerViewMovieAdapter.MovieAdapterOnClickHandler{

    private RecyclerViewMovieAdapter mMovieAdapter;
    private Gson mGson;

    private MovieList mMovieList;
    private String mTopRated;
    private String mPopMovies;
    private String mKey;

    @BindView(R.id.movies_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.network_error) TextView mError;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.top_movie_title);

        ButterKnife.bind(this);

        mTopRated = getString(R.string.top_rated);
        mPopMovies = getString(R.string.popular);
        mKey = getString(R.string.key);

        mGson = new GsonBuilder().create();

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);
        mMovieAdapter = new RecyclerViewMovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        try {
            run(mTopRated, mKey);
        } catch (Exception e) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mError.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }

    }


    public void run( String endPoint, String key) throws Exception {
        String url = getString(R.string.base_url);

        Request request = new Request.Builder()
                .url(url + endPoint + key)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mRecyclerView.setVisibility(View.INVISIBLE);
                mError.setVisibility(View.VISIBLE);

            }

            @Override public void onResponse(Call call, Response response) throws IOException {
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

    private void setAdapter(String json) {
        mRecyclerView.setVisibility(View.VISIBLE);
        mError.setVisibility(View.INVISIBLE);

        mMovieList = mGson.fromJson(json, MovieList.class);
        mMovieAdapter.setData(mMovieList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_pop_movies) {
            setTitle(R.string.pop_movie_title);

            try {
                run(mPopMovies, mKey);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }
        if(item.getItemId() == R.id.menu_top_rated) {
            setTitle(R.string.top_movie_title);

            try {
                run(mTopRated, mKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(MovieResult movieResult) {
        System.out.println("FUCKCKCKCKCKCCKCKCKCKCKC");
        System.out.println(movieResult.originalTitle);
    }
}
