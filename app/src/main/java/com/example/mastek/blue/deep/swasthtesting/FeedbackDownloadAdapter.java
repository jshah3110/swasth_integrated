package com.example.mastek.blue.deep.swasthtesting;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Mitali on 25-03-2016.
 */
public final class FeedbackDownloadAdapter {
    public static FeedbackDownloadService getRetrofitBuilder() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://swasth-india.esy.es/swasth/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(FeedbackDownloadService.class);
    }
}
