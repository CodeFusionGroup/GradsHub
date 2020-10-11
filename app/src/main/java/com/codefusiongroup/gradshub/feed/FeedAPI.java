package com.codefusiongroup.gradshub.feed;

import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FeedAPI {

    @POST("GroupPost/feed.php")
    Call<JsonObject> getLatestPosts(@Body HashMap<String, String> params);

}
