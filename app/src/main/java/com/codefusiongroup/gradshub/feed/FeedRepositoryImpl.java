package com.codefusiongroup.gradshub.feed;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.codefusiongroup.gradshub.common.models.Post;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.utils.api.Resource;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FeedRepositoryImpl implements IFeedRepository {

    private static String TAG = "FeedRepositoryImpl";

    private static FeedRepositoryImpl repository = null;
    private final FeedAPI feedAPI = ApiProvider.getFeedApiService();

    // constructor private so that only one instance of FeedRepositoryImpl object is created and no other
    // class can instantiate it directly
    private FeedRepositoryImpl() { }

    // singleton pattern
    public static FeedRepositoryImpl getInstance() {
        if (repository == null) {
            repository = new FeedRepositoryImpl();
        }
        return repository;
    }


    private MutableLiveData<Resource<List<Post>>> latestPostsResponse;
    private MutableLiveData<Resource<List<String>>> likedPostsResponse;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);


    @Override
    public MutableLiveData<Boolean> getIsLoading() { return isLoading; }


    @Override
    public MutableLiveData<Resource<List<Post>>> getLatestPostsResponse() {
        if (latestPostsResponse == null) {
            latestPostsResponse = new MutableLiveData<>();
        }
        return latestPostsResponse;
    }

    @Override
    public MutableLiveData<Resource<List<String>>> getUserLikedPostsResponse() {
        if (likedPostsResponse == null) {
            likedPostsResponse = new MutableLiveData<>();
        }
        return likedPostsResponse;
    }


    @Override
    public void getLatestPosts(String userID) {

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userID);

        isLoading.setValue(true);
        feedAPI.getLatestPosts(params).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                isLoading.setValue(false);
                if ( response.isSuccessful() ) {
                    Log.i(TAG, "getLatestPosts() --> response.isSuccessful() = true");

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        List<Post> latestPosts = new ArrayList<>();
                        JsonArray latestPostsJA = jsonObject.getAsJsonArray("message");

                        for (JsonElement jsonElement : latestPostsJA) {

                            JsonObject latestPostJO = jsonElement.getAsJsonObject();
                            Post post = new Gson().fromJson(latestPostJO, Post.class);

                            String postDescription;
                            JsonElement postFileJE = latestPostJO.get("POST_FILE");
                            // check if post is for a pdf file
                            if (postFileJE != null && !postFileJE.isJsonNull()) {
                                postDescription = latestPostJO.get("POST_FILE").getAsString();
                                String postFileName = latestPostJO.get("POST_FILE_NAME").getAsString();
                                post.setPostFileName(postFileName);
                            }
                            // post description is for a normal link
                            else {
                                postDescription = latestPostJO.get("POST_URL").getAsString();
                            }

                            int noLikes = 0;
                            JsonElement postLikesCountJE = latestPostJO.get("NO_OF_LIKES");
                            if (postLikesCountJE != null && !postLikesCountJE.isJsonNull()) {
                                noLikes = Integer.parseInt(latestPostJO.get("NO_OF_LIKES").getAsString());
                            }

                            int noComments = 0;
                            JsonElement postCommentsCountJE = latestPostJO.get("NO_OF_COMMENTS");
                            if (postCommentsCountJE != null && !postCommentsCountJE.isJsonNull()) {
                                noComments = Integer.parseInt(latestPostJO.get("NO_OF_COMMENTS").getAsString());
                            }

                            post.setPostLikesCount(noLikes);
                            post.setPostCommentsCount(noComments);
                            post.setPostDescription(postDescription);
                            latestPosts.add(post);
                        }

                        latestPostsResponse.setValue( Resource.apiDataRequestSuccess(latestPosts, null) );

                    }
                    // no latest posts exists yet
                    else {
                        String message ="No latest posts available, join a group if you don't belong to one." ;
                        latestPostsResponse.setValue( Resource.apiNonDataRequestSuccess(message) );
                    }

                }

                else {
                    latestPostsResponse.setValue( Resource.error("Could not load feed please refresh page.") );
                    Log.i(TAG, "requestUserLogin() --> response.isSuccessful() = false");
                    Log.i(TAG, "error code: " +response.code() );
                    Log.i(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                latestPostsResponse.setValue( Resource.error("Could not load feed please refresh page.") );
                Log.i(TAG, "getLatestPosts() --> onFailure executed, error: ", t);
            }
        });

    }


    @Override
    public void getUserLikedPosts(String userID) {

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userID);

        feedAPI.getUserLikedPosts(params).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if ( response.isSuccessful() ) {
                    Log.i(TAG, "getUserLikedPosts() --> response.isSuccessful() = true");

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        List<String> userLikedPosts = new ArrayList<>();
                        JsonArray likedPostsJA = jsonObject.getAsJsonArray("message");
                        for (int i = 0; i < likedPostsJA.size(); i++) {
                            userLikedPosts.add(likedPostsJA.get(i).getAsString());
                        }
                        likedPostsResponse.setValue( Resource.apiDataRequestSuccess(userLikedPosts, null) );
                    }
                    else {
                        Log.i(TAG, "api response: no like posts for this user");
                    }

                }

                else {
                    likedPostsResponse.setValue( Resource.error("Could not update feed correctly please refresh page.") );
                    Log.i(TAG, "getUserLikedPosts() --> response.isSuccessful() = false");
                    Log.i(TAG, "error code: " +response.code() );
                    Log.i(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                likedPostsResponse.setValue( Resource.error("Could not update feed correctly please refresh page.") );
                Log.i(TAG, "getUserLikedPosts() --> onFailure executed, error: ", t);
            }
        });

    }


    @Override
    public void insertUserLikedPosts(String userID, String groupID, List<String> likedPosts) {

        StringBuilder likedPostsIDs = new StringBuilder();

        for(int i = 0; i < likedPosts.size(); i++) {

            likedPostsIDs.append(likedPosts.get(i));

            if (i != likedPosts.size()-1) {
                likedPostsIDs.append(",");
            }
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userID);
        params.put("group_id", groupID);
        params.put("post_id", likedPostsIDs.toString());

        feedAPI.insertFeedLikedPosts(params).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if ( response.isSuccessful() ) {
                    Log.i(TAG, "insertUserLikedPosts() --> response.isSuccessful() = true");
                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {
                        // no toast messages shown yet
                        Log.i(TAG, "api response: inserted liked posts");
                    }
                    else {
                        Log.i(TAG, "api response: no like posts for this user");
                    }

                }

                else {
                    Log.i(TAG, "insertUserLikedPosts() --> response.isSuccessful() = false");
                    Log.i(TAG, "error code: " +response.code() );
                    Log.i(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i(TAG, "insertUserLikedPosts() --> onFailure executed, error: ", t);
            }
        });

    }


    public void deregisterObserverObjects() {
        Log.i(TAG, "deregisterObserverObjects() --> live data objects set to null");
        likedPostsResponse.setValue(null);
        latestPostsResponse.setValue(null);
        isLoading.setValue(null);
    }

}
