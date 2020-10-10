package com.codefusiongroup.gradshub.utils.forms;

import com.codefusiongroup.gradshub.databinding.FragmentRegisterBinding;

public class RegisterForm {

    private static RegisterForm instance;
    private FragmentRegisterBinding registerBinding;

    // for singleton pattern, we make the constructor private so that only one instance of RegisterForm object
    // is created and the same instance is reused and no other class can instantiate a RegisterForm object
    // by directly by calling its constructor (i.e must use getInstance())
    private RegisterForm() { }

    public static RegisterForm getInstance() {
        if (instance == null) {
            instance = new RegisterForm();
        }
        return instance;
    }

    public void setRegisterBinding(FragmentRegisterBinding binding) { this.registerBinding = binding; }

    public FragmentRegisterBinding getRegisterBinding() {
        return registerBinding;
    }

    // RegisterForm fields
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
    private String academicStatus;
    private String password;
    private String confirmedPassword;

//    public RegisterForm(String firstName, String lastName, String email, String phoneNo, String academicStatus, String password) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.email = email;
//        this.phoneNo = phoneNo;
//        this.academicStatus = academicStatus;
//        this.password = password;
//    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    //    public void setEmptyRegistrationFieldsError() {
//
//        registerBinding.firstNameET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
//        registerBinding.lastNameET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
//        registerBinding.emailET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
//        registerBinding.phoneNumberET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
//        registerBinding.passwordET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
//        registerBinding.confirmNewPasswordET.setError( errors.getError(InputValidationErrors.EMPTY_FIELD) );
//
//        // if all form fields are empty then the current academic status selection is also invalid
//        TextView tv = (TextView) registerBinding.spinner.getSelectedView();
//        tv.setError("");
//        tv.setTextColor(Color.RED); // just to highlight that this is an error message, we display it in red.
//        tv.setText(R.string.spinnerErrorMsg); // changes the selected item text to this text.
//        registerBinding.spinner.requestFocus();
//
//    }

//    public boolean isFormValid() {
//
//        // must still check if valid academic status is selected
//        final String statusErrorPlaceHolder = "please select valid academic status";
//        final String statusHintPlaceHolder = "Select your academic status here";
//        TextView tv = (TextView) registerBinding.spinner.getSelectedView();
//        String currentSelection = tv.getText().toString();
//
//        if ( !currentSelection.equals(statusErrorPlaceHolder) && !currentSelection.equals(statusHintPlaceHolder) ) {
//            isValidAcademicStatus = true;
//        }
//        return isValidFirstName && isValidLastName && isValidEmail && isValidPhoneNo && isValidAcademicStatus && isValidPassword && isPasswordConfirmed;
//    }

    public static class RegisterFormBuilder {

        private String firstName;
        private String lastName;
        private String email;
        private String phoneNo;
        private String academicStatus;
        private String password;
        private String confirmPassword;

        public RegisterFormBuilder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public RegisterFormBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public RegisterFormBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public RegisterFormBuilder withPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
            return this;
        }

        public RegisterFormBuilder withAcademicStatus(String academicStatus) {
            this.academicStatus = academicStatus;
            return this;
        }

        public RegisterFormBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public RegisterFormBuilder withConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
            return this;
        }

        public RegisterForm build() {

            RegisterForm registerForm = new RegisterForm();
            registerForm.firstName = this.firstName;
            registerForm.lastName = this.lastName;
            registerForm.email = this.email;
            registerForm.phoneNo = this.phoneNo;
            registerForm.academicStatus = this.academicStatus;
            registerForm.password = this.password;
            registerForm.confirmedPassword = this.confirmPassword;
            return registerForm;

        }

    }

}
