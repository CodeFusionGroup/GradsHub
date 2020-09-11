package com.codefusiongroup.gradshub.authentication.login;

import android.util.Patterns;

import com.codefusiongroup.gradshub.common.BasePresenter;
import com.codefusiongroup.gradshub.common.models.User;


public class LoginPresenter implements BasePresenter<LoginContract.ILoginView>, LoginContract.ILoginPresenter {

    private static String TAG = "LoginPresenter"; // for debugging

    private String mResponseCode = null;
    private String mResponseMessage = null;
    private User mUser = null;

    private final LoginContract.ILoginModel mLoginModel = LoginModel.newInstance(this);
    private LoginContract.ILoginView mLoginView;


    @Override
    public boolean validateLoginInput(String email, String password) {

            String emptyFieldErrorMsg = "Field can't be empty.";
            String emailPatternErrorMsg = "Check that your email address is entered correctly!";

            if( email.isEmpty() ) {
                if(mLoginView != null) {
                    mLoginView.showEmailInputError(emptyFieldErrorMsg);
                    return false;
                }
            }

            if( !Patterns.EMAIL_ADDRESS.matcher(email).matches() ) {
                if(mLoginView != null) {
                    mLoginView.showEmailInputError(emailPatternErrorMsg);
                    return false;
                }
            }

            if ( password.isEmpty() ) {
                if(mLoginView != null) {
                    mLoginView.showPasswordInputError(emptyFieldErrorMsg);
                    return false;
                }
            }
        return true;
    }


    @Override
    public void loginUser(String email, String password) {
        mLoginModel.requestUserLogin(email, password);
        if (mLoginView != null) mLoginView.showProgressBar();
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

        if (mLoginView != null) {
            mLoginView.hideProgressBar();
            String SUCCESS_CODE = "1";
            if ( mResponseCode.equals(SUCCESS_CODE) ) {
                mLoginView.initialiseUser(mUser);
                mLoginView.startMainActivity();
            }
            mLoginView.showLoginResponseMsg(mResponseMessage);
        }

    }


    @Override
    public void subscribe(LoginContract.ILoginView loginView) { mLoginView = loginView; }


    @Override
    public void unsubscribe() {
        mLoginView = null;
    }


}
