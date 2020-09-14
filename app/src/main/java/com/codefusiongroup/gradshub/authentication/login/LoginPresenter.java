package com.codefusiongroup.gradshub.authentication.login;

import android.util.Log;
import android.util.Patterns;

import com.codefusiongroup.gradshub.common.BasePresenter;
import com.codefusiongroup.gradshub.common.BasePresenter2;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.messaging.chatMessages.ChatMessagesContract;


public class LoginPresenter implements BasePresenter<LoginContract.ILoginView>, LoginContract.ILoginPresenter {

    private static String TAG = "LoginPresenter"; // for debugging

    private String mResponseCode = null;
    private String mResponseMessage = null;
    private User mUser = null;

    private final LoginContract.ILoginModel mLoginModel = LoginModel.newInstance(this);
    private LoginContract.ILoginView mView;


    @Override
    public void subscribe(LoginContract.ILoginView view) {
        mView = view;
    }


    @Override
    public void unsubscribe() {
        mView = null;
    }


    @Override
    public boolean validateLoginInput(String email, String password) {

            String emptyFieldErrorMsg = "Field can't be empty.";
            String emailPatternErrorMsg = "Check that your email address is entered correctly!";

            if( email.isEmpty() ) {
                if(mView != null) {
                    mView.showEmailInputError(emptyFieldErrorMsg);
                    return false;
                }
            }

            if( !Patterns.EMAIL_ADDRESS.matcher(email).matches() ) {
                if(mView != null) {
                    mView.showEmailInputError(emailPatternErrorMsg);
                    return false;
                }
            }

            if ( password.isEmpty() ) {
                if(mView != null) {
                    mView.showPasswordInputError(emptyFieldErrorMsg);
                    return false;
                }
            }
        return true;
    }


    @Override
    public void loginUser(String email, String password) {
        Log.i(TAG, "loginUser() executed");
        mLoginModel.requestUserLogin(email, password);
        if (mView != null) {
            Log.i(TAG, "mView not null, showing progress bar...");
            mView.showProgressBar();
        }
    }


    @Override
    public void setLoginResponseCode(String responseCode) {
        mResponseCode = responseCode;
    }


    @Override
    public void setLoginResponseMessage(String responseMessage) {
        mResponseMessage = responseMessage;
    }

    @Override
    public void setCurrentUser(User user) {
        mUser = user;
    }


    @Override
    public void onLoginRequestFinished() {
        Log.i(TAG, "onLoginRequestFinished() executed");

        if (mView != null) {
            Log.d(TAG, "mView not null, hide progress bar");
            mView.hideProgressBar();

            if ( mResponseCode.equals(ApiResponseConstants.API_SUCCESS_CODE) ) {
                mView.initialiseUser(mUser);
                mView.startMainActivity();
            }
            mView.showLoginResponseMsg(mResponseMessage);
        }

    }

}
