package com.codefusiongroup.gradshub.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codefusiongroup.gradshub.common.models.Post;
import com.codefusiongroup.gradshub.utils.api.Resource;
import com.codefusiongroup.gradshub.utils.viewmodels.ObservableViewModel;

import java.util.List;


public class FeedViewModel extends ObservableViewModel {


    private FeedRepositoryImpl repository;
    private String userID, groupID;
    private List<String> likedPosts;

    public FeedViewModel() {
        repository = FeedRepositoryImpl.getInstance();
    }

    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Resource<List<Post>>> latestPostsResponse;
    private MutableLiveData<Resource<List<String>>> likedPostsResponse;


    public void getLatestPosts(String userID) {
        repository.getLatestPosts(userID);
    }

    public void getUserLikedPosts(String userID) { repository.getUserLikedPosts(userID); }


    public void insertFeedLikedPosts(String userID, String groupID, List<String> likedPosts) {
        this.userID = userID;
        this.groupID = groupID;
        this.likedPosts = likedPosts;
    }


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

    public LiveData<Resource<List<String>>> getLikedPostsResponse() {
        if (likedPostsResponse == null) {
            likedPostsResponse = repository.getUserLikedPostsResponse();
        }
        return likedPostsResponse;
    }

//    public void deregisterObserverObjects() {
//        latestPostsResponse.setValue(null);
//        likedPostsResponse.setValue(null);
//    }

    @Override
    public void onCleared() {
        //repository.insertUserLikedPosts(userID, groupID, likedPosts);
        repository.deregisterObserverObjects();
    }

}
