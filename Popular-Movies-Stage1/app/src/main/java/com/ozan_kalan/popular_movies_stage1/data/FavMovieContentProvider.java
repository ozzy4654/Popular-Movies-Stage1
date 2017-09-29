package com.ozan_kalan.popular_movies_stage1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.ozan_kalan.popular_movies_stage1.R;

import static com.ozan_kalan.popular_movies_stage1.data.FavMoviesContract.MovieEntry.TABLE_NAME;

/**
 * Created by ozan.kalan on 6/17/17.
 */

public class FavMovieContentProvider extends ContentProvider {

    // Define final integer constants for the directory of tasks and a single item.
    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory.
    public static final int FAV_MOVIES = 100;
    public static final int FAV_MOVIES_WITH_ID = 101;

    private static Context context;


    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     Initialize a new matcher object without any matches,
     then use .addURI(String authority, String path, int match) to add matches
     */
    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(FavMoviesContract.AUTHORITY, FavMoviesContract.PATH_MOVIES, FAV_MOVIES);
        uriMatcher.addURI(FavMoviesContract.AUTHORITY, FavMoviesContract.PATH_MOVIES + "/#", FAV_MOVIES_WITH_ID);

        return uriMatcher;
    }


    // Member variable for a TaskDbHelper that's initialized in the onCreate() method
    private FavMoviesDBHelper mFavMoviesDBHelper;


    @Override
    public boolean onCreate() {
        context = getContext();
        mFavMoviesDBHelper = new FavMoviesDBHelper(context);
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mFavMoviesDBHelper.getReadableDatabase();

        int match =  sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {

            case FAV_MOVIES:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case FAV_MOVIES_WITH_ID:

                String id = uri.getPathSegments().get(1);

                // Selection is the movieID column = ?, and the Selection args = the row ID from the URI
                String[] mSelectionArgs = new String[]{id};

                // Construct a query as you would normally, passing in the selection/args
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        context.getString(R.string.movie_with_id_q),
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException(context.getString(R.string.unknown_uri) + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException(context.getString(R.string.not_implemented));
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFavMoviesDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAV_MOVIES:
                long id = db.insert(TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavMoviesContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = mFavMoviesDBHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            case FAV_MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                System.out.println("MOVIE ID =  " + id);
                tasksDeleted = db.delete(TABLE_NAME, "movieID=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
            // Handle the single
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
