package com.codefusiongroup.gradshub.groups;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface GroupsAPI {


    @POST("User/listgroups.php")
    Call<JsonObject> getUserGroups(@Body Map<String, String> params);


    @POST("User/joingroup.php")
    Call<JsonObject> requestToJoinGroup(@Body Map<String, String> params);


    @POST("GroupPost/retrieveAll.php")
    Call<JsonObject> getGroupPosts(@Body Map<String, String> params);


    @POST("Group/create.php")
    Call<JsonObject> createGroup(@Body Map<String, String> params);


    @POST("GroupPost/retrievelikes.php")
    Call<JsonObject> getUserLikedGroupPosts(@Body Map<String, String> params);


    @POST("User/availablegroups.php")
    Call<JsonObject> getGroupsToExplore(@Body Map<String, String> params);


    @POST("GroupPost/insertlikes.php")
    Call<JsonObject> updateGroupPostLikes(@Body HashMap<String, String> params);


}
