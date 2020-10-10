package com.codefusiongroup.gradshub.utils.validations;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.databinding.FragmentLoginBinding;
import com.codefusiongroup.gradshub.databinding.FragmentRegisterBinding;
import com.codefusiongroup.gradshub.databinding.FragmentResetPasswordBinding;
import com.codefusiongroup.gradshub.utils.forms.LoginForm;
import com.codefusiongroup.gradshub.utils.forms.RegisterForm;
import com.codefusiongroup.gradshub.utils.forms.ResetPasswordForm;

public class FormValidator {

    private static boolean isValidFirstName = false;
    private static boolean isValidLastName = false;
    private static boolean isValidEmail = false;
    private static boolean isValidPhoneNo = false;
    private static boolean isValidAcademicStatus = false;
    private static boolean isValidPassword = false;
    private static boolean isPasswordConfirmed = false;

    private static FormValidator instance;
    private static InputValidationErrors errors = InputValidationErrors.getInstance();

    // constructor private so that only one instance of FormValidator object is created and no other
    // class can instantiate it directly
    private FormValidator() { }

    // singleton pattern
    public static FormValidator getInstance() {
        if (instance == null) {
            instance = new FormValidator();
        }
        return instance;
    }


    @BindingAdapter("firstNameValidator")
    public static void firstNameValidator(EditText firstNameET, String placeHolder) {

        firstNameET.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable firstNameEditable) {
                // NOTE: must use the Editable variable instead of passing the view model property which
                // corresponds to this field to get reliable error validation except where certain cases
                // require model property like in confirm password validator below. (similar explanations
                // for subsequent methods that follow)
                if ( TextUtils.isEmpty(firstNameEditable) ) {
                    firstNameET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
                    isValidFirstName = false;
                }
                else {
                    firstNameET.setError(null);
                    isValidFirstName = true;
                }
            }
        });

    }

    @BindingAdapter("lastNameValidator")
    public static void lastNameValidator(EditText lastNameET, String placeHolder) {

        lastNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable lastNameEditable) {
                if ( TextUtils.isEmpty(lastNameEditable) ) {
                    lastNameET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
                    isValidLastName = false;
                }
                else {
                    lastNameET.setError(null);
                    isValidLastName = true;
                }
            }
        });

    }


    @BindingAdapter("emailValidator")
    public static void emailValidator(EditText emailET, String placeHolder) {

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable emailEditable) {
                if ( TextUtils.isEmpty(emailEditable) ) {
                    emailET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
                    isValidEmail = false;
                }
                else if ( !Patterns.EMAIL_ADDRESS.matcher(emailEditable.toString().trim()).matches() ) {
                    emailET.setError( errors.getError(InputValidationErrors.INVALID_EMAIL_FORMAT) );
                    isValidEmail = false;
                }
                else {
                    emailET.setError(null);
                    isValidEmail = true;
                }

            }
        });

    }


    @BindingAdapter("phoneNoValidator")
    public static void phoneNoValidator(EditText phoneNoET, String placeHolder) {

        phoneNoET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable phoneNoEditable) {
                if ( TextUtils.isEmpty(phoneNoEditable) ) {
                    phoneNoET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
                    isValidPhoneNo = false;
                }
                else if ( phoneNoEditable.toString().trim().length() < 10) {
                    phoneNoET.setError( errors.getError(InputValidationErrors.INVALID_CONTACT_NO) );
                    isValidPhoneNo = false;
                }
                else {
                    phoneNoET.setError(null);
                    isValidPhoneNo = true;
                }

            }
        });

    }


    @BindingAdapter("passwordValidator")
    public static void passwordValidator(EditText passwordET, String placeHolder) {

        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable passwordEditable) {
                if ( TextUtils.isEmpty(passwordEditable) ) {
                    passwordET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
                    isValidPassword = false;
                }
                else {
                    passwordET.setError(null);
                    isValidPassword = true;
                }
            }
        });

    }


    @BindingAdapter("originalPassword")
    public static void passwordConfirmValidator(EditText passwordET, String originalPassword) {

        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable confirmPasswordEditable) {
                if ( TextUtils.isEmpty(confirmPasswordEditable) ) {
                    passwordET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
                    isPasswordConfirmed = false;
                }
                else if ( !confirmPasswordEditable.toString().trim().equals(originalPassword) ) {
                    passwordET.setError( errors.getError(InputValidationErrors.PASSWORD_MISMATCH) );
                    isPasswordConfirmed = false;
                }
                else {
                    passwordET.setError(null);
                    isPasswordConfirmed = true;
                }
            }
        });

    }


    public boolean isValidEmail(String email) {
        FragmentResetPasswordBinding resetPasswordBinding = ResetPasswordForm.getInstance().getResetPasswordBinding();
        if (email.length() == 0) {
            resetPasswordBinding.emailET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
            return false;
        }
        return isValidEmail;
    }


    public boolean isFormStateValid(RegisterForm registerForm) {

        final FragmentRegisterBinding registerBinding = RegisterForm.getInstance().getRegisterBinding();
        final String statusHintPlaceHolder = "Select your academic status here";
        final String statusErrorPlaceHolder = "please select valid academic status";
        TextView tv = (TextView) registerBinding.spinner.getSelectedView();
        String currentStatusSelection = tv.getText().toString();

        // if no fields received any input and login was clicked, for the first time
        if ( registerForm.getFirstName().length() == 0 && registerForm.getLastName().length() == 0 &&
                registerForm.getEmail().length() == 0 && registerForm.getPhoneNo().length() == 0 &&
                registerForm.getAcademicStatus().equals(statusHintPlaceHolder) &&
                registerForm.getPassword().length() == 0 && registerForm.getConfirmedPassword().length() == 0) {

            registerBinding.firstNameET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
            registerBinding.lastNameET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
            registerBinding.emailET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
            registerBinding.phoneNumberET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
            registerBinding.passwordET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
            registerBinding.confirmNewPasswordET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );

            tv.setError("");
            tv.setTextColor(Color.RED); // just to highlight that this is an error message, we display it in red.
            tv.setText(R.string.status_error); // changes the selected item text to this text.
            registerBinding.spinner.requestFocus();

            return false;
        }

        // still check if current academic status selection is valid
        if (!currentStatusSelection.equals(statusHintPlaceHolder) && !currentStatusSelection.equals(statusErrorPlaceHolder)) {
            isValidAcademicStatus = true;
        }

        return isValidFirstName && isValidLastName && isValidEmail && isValidPhoneNo && isValidAcademicStatus && isValidPassword && isPasswordConfirmed;

    }


    public boolean isFormStateValid(LoginForm loginForm) {

        final FragmentLoginBinding loginBinding = LoginForm.getInstance().getLoginBinding();
        if (loginForm.getEmail().length() == 0 && loginForm.getPassword().length() == 0) {
            loginBinding.emailET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
            loginBinding.passwordET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
            return false;
        }
        return isValidEmail && isValidPassword;
    }

}
