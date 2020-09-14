package com.codefusiongroup.gradshub.authentication.login;

import com.codefusiongroup.gradshub.common.models.User;


// This interface specifies the contract between the View (LoginFragment), the Presenter (LoginPresenter)
// and the Model (LoginModel).
public interface LoginContract {


    // The interface the LoginPresenter uses to communicate with the View (LoginFragment).
    interface ILoginView {

        void showProgressBar();

        void hideProgressBar();

        // invoked by presenter on view when the email field is empty
        void showEmailInputError(String message);

        // invoked by presenter on view when the password field is empty
        void showPasswordInputError(String message);

        // invoked by presenter on view to display the login status as received from the server
        void showLoginResponseMsg(String message);

        // invoked by presenter on view to initialise the user when login is successful
        void initialiseUser(User user);

        // invoked by presenter on view to start MainActivity when login is successful and user has
        // been initialised
        void startMainActivity();

    }


    // The interface the View (LoginFragment) and LoginModel use to communicate with the LoginPresenter.
    interface ILoginPresenter {

        // invoked by the view on presenter when user clicks the login button in order to validate the
        // user's input
        boolean validateLoginInput(String email, String password);

        // invoked by view on presenter when input validation is successful to request for loggin in
        // the user
        void loginUser(String email, String password);

        // invoked by model on the presenter when the request for login has finished
        void onLoginRequestFinished();

        // invoked by model on the presenter when the request for login has and sets the response parameter
        // as received from the server (similarly for the method below setLoginResponseMessage())
        void setLoginResponseCode(String responseCode);


        void setLoginResponseMessage(String responseMessage);

        // invoked by the model on the presenter to set the current user when login is successful
        void setCurrentUser(User user);

    }


    // The interface LoginPresenter uses to communicate with LoginModel.
    interface ILoginModel {

        // invoked by the presenter on the model to request for login for the given login credentials
        void requestUserLogin(String email, String password);

    }

}
