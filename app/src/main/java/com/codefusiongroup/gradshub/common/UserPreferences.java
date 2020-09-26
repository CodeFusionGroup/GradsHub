package com.codefusiongroup.gradshub.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.codefusiongroup.gradshub.common.models.User;


public class UserPreferences {

    private static final String TAG = "UserPreferences";


    private final String USER_ID = "user_id";
    private final String FIRST_NAME = "first_name";
    private final String LAST_NAME = "last_name";
    private final String EMAIL = "email";
    private final String PHONE_NO = "phone_no";
    private final String ACADEMIC_STATUS = "academic_status";
    private final String PROFILE_PICTURE = "profile_picture";
    private final String USER_NAME = "username";
    private final String LOGIN_STATE = "is_loggedIn";
    private final String PREF_NAME = "com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY";
    private final String FCM_TOKEN = "fcm_token";
    private final String FCM_TOKEN_CHANGED = "fcm_token_changed";

    private SharedPreferences.Editor editor;
    private static UserPreferences instance;

    private UserPreferences() {

    }

    public static UserPreferences getInstance() {
        if (instance == null){
            instance = new UserPreferences();
        }

        return instance;
    }


    public void saveUserDetails(User user, Context context) {

//        Log.i(TAG, "updating preferences with new profile information:");
//        Log.i(TAG, "first_names: " +user.getFirstName() );
//        Log.i(TAG, "last_names: " +user.getFirstName() );
//        Log.i(TAG, "username: " +user.getUsername() );
//        Log.i(TAG, "phone_no: " +user.getPhoneNumber()  );
//        Log.i(TAG, "email: " +user.getEmail()  );
//        Log.i(TAG, "academic status: " +user.getAcademicStatus() );
//        Log.i(TAG, "profile_picture: " +user.getProfilePicture()  );

        editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
                .putString( USER_ID, user.getUserID() )
                .putString( FIRST_NAME, user.getFirstName() )
                .putString( LAST_NAME, user.getLastName() )
                .putString( EMAIL, user.getEmail() )
                .putString( PHONE_NO, user.getPhoneNumber() )
                .putString( ACADEMIC_STATUS, user.getAcademicStatus() )
                .putString( PROFILE_PICTURE, user.getProfilePicture() )
                .putString( USER_NAME, user.getUsername() );
        editor.apply();

    }


    public User getUserDetails(Context context) {

        SharedPreferences mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String userID = mPreferences.getString(USER_ID, "no ID set");
        String firstName = mPreferences.getString(FIRST_NAME, "no firstName set");
        String lastName = mPreferences.getString(LAST_NAME, "no lastName set");
        String email = mPreferences.getString(EMAIL, "no email set");
        String phoneNumber = mPreferences.getString(PHONE_NO, "no phoneNo set");
        String academicStatus = mPreferences.getString(ACADEMIC_STATUS, "no academicStatus set");
        String profilePicture = mPreferences.getString(PROFILE_PICTURE, "no profilePicture set");
        String username = mPreferences.getString(USER_NAME, "no username set");

        return new User(userID, firstName, lastName, email, phoneNumber, academicStatus, profilePicture, username);

    }


    public boolean isLoggedIn(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(LOGIN_STATE, false);
    }


    public void setLogInState(Context context) {
        editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(LOGIN_STATE, true);
        editor.apply();
    }


    public void setLogOutState(Context context) {
        editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(LOGIN_STATE, false);
        editor.apply();
    }

    // FCM TOKEN
    public void saveFCMToken(String fcmToken, Context context) {
        editor = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE).edit();
        editor.putString(FCM_TOKEN,fcmToken);
        editor.apply();
    }


    public String getFCMToken(Context context) {
        return context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE).getString(FCM_TOKEN,"Token Not Found");
    }


    public void tokenChanged(Context context) {
        editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(FCM_TOKEN_CHANGED, true);
        editor.apply();
    }


    public boolean isTokenChanged(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(FCM_TOKEN_CHANGED, false);
    }

}
