package com.codefusiongroup.gradshub.authentication;

import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface AuthenticationAPI {

    @POST("User/login.php")
    Call<JsonObject> loginUser(@Body HashMap<String, String> params);


    @POST("User/register.php")
    Call<JsonObject> registerUser(@Body HashMap<String, String> params);

}