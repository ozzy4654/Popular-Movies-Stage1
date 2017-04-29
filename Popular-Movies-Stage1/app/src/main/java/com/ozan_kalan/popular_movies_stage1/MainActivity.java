package com.ozan_kalan.popular_movies_stage1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ozan_kalan.popular_movies_stage1.Models.MovieList;
import com.ozan_kalan.popular_movies_stage1.RecyclerView.RecyclerViewMovieAdapter;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerViewMovieAdapter movieAdaper;
    private MovieList movieList;
    private String json;

    @BindView(R.id.movies_recycler_view) RecyclerView mRecyclerView;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        String url = getString(R.string.base_url) + getString(R.string.top_rated);
        String keyy =url + "=" + getString(R.string.key);

        try {
            run(keyy);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                final String jsons = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        json = jsons;
                        setAdapter();
                    }
                });

            }
        });
    }

    private void setAdapter() {
        Gson gson = new GsonBuilder().create();
        movieList = gson.fromJson(json, MovieList.class);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);
        movieAdaper = new RecyclerViewMovieAdapter(movieList.movieResults);
        mRecyclerView.setAdapter(movieAdaper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_pop_movies) {


            return true;
        }
        if(item.getItemId() == R.id.menu_top_rated) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
