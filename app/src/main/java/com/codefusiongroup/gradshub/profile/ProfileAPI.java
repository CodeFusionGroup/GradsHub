package com.codefusiongroup.gradshub.profile;

import com.google.gson.JsonObject;

import java.util.HashMap;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface ProfileAPI {

    @POST("User/updateUserProfile.php")
    Call<JsonObject> updateUserProfile(@Body HashMap<String, String> params);


    @POST("User/getUserUpdatedProfile.php")
    Call<JsonObject> getUserUpdatedProfile(@Body HashMap<String, String> params);


}
