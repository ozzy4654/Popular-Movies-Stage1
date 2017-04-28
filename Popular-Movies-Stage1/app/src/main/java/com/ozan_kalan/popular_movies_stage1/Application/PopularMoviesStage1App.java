package com.ozan_kalan.popular_movies_stage1.Application;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Picasso;

/**
 * Created by ozan.kalan on 4/28/17.
 */

public class PopularMoviesStage1App extends Application {
    @Override public void onCreate() {

        // Setup Picasso
        Picasso picassoInstance = new Picasso.Builder(this)
                .memoryCache(Cache.NONE)
                .build();
        Picasso.setSingletonInstance(picassoInstance);

        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
    }
}
