package com.codefusiongroup.gradshub.authentication.registration;

import android.util.Patterns;

import com.codefusiongroup.gradshub.common.BasePresenter;
import com.codefusiongroup.gradshub.common.models.User;


public class RegisterPresenter implements BasePresenter<RegisterContract.IRegisterView>, RegisterContract.IRegisterPresenter {


    private String mResponseCode = null;
    private String mResponseMessage = null;

    private final RegisterContract.IRegisterModel mRegisterModel = RegisterModel.newInstance(this);
    private RegisterContract.IRegisterView mRegisterView;


    @Override
    public boolean validateRegistrationInput(String firstName, String lastName, String email, String phoneNo, String academicStatus, String password, String confirmPassword) {

        String emptyFieldErrorMsg = "Field can't be empty!";
        String passwordMismatchErrorMsg = "password doesn't match the above entered password!";
        String emailPatternErrorMsg = "check that your email address is entered correctly!";
        String academicStatusErrorMsg = "Please select valid academic status!";
        String academicStatusPlaceHolder = "Select your academic status here";//used as hint

        if ( firstName.isEmpty() ) {
            if(mRegisterView != null) {
                mRegisterView.showFirstNameInputError(emptyFieldErrorMsg);
                return false;
            }
        }

        if ( lastName.isEmpty() ) {
            if(mRegisterView != null) {
                mRegisterView.showLastNameInputError(emptyFieldErrorMsg);
                return false;
            }
        }

        if ( email.isEmpty() ) {
            if(mRegisterView != null) {
                mRegisterView.showEmailInputError(emptyFieldErrorMsg);
                return false;
            }
        }

        if( !Patterns.EMAIL_ADDRESS.matcher(email).matches() ) {
            if(mRegisterView != null) {
                mRegisterView.showEmailInputError(emailPatternErrorMsg);
                return false;
            }
        }

        if ( phoneNo.isEmpty() ) {
            if(mRegisterView != null) {
                mRegisterView.showPhoneNoInputError(emptyFieldErrorMsg);
                return false;
            }
        }

        if (academicStatus.equals(academicStatusPlaceHolder)) {
            if(mRegisterView != null) {
                mRegisterView.showAcademicStatusInputError(academicStatusErrorMsg);
                return false;
            }
        }

        if (password.isEmpty()) {
            if(mRegisterView != null) {
                mRegisterView.showPasswordInputError(emptyFieldErrorMsg);
                return false;
            }
        }

        if (confirmPassword.isEmpty()) {
            if(mRegisterView != null) {
                mRegisterView.showPasswordConfirmationError(emptyFieldErrorMsg);
                return false;
            }
        }

        if (!confirmPassword.equals(password)) {
            if(mRegisterView != null) {
                mRegisterView.showPasswordConfirmationError(passwordMismatchErrorMsg);
                return false;
            }
        }

        return true;

    }


    @Override
    public void registerUser(User user) {
        mRegisterModel.requestUserRegistration(user);
        if (mRegisterView != null) mRegisterView.showProgressBar();
    }


    @Override
    public void onRegistrationRequestFinished() {

        if (mRegisterView != null) {
            mRegisterView.hideProgressBar();
            String SUCCESS_CODE = "1";
            if ( mResponseCode.equals(SUCCESS_CODE) ) {
                mRegisterView.navigateToLogin();
            }
            mRegisterView.showRegistrationResponseMsg(mResponseMessage);
        }

    }


    @Override
    public void setRegistrationResponseCode(String responseMessage) {
        mResponseCode = responseMessage;
    }


    @Override
    public void setRegistrationResponseMsg(String responseMessage) {
        mResponseMessage = responseMessage;
    }


    @Override
    public void subscribe(RegisterContract.IRegisterView view) {
        mRegisterView = view;
    }


    @Override
    public void unsubscribe() {
        mRegisterView = null;
    }


}
