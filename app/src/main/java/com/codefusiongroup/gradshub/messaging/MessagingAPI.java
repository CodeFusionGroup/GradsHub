package com.codefusiongroup.gradshub.messaging;

import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface MessagingAPI {


    @POST("User/updatefcmtoken.php")
    Call<JsonObject> updateUserFCMToken(@Body HashMap<String, String> params);


    @POST("User/retrievechats.php")
    Call<JsonObject> fetchOpenChats(@Body HashMap<String, String> params);


    @POST("Chat/retrievemessages.php")
    Call<JsonObject> fetchChatMessages(@Body HashMap<String, String> params);


    @POST("Message/create.php")
    Call<JsonObject> insertMessage(@Body HashMap<String, String> params);

    @POST("User/closeChat.php")
    Call<JsonObject> removeChats(@Body HashMap<String, String> params);


}
