package com.example.gradshub;

import android.graphics.Color;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String academicStatus;
    private String password;

    public User(String firstName, String lastName, String email, String phoneNumber, String academicStatus, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.academicStatus = academicStatus;
        this.password = password;
    }

    public User() {

    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }


    // this method is called whenever a login/registration activity is running in order to verify data input.
    public static boolean validateInputData(EditText[] editTexts, Spinner[] spinners) {

        // NOTE: tried to order these in such a way that the errors are displayed in the order of EditTexts as in the UI

        // if the registration activity is running then we have more fields to check
        if(editTexts.length > 2) {

            //======================================================================================
            // check that the first name field is not empty
            String firstName = editTexts[3].getText().toString().trim();
            if (firstName.isEmpty()) {
                editTexts[3].setError("Field can't be empty!");
                editTexts[3].requestFocus();
                return false;
            }

            //======================================================================================
            // check that the last name field is not empty
            String lastName = editTexts[4].getText().toString().trim();
            if (lastName.isEmpty()) {
                editTexts[4].setError("Field can't be empty!");
                editTexts[4].requestFocus();
                return false;
            }

            //======================================================================================
            // check that the phone number field is not empty
            String phoneNumber = editTexts[5].getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                editTexts[5].setError("Field can't be empty!");
                editTexts[5].requestFocus();
                return false;
            }

            //======================================================================================
            // check that the user selects a valid academic status from list (Honours, Masters, PhD)
            // first item is invalid (used as hint)
            String academicStatus = spinners[0].getSelectedItem().toString();;
            if (academicStatus.equals("Select your academic status:")) {
                TextView errorText = (TextView)spinners[0].getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.BLACK); // just to highlight that this is an error (for now display with black but need to change once UI colour is changed)
                errorText.setText(R.string.spinnerErrorMsg); // changes the selected item text to this text.
                spinners[0].requestFocus();
                return false;
            }

            //======================================================================================
            // compare to check if the passwords match for the fields: (password & confirm password).
            String password = editTexts[1].getText().toString().trim();
            String confirmPassword = editTexts[2].getText().toString().trim();
            if (!confirmPassword.equals(password)) {
                editTexts[2].setError("password doesn't match the above entered password!");
                editTexts[2].requestFocus();
                return false;
            }
            //======================================================================================

        }

        //==========================================================================================
        // accommodates login activity as well.
        // check that the email field is not empty
        String email = editTexts[0].getText().toString().trim();
        if (email.isEmpty()) {
            editTexts[0].setError("Field can't be empty!");
            editTexts[0].requestFocus();
            return false;
        }

        // if an email is entered, check that the entered email is formatted correctly (is it a valid email address).
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTexts[0].setError("check that your email address is entered correctly!");
            editTexts[0].requestFocus();
            return false;
        }

        //==========================================================================================
        // check that the password field is not empty
        String password = editTexts[1].getText().toString().trim();
        if (password.isEmpty()) {
            editTexts[1].setError("Field can't be empty!");
            editTexts[1].requestFocus();
            return false;
        }
        //==========================================================================================

        return true;
    }

}
