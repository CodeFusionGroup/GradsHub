package com.codefusiongroup.gradshub.friends;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface FriendsAPI {

    @POST("User/retrieveFriends.php")
    Call<JsonObject> getUserFriends(@Body Map<String, String> params);


    @POST("User/addFriend.php")
    Call<JsonObject> addUserToFriendsList(@Body Map<String, String> params);


    @POST("User/removeFriend.php")
    Call<JsonObject> removeUserFromFriendsList(@Body Map<String, String> params);


    @POST("User/block/add.php")
    Call<JsonObject> blockUser(@Body Map<String, String> params);


    @POST("User/block/remove.php")
    Call<JsonObject> unblockUser(@Body Map<String, String> params);

}
