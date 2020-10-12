package com.codefusiongroup.gradshub.friends;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.utils.api.Resource;
import com.codefusiongroup.gradshub.utils.viewmodels.ObservableViewModel;

import java.util.List;


public class FriendsViewModel extends ObservableViewModel {


    private FriendsRepositoryImpl repository;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<Resource<List<User>>> userFriendsResponse;


    public FriendsViewModel() {
        repository = FriendsRepositoryImpl.getInstance();
    }


    public void getUserFriends(String userID) {
        repository.getUserFriends(userID);
    }

    public LiveData<Boolean> getIsLoading() {
        if (isLoading == null) {
            isLoading = repository.getIsLoading();
        }
        return isLoading;
    }

    public LiveData<Resource<List<User>>> getUserFriendsResponse() {
        if (userFriendsResponse == null) {
            userFriendsResponse = repository.getUserFriendsResponse();
        }
        return userFriendsResponse;
    }

    @Override
    public void onCleared() {
        repository.deregisterObserverObjects();
    }

}
