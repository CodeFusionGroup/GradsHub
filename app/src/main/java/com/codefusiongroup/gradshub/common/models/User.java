package com.codefusiongroup.gradshub.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class User implements Parcelable {


    @SerializedName("USER_ID")
    private String userID;

    @SerializedName("USER_FNAME")
    private String firstName;

    @SerializedName("USER_LNAME")
    private String lastName;

    @SerializedName("USER_EMAIL")
    private String email;

    @SerializedName("USER_PHONE_NO")
    private String phoneNumber;

    @SerializedName("USER_ACAD_STATUS")
    private String academicStatus;

    private String password;

    private String fcmToken;


    public User(String firstName, String lastName, String email, String phoneNumber, String academicStatus, String password, String fcmToken) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.academicStatus = academicStatus;
        this.password = password;
        this.fcmToken = fcmToken;
    }

    // we provide the default constructor so that we can also be able to set fields on a user object if needed.
    public User() {}


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

    public String getPassword() {return password;}

    public void setUserID(String userID) {this.userID = userID;}

    public String getUserID() {return userID;}

    public String getFullName() {return firstName + " " + lastName;}

    public String getFCMToken(){ return fcmToken;}


    protected User(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        academicStatus = in.readString();
        userID = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {

        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(academicStatus);
        dest.writeString(userID);
    }

}
