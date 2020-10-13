package com.codefusiongroup.gradshub.friends;

import androidx.lifecycle.MutableLiveData;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.utils.api.Resource;

import java.util.List;


public interface IFriendsRepository {

    void getUserFriends(String userID);

    MutableLiveData<Boolean> getIsLoading();

    MutableLiveData<Resource<List<User>>> getUserFriendsResponse();

}
