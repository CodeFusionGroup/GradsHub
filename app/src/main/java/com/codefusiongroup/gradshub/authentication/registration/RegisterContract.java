package com.codefusiongroup.gradshub.authentication.registration;


import com.codefusiongroup.gradshub.common.models.User;


// This interface specifies the contract between the View (RegisterFragment), the Presenter (RegisterPresenter)
// and the Model (RegisterModel).
public interface RegisterContract {


    // The interface the RegisterPresenter uses to communicate with the View (RegisterFragment).
    interface IRegisterView {

        // invoked by the presenter on the view to display a progress bar when the user clicks submit
        // for sending the registration details to the server
        void showProgressBar();

        // invoked by the presenter on the view to hide the progress bar when the request to register the
        // user has completed
        void hideProgressBar();

        // invoked by the presenter on the view when the first name field on the registration form
        // is empty
        void showFirstNameInputError(String message);

        // invoked by the presenter on the view when the last name field on the registration form
        // is empty
        void showLastNameInputError(String message);

        // invoked by the presenter on the view when the email field on the registration form
        // is empty
        void showEmailInputError(String message);

        // invoked by the presenter on the view when the phone number field on the registration form
        // is empty
        void showPhoneNoInputError(String message);

        // invoked by the presenter on the view when the user did not select a valid academic status
        // from the list of options
        void showAcademicStatusInputError(String message);

        // invoked by the presenter on the view when the password field on the registration form
        // is empty
        void showPasswordInputError(String message);

        // invoked by the presenter on the view when the password confirmation field on the registration form
        // is empty
        void showPasswordConfirmationError(String message);

        // invoked by the presenter on the view to display the status of the registration of the user
        // as received from the server
        void showRegistrationResponseMsg(String message);

        // invoked by the presenter on the view when the registration of the user is successful
        void navigateToLogin();

    }


    // The interface the View (RegistrationFragment) and RegisterModel use to communicate with
    // the RegisterPresenter.
    interface IRegisterPresenter {

        // invoked by the view on the presenter when user has filled their registration details to
        // validate the input data
        boolean validateRegistrationInput(String firstName, String lastName,
                                          String email, String phoneNo,
                                          String acadStatus, String password, String confirmPassword);


        // invoked by the view on the presenter when the validation of the user's registrations details
        // is successful
        void registerUser(User user);

        // invoked by the model on the presenter to alert the presenter that the request to register the
        // user has completed
        void onRegistrationRequestFinished();

        // invoked by the model on the presenter to set the parameters of the registrations response as
        // received from the server (similarly for the method below)
        void setRegistrationResponseCode(String responseMessage);


        void setRegistrationResponseMsg(String responseMessage);

    }


    // The interface RegisterPresenter uses to communicate with RegisterModel.
    interface IRegisterModel {

        // invoked by the presenter on the model to register the user
        void requestUserRegistration(User user);
    }

}
