package com.ozan_kalan.popular_movies_stage1.Services;

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

    public static final String EXTRA_KEY_OUT = "RESULTS";
    public static final String FAILURE = "hasFaild";
    public static final String ACTION_MyIntentService =
            "com.ozan_kalan.popular_movies_stage1.Services.GetMoviesService.RESPONSE";

    private String mKey;
    private String endPoint;
    private String baseUrl;

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
        OkHttpClient client = new OkHttpClient();

        mKey = intent.getStringExtra("mKey");
        endPoint = intent.getStringExtra("endPoint");
        baseUrl = intent.getStringExtra("baseUrl");

        Request request = new Request.Builder()
                .url(baseUrl + endPoint + mKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                sendIntentResponse(true, null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException(getString(R.string.unexpected) + response);
                final String json = response.body().string();
                sendIntentResponse(false, json);

            }
        });

    }


    /**
     * This method will send a intent response back to our main activity. Which our activity is
     * listening for. This will send back our json or a null json if there was failure. Our main
     * activity will handle what to do from there.
     */
    private void sendIntentResponse(@Nullable Boolean hasFailed, @Nullable String json) {

        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_MyIntentService);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(EXTRA_KEY_OUT, json);
        intentResponse.putExtra(FAILURE, hasFailed);

        sendBroadcast(intentResponse);
        stopSelf();
    }
}
