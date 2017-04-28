package com.ozan_kalan.popular_movies_stage1.Application;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by ozan.kalan on 4/28/17.
 */

public class PopularMoviesStage1App extends Application {
    @Override public void onCreate() {

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
