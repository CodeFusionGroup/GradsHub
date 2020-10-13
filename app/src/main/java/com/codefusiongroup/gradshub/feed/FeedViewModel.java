package com.codefusiongroup.gradshub.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codefusiongroup.gradshub.common.models.Post;
import com.codefusiongroup.gradshub.utils.api.Resource;
import com.codefusiongroup.gradshub.utils.viewmodels.ObservableViewModel;

import java.util.List;


public class FeedViewModel extends ObservableViewModel {


    private FeedRepositoryImpl repository;

    public FeedViewModel() {
        repository = FeedRepositoryImpl.getInstance();
    }

    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Resource<List<Post>>> latestPostsResponse;
    //private MutableLiveData<Resource<String>> insertLikesResponse;

    public void getLatestPosts(String userID) {
        repository.getLatestPosts(userID);
    }

//    public void insertFeedLikedPosts(String userID, String groupID, String postID) {
//        repository.insertUserLikedPosts(userID, groupID, postID);
//    }


    public LiveData<Boolean> getIsLoading() {
        if (isLoading == null) {
            isLoading = repository.getIsLoading();
        }
        return isLoading;
    }

    public LiveData<Resource<List<Post>>> getLatestPostsResponse() {
        if (latestPostsResponse == null) {
            latestPostsResponse = repository.getLatestPostsResponse();
        }
        return latestPostsResponse;
    }

//    public LiveData<Resource<String>> getInsertLikesResponse() {
//        if (insertLikesResponse == null) {
//            insertLikesResponse = repository.getInsertLikesResponse();
//        }
//        return insertLikesResponse;
//    }

    public void deregisterObserverObjects() {
        latestPostsResponse.setValue(null);
        //insertLikesResponse.setValue(null);
    }

}
