package com.codefusiongroup.gradshub.common.repositories;

import androidx.lifecycle.LiveData;

import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.utils.api.Resource;


public interface IUserRepository {

    LiveData<Boolean> getIsLoading();

    LiveData<Resource<User>> getLoginResponse();

    LiveData<Resource<String>> getRegisterResponse();

    LiveData<Resource<String>> getPasswordResetResponse();

    void loginUser(String email, String password);

    void registerUser(User user);

    void requestPasswordReset(String email);

    void updateUserToken(String userID, String token);

}
