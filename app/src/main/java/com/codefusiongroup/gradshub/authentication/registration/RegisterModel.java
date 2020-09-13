package com.codefusiongroup.gradshub.authentication.registration;


import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.codefusiongroup.gradshub.authentication.AuthenticationAPI;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.ApiProvider;

import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;


public class RegisterModel implements RegisterContract.IRegisterModel{


    private static final String TAG = "RegisterModel";
    private static RegisterModel registerModel = null;
    private final RegisterContract.IRegisterPresenter mPresenter;


    private RegisterModel(RegisterContract.IRegisterPresenter presenter) {
        mPresenter = presenter;
    }


    public static RegisterModel newInstance(RegisterPresenter presenter) {
        if (registerModel == null) {
            registerModel = new RegisterModel(presenter);
        }
        return registerModel;
    }


    @VisibleForTesting
    @Override
    public void requestUserRegistration(User user) {

        HashMap<String, String> params = new HashMap<>();
        params.put("f_name", user.getFirstName());
        params.put("l_name", user.getLastName());
        params.put("email", user.getEmail());
        params.put("phone_no", user.getPhoneNumber());
        params.put("acad_status", user.getAcademicStatus());
        params.put("password", user.getPassword());
        params.put("fcm_token",user.getFCMToken());

        AuthenticationAPI authenticationAPI = ApiProvider.getAuthApiService();

        authenticationAPI.registerUser(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {
                    JsonObject jsonObject = response.body();
                    mPresenter.setRegistrationResponseCode( jsonObject.get("success").getAsString() );
                    mPresenter.setRegistrationResponseMsg( jsonObject.get("message").getAsString() );
                    mPresenter.onRegistrationRequestFinished();
                }
                else {
                    Log.i(TAG, "response.isSuccessful() = false");
                    Log.i(TAG, "response.errorBody() --> "+response.errorBody());
                    Log.i(TAG, "response.message() = false, "+response.message());
                    Log.i(TAG, "response.code() = false, "+response.code());
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                String SERVER_FAILURE_CODE = "-100";
                String  SERVER_FAILURE_MSG = "Connection failed, please try again later.";
                mPresenter.setRegistrationResponseCode(SERVER_FAILURE_CODE);
                mPresenter.setRegistrationResponseMsg(SERVER_FAILURE_MSG);
                mPresenter.onRegistrationRequestFinished();
                t.printStackTrace();
            }

        });

    }

}
