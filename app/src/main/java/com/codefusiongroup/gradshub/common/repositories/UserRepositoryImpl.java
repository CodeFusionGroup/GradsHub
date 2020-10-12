package com.codefusiongroup.gradshub.common.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import com.codefusiongroup.gradshub.authentication.AuthenticationAPI;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.UserPreferences;
import com.codefusiongroup.gradshub.common.network.ApiBaseResponse;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.messaging.MessagingAPI;
import com.codefusiongroup.gradshub.utils.api.Resource;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


public class UserRepositoryImpl implements IUserRepository {

    private static String TAG = "UserRepositoryImpl";

    private MutableLiveData<Resource<User>> loginResponse;
    private MutableLiveData<Resource<String>> registerResponse;
    private MutableLiveData<Resource<String>> passwordResetResponse;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final  AuthenticationAPI authenticationAPI = ApiProvider.getAuthApiService();


    private static UserRepositoryImpl repository = null;

    // constructor private so that only one instance of UserRepositoryImpl object is created and no other
    // class can instantiate it directly
    private UserRepositoryImpl() { }

    // singleton pattern
    public static UserRepositoryImpl getInstance() {
        if (repository == null) {
            repository = new UserRepositoryImpl();
        }
        return repository;
    }


    @Override
    public MutableLiveData<Boolean> getIsLoading() { return isLoading; }

    @Override
    public MutableLiveData<Resource<User>> getLoginResponse () {
        if (loginResponse == null) {
            loginResponse = new MutableLiveData<>();
        }
        return loginResponse;
    }

    @Override
    public MutableLiveData<Resource<String>> getRegisterResponse() {
        if (registerResponse == null) {
            registerResponse = new MutableLiveData<>();
        }
        return registerResponse;
    }

    @Override
    public MutableLiveData<Resource<String>> getPasswordResetResponse() {
        if (passwordResetResponse == null) {
            passwordResetResponse = new MutableLiveData<>();
        }
        return passwordResetResponse;
    }


    @Override
    public void loginUser(String email, String password) {

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        isLoading.setValue(true);
        authenticationAPI.loginUser(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                isLoading.setValue(false);
                if ( response.isSuccessful() ) {
                    Log.i(TAG, "requestUserLogin() --> response.isSuccessful() = true");

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        JsonObject userJO = jsonObject.getAsJsonObject("user");
                        User user = new Gson().fromJson(userJO, User.class);
                        String message = jsonObject.get("message").getAsString();
                        loginResponse.setValue( Resource.apiDataRequestSuccess(user, message) );

                        // if token update was unsuccessful previously then update token with most
                        // recent value
                        UserPreferences userPreferences = UserPreferences.getInstance();
                        Context ctx = GradsHubApplication.getContext();
                        if( userPreferences.isTokenChanged(ctx) ) {
                            updateUserToken(user.getUserID(), userPreferences.getToken(ctx));
                        }

                    }
                    else {
                        ApiBaseResponse apiDefault = new Gson().fromJson(jsonObject, ApiBaseResponse.class);
                        loginResponse.setValue( Resource.apiNonDataRequestSuccess( apiDefault.getMessage() ) );
                    }

                }
                else {
                    loginResponse.setValue( Resource.error(ApiResponseConstants.SERVER_FAILURE_MSG) );
                    Log.i(TAG, "requestUserLogin() --> response.isSuccessful() = false");
                    Log.i(TAG, "error code: " +response.code() );
                    Log.i(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                isLoading.setValue(false);
                loginResponse.setValue( Resource.error(ApiResponseConstants.SERVER_FAILURE_MSG) );
                Log.i(TAG, "requestUserLogin() --> onFailure executed, error: ", t);
            }

        });

    }


    @Override
    public void registerUser(User user) {

        HashMap<String, String> params = new HashMap<>();
        params.put("f_name", user.getFirstName());
        params.put("l_name", user.getLastName());
        params.put("email", user.getEmail());
        params.put("phone_no", user.getPhoneNumber());
        params.put("acad_status", user.getAcademicStatus());
        params.put("password", user.getPassword());
        params.put("fcm_token",user.getFCMToken());

        isLoading.setValue(true);
        authenticationAPI.registerUser(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                isLoading.setValue(false);

                if ( response.isSuccessful() ) {
                    Log.i(TAG, "registerUser() --> response.isSuccessful() = true");
                    JsonObject jsonObject = response.body();
                    registerResponse.setValue( Resource.apiNonDataRequestSuccess( jsonObject.get("message").getAsString() ) );
                }
                else {
                    registerResponse.setValue( Resource.error(ApiResponseConstants.SERVER_FAILURE_MSG) );
                    Log.i(TAG, "registerUser() --> response.isSuccessful() = false");
                    Log.i(TAG, "registerUser() --> response.errorBody() --> "+response.errorBody());
                    Log.i(TAG, "registerUser() --> response.message() = false, "+response.message());
                    Log.i(TAG, "registerUser() --> response.code() = false, "+response.code());
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                isLoading.setValue(false);
                registerResponse.setValue( Resource.error(ApiResponseConstants.SERVER_FAILURE_MSG) );
                Log.i(TAG, "registerUser() --> onFailure executed, error: ", t);
            }

        });


    }


    @Override
    public void requestPasswordReset(String email) {

        Map<String, String> params = new HashMap<>();
        params.put("user_email", email);

        isLoading.setValue(true);
        authenticationAPI.requestPasswordReset(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                isLoading.setValue(false);

                if ( response.isSuccessful() ) {
                    Log.i(TAG, " requestPasswordReset() --> response.isSuccessful = true");
                    JsonObject jsonObject = response.body();
                    passwordResetResponse.setValue( Resource.apiNonDataRequestSuccess( jsonObject.get("message").getAsString() ) );
                }
                else {
                    registerResponse.setValue( Resource.error(ApiResponseConstants.SERVER_FAILURE_MSG) );
                    Log.i(TAG, " requestPasswordReset() --> response.isSuccessful = false");
                    Log.i(TAG, "error code: " +response.code() );
                    Log.i(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                isLoading.setValue(false);
                registerResponse.setValue( Resource.error(ApiResponseConstants.SERVER_FAILURE_MSG) );
                Log.i(TAG, "requestPasswordReset() --> onFailure executed, error: ", t);
            }

        });

    }


    @Override
    public void updateUserToken(String userID, String token) {

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userID);
        params.put("fcm_token", token);

        MessagingAPI messagingAPI = ApiProvider.getMessageApiService();
        messagingAPI.updateUserFCMToken(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {

                    JsonObject jsonObject = response.body();
                    // we don't show a toast for this
                    Log.i(TAG, "updateUserToken() --> response.isSuccessful() = true");
                    Log.i(TAG, "api response: "+ jsonObject.get("message").getAsString());
                }
                else {
                    // if token update on DB was not successful then store it on preferences
                    UserPreferences.getInstance().saveTokenState(token, GradsHubApplication.getContext());
                    Log.i(TAG, "updateUserToken() --> response.isSuccessful() = false");
                    Log.i(TAG, "error code: " +response.code() );
                    Log.i(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // if token update on DB was not successful then store it on preferences
                UserPreferences.getInstance().saveTokenState(token, GradsHubApplication.getContext());
                Log.i(TAG, "updateUserToken() --> onFailure executed, error: ", t);
            }

        });

    }

}
