package com.ozan_kalan.popular_movies_stage1.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ozan.kalan on 6/17/17.
 */

public class FavMoviesContract {

    public static final String AUTHORITY =  "com.ozan_kalan.popular_movies_stage1";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String TABLE_NAME = "movies";

        // Since TaskEntry implements the interface "BaseColumns", it has an automatically produced
        // "_ID" column in addition to the two below
        public static final String COLUMN_MOVIE_ID = "movieID";
        public static final String COLUMN_ORIG_TITLE = "origTitle";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_RATING = "rating";


    }

}
