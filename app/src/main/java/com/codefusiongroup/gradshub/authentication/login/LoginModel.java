package com.codefusiongroup.gradshub.authentication.login;

import android.content.Context;
import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

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

    private static String TAG = "LoginModel"; // for debugging

    private final String  SUCCESS_CODE = "1";
    private static LoginModel loginModel = null;
    private final LoginContract.ILoginPresenter mPresenter;

    private AppCompatActivity mActivity;
    private UserPreferences mUserPreferences;

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

                        mUserPreferences = UserPreferences.getInstance();
                        Context ctx = GradsHubApplication.getContext();
                        if(mUserPreferences.isTokenChanged(ctx)){
                            // Update the FCM token
                            String token = mUserPreferences.getFCMToken(ctx);
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


    private void updateUserToken(String userID, String token) {

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userID);
        params.put("fcm_token", token);

        MessagingAPI messagingAPI = ApiProvider.getMessageApiService();

        messagingAPI.updateUserFCMToken(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {
                    JsonObject jsonObject = response.body();
//                    GradsHubApplication.showToast("successfully updated your token for messaging.");
                    Log.i(TAG, " update user fcm token response.isSuccessful() = true");
                }
                else {
                    Log.i(TAG, "response.isSuccessful() = false");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                GradsHubApplication.showToast("your token has been refreshed, please refresh page to continue receiving messages.");
                t.printStackTrace();
            }

        });

    }


}
