package com.ozan_kalan.popular_movies_stage1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.ozan_kalan.popular_movies_stage1.RecyclerView.RecyclerViewMovieAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private RecyclerViewMovieAdapter movieAdaper;

    @BindView(R.id.movies_recycler_view) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);

        String[] array = {"dkd", "kk", "dd ", "33", "rr", "oo"};

        movieAdaper = new RecyclerViewMovieAdapter();
        movieAdaper.setData(array);
        System.out.println("THE FUCKKKKKKKK     " +         movieAdaper.getItemCount());
        mRecyclerView.setAdapter(movieAdaper);
        mRecyclerView.setHasFixedSize(true);

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
