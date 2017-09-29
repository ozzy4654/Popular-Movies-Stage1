package com.ozan_kalan.popular_movies_stage1.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.ozan_kalan.popular_movies_stage1.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ozan-laptop on 9/27/2017.
 */

public class GetMoviesService extends IntentService {

    public static final String MOVIE_RESULTS = "RESULTS";
    public static final String REVIEW_RESULTS = "REVIEW_RESULTS";
    public static final String TRAILER_RESULTS = "TRAILER_RESULTS";
    public static final String FAILURE = "hasFaild";
    public static final String ACTION_MyIntentService = "com.ozan_kalan.popular_movies_stage1.Services.GetMoviesService.RESPONSE";

    private String mKey;
    private String mMoviesEndPoint;
    private String mTrailersEP;
    private String mReviewEP;
    private String mBaseUrl;
    private int mId;

    private static String mTrailers;
    private static String mReviews;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.*
     */
    public GetMoviesService() {
        super("com.ozan_kalan.popular_movies_stage1.Services.GetMoviesService");
    }


    /**
     * This method will build our query and request
     * to call the Api to retrieve the data.
     * it will also handle a request failure
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        mKey = intent.getStringExtra("mKey");
        mMoviesEndPoint = intent.getStringExtra("endPoint");
        mBaseUrl = intent.getStringExtra("baseUrl");
        mId = intent.getIntExtra("id", 0);
        mTrailersEP = intent.getStringExtra("trailersEP");
        mReviewEP = intent.getStringExtra("reviewEP");

        Request request;
        if (mMoviesEndPoint != null){
            request = new Request.Builder()
                    .url(mBaseUrl + mMoviesEndPoint + mKey)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(mBaseUrl + mId + mTrailersEP + mKey)
                    .build();
        }

        try {
            run(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method will send a intent response back to our main activity. Which our activity is
     * listening for. This will send back our json or a null json if there was failure. Our main
     * activity will handle what to do from there.
     */
    private void sendIntentResponse(Boolean hasFailed, @Nullable String moviesJson,
                                    @Nullable String trailersJson, @Nullable String reviewsJson) {

        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_MyIntentService);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(MOVIE_RESULTS, moviesJson);
        intentResponse.putExtra(TRAILER_RESULTS, trailersJson);
        intentResponse.putExtra(REVIEW_RESULTS, reviewsJson);

        intentResponse.putExtra(FAILURE, hasFailed);

        sendBroadcast(intentResponse);
        stopSelf();
    }

    public void run(final Request request) throws Exception {

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                sendIntentResponse(true, null, null, null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException(getString(R.string.unexpected) + response);
                final String json = response.body().string();

                if (mMoviesEndPoint != null) {
                    sendIntentResponse(false,json, null, null);

                } else if(mTrailersEP != null && request.url().toString().contains(mTrailersEP)) {
                    mTrailers = json;
                    Request request = new Request.Builder()
                            .url(mBaseUrl + mId + mReviewEP + mKey)
                            .build();
                    try {
                        run(request);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    mReviews = json;
                    sendIntentResponse(false, null, mTrailers, mReviews);
                }
            }
        });
    }
}
