package com.codefusiongroup.gradshub.authentication.login;

import android.content.Context;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.codefusiongroup.gradshub.authentication.AuthenticationAPI;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.UserPreferences;
import com.codefusiongroup.gradshub.common.network.ApiBaseResponse;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.messaging.MessagingAPI;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;


public class LoginModel implements LoginContract.ILoginModel {

    private static String TAG = "LoginModel";

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

                if ( response.isSuccessful() ) {
                    Log.i(TAG, "requestUserLogin() --> response.isSuccessful() = true");

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        JsonObject userJO = jsonObject.getAsJsonObject("user");
                        User user = new Gson().fromJson(userJO, User.class);

                        mPresenter.setLoginResponseCode(SUCCESS_CODE);
                        mPresenter.setLoginResponseMessage(jsonObject.get("message").getAsString());
                        mPresenter.setCurrentUser(user);

                        UserPreferences userPreferences = UserPreferences.getInstance();
                        Context ctx = GradsHubApplication.getContext();
                        if( userPreferences.isTokenChanged(ctx) ) {
                            // Update the FCM token
                            String token = userPreferences.getFCMToken(ctx);
                            updateUserToken(user.getUserID(),token);
                        }

                    }
                    else {
                        ApiBaseResponse apiDefault = new Gson().fromJson(jsonObject, ApiBaseResponse.class);
                        mPresenter.setLoginResponseCode( apiDefault.getStatusCode() );
                        mPresenter.setLoginResponseMessage( apiDefault.getMessage() );
                    }

                    mPresenter.onLoginRequestFinished();
                }
                else {
                    GradsHubApplication.showToast(ApiResponseConstants.SERVER_FAILURE_MSG);
                    Log.i(TAG, "requestUserLogin() --> response.isSuccessful() = false");
                    Log.i(TAG, "error code: " +response.code() );
                    Log.i(TAG, "error message: " +response.message() );
                }

            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mPresenter.setLoginResponseCode(ApiResponseConstants.SERVER_FAILURE_CODE);
                mPresenter.setLoginResponseMessage(ApiResponseConstants.SERVER_FAILURE_MSG);
                mPresenter.onLoginRequestFinished();
                Log.i(TAG, "requestUserLogin() --> onFailure executed, error: ", t);
                t.printStackTrace();
            }

        });

    }


    private void updateUserToken(String userID, String token) {

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userID);
        params.put("fcm_token", token);

        MessagingAPI messagingAPI = ApiProvider.getMessageApiService();
        messagingAPI.updateUserFCMToken(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {
                    Log.i(TAG, "updateUserToken --> response.isSuccessful() = true");
                    JsonObject jsonObject = response.body();
//                    GradsHubApplication.showToast("successfully updated your token for messaging.");

                }
                else {
                    GradsHubApplication.showToast( ApiResponseConstants.SERVER_FAILURE_MSG );
                    Log.i(TAG, "updateUserToken --> response.isSuccessful() = false");
                    Log.i(TAG, "error code: " +response.code() );
                    Log.i(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                GradsHubApplication.showToast( ApiResponseConstants.SERVER_FAILURE_MSG );
                Log.i(TAG, "updateUserToken() --> onFailure executed, error: ", t);
                t.printStackTrace();
            }

        });

    }


}
