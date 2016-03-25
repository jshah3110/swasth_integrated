package com.example.mastek.blue.deep.swasthtesting;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface FeedbackDownloadService {
    @GET("jsontest.php")
    Call<Feedback[]> fetchFeedback(@Query("choice") int choice);
}
