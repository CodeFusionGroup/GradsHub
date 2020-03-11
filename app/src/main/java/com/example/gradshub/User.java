package com.example.gradshub;

import android.util.Patterns;
import android.widget.EditText;

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


    // this method is called whenever a user logs in or registers so that we can validate the data they entered.
    // its necessary we pass editTexts and not the values in editTexts so that we can get reference to
    // EditTexts for setting the error message according to which field is empty.
    public static boolean validateInputData(EditText[] editTexts) {

        // using a for loop to loop through editTexts as previously did not evaluate Edit texts fields in order, so changed to this style of evaluation

        //==========================================================================================
        // check that the email field is not empty
        String email = editTexts[0].getText().toString().trim();
        if (email.isEmpty()) {
            editTexts[0].setError("Field can't be empty!");
            editTexts[0].requestFocus();
            return false;
        }

        // if an email is entered, check that the entered email is formatted correctly (is it a valid email address).
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

        // if the registration activity is running then we have more fields to check
        if(editTexts.length > 2) {

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
            //TODO: must validate the spinner for selected value
            /*String academicStatus = editTexts[6].getText().toString().trim();
            if (academicStatus.equals("Choose Category")) {
                editTexts[6].setError("Please select a valid academic status");
                editTexts[6].requestFocus();
                return false;
            }*/

            //======================================================================================
            // compare to check if the passwords match for the fields: (password & confirm password).
            String confirmPassword = editTexts[2].getText().toString().trim();
            if (!confirmPassword.equals(password)) {
                editTexts[2].setError("password doesn't match the above entered password!");
                editTexts[2].requestFocus();
                return false;
            }
            //======================================================================================

        }

        return true;
    }



}
