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

    @SerializedName("USER_PROFILE_PICTURE")
    private String profilePicture;

    @SerializedName("USER_NAME")
    private String username;

    private String password;
    private String fcmToken;
    private boolean isBlocked = false;
    private boolean isAFriend = false;
    private boolean isLoggedIn = false;


    public User(String firstName, String lastName, String email, String phoneNumber, String academicStatus, String password, String fcmToken) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.academicStatus = academicStatus;
        this.password = password;
        this.fcmToken = fcmToken;
    }


    public User() {
        // default constructor needed
    }


    public void setUsername(String username) { this.username = username; }

    public String getUsername() { return username; }

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

    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getProfilePicture() { return profilePicture; }

    public String getFullName() {return firstName + " " + lastName;}

    public String getFCMToken(){ return fcmToken;}

    public void setBlockedStatus(boolean value) { isBlocked = value; }

    public boolean isBlocked() { return isBlocked; }

    public void setFriendStatus(boolean value) { isAFriend = value; }

    public boolean isAFriend() { return isAFriend; }

    public void setLoginState(boolean value) { this.isLoggedIn = value; }

    public boolean isLoggedIn() { return isLoggedIn; }


    protected User(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        academicStatus = in.readString();
        userID = in.readString();
        username = in.readString();
        profilePicture = in.readString();
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
        dest.writeString(username);
        dest.writeString(profilePicture);
    }

}
