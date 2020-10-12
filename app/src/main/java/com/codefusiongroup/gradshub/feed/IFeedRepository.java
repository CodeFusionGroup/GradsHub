package com.codefusiongroup.gradshub.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codefusiongroup.gradshub.common.models.Post;
import com.codefusiongroup.gradshub.utils.api.Resource;

import java.util.List;


public interface IFeedRepository {

    void getLatestPosts(String userID);

    void getUserLikedPosts(String userID);

    void insertUserLikedPosts(String userID, String groupID, String postID);

    LiveData<Boolean> getIsLoading();

    MutableLiveData<Resource<List<Post>>> getLatestPostsResponse();

    MutableLiveData<Resource<List<String>>> getUserLikedPostsResponse();

}
