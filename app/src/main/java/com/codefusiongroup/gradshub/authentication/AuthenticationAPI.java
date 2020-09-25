package com.codefusiongroup.gradshub.authentication;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface AuthenticationAPI {

    @POST("User/login.php")
    Call<JsonObject> loginUser(@Body HashMap<String, String> params);


    @POST("User/register.php")
    Call<JsonObject> registerUser(@Body HashMap<String, String> params);


    @POST("User/resetpassword.php")
    Call<JsonObject> requestPasswordReset(@Body Map<String, String> params);

}