package com.codefusiongroup.gradshub.events;

import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface EventsAPI {


    @POST("User/retrievefavourites.php")
    Call<JsonObject> getUserFavouredEvents(@Body HashMap<String, String> params);


    @GET("Event/retrieveStarCount.php")
    Call<JsonObject> getEventsStars();


    @POST("Event/insertfavourite.php")
    Call<JsonObject> registerFavouredEvents(@Body HashMap<String, String> params);


    @POST("User/updatefavourites.php")
    Call<JsonObject> unRegisterFavouredEvents(@Body HashMap<String, String> params);

}
