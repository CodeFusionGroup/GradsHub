package com.codefusiongroup.gradshub.authentication.login;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.codefusiongroup.gradshub.authentication.AuthenticationAPI;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.network.ApiBaseResponse;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;


public class LoginModel implements LoginContract.ILoginModel {

    private static String TAG = "LoginModel"; // for debugging

    private final String  SUCCESS_CODE = "1";
    private static LoginModel loginModel = null;
    private final LoginContract.ILoginPresenter mPresenter;


    private LoginModel(LoginContract.ILoginPresenter presenter) {
        mPresenter = presenter;
    }


    public static LoginModel newInstance(LoginPresenter presenter) {
        if (loginModel == null) {
            loginModel = new LoginModel(presenter);
        }
        return loginModel;
    }


    @VisibleForTesting
    @Override
    public void requestUserLogin(String email, String password) {

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        AuthenticationAPI authenticationAPI = ApiProvider.getAuthApiService();
        authenticationAPI.loginUser(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                Log.i(TAG, "onResponse() executed");
                if ( response.isSuccessful() ) {
                    Log.i(TAG, "response.isSuccessful() = true");
                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        JsonObject userJO = jsonObject.getAsJsonObject("user");
                        User user = new Gson().fromJson(userJO, User.class);

                        mPresenter.setLoginResponseCode(SUCCESS_CODE);
                        mPresenter.setLoginResponseMessage(jsonObject.get("message").getAsString());
                        mPresenter.setCurrentUser(user);
                    }
                    else {
                        ApiBaseResponse apiDefault = new Gson().fromJson(jsonObject, ApiBaseResponse.class);
                        mPresenter.setLoginResponseCode( apiDefault.getStatusCode() );
                        mPresenter.setLoginResponseMessage( apiDefault.getMessage() );
                    }

                    mPresenter.onLoginRequestFinished();
                }
                else {
                    Log.i(TAG, "response.isSuccessful() = false");
                    // wouldn't normally show this to the user, but its for debugging purposes only and will be removed
                    GradsHubApplication.showToast(ApiResponseConstants.RESOURCE_LOCATION_FAILED);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.i(TAG, "onFailure() executed");
                mPresenter.setLoginResponseCode(ApiResponseConstants.SERVER_FAILURE_CODE);
                mPresenter.setLoginResponseMessage(ApiResponseConstants.SERVER_FAILURE_MSG);
                mPresenter.onLoginRequestFinished();
                t.printStackTrace();
            }

        });

    }


}
