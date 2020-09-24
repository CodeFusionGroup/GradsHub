package com.codefusiongroup.gradshub.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.codefusiongroup.gradshub.common.models.User;


public class UserPreferences {

    private final String USER_ID = "user_id";
    private final String FIRST_NAME = "first_name";
    private final String LAST_NAME = "last_name";
    private final String EMAIL = "email";
    private final String PHONE_NO = "phone_no";
    private final String ACADEMIC_STATUS = "academic_status";
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
        editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
                .putString( USER_ID, user.getUserID() )
                .putString( FIRST_NAME, user.getFirstName() )
                .putString( LAST_NAME, user.getLastName() )
                .putString( EMAIL, user.getEmail() )
                .putString( PHONE_NO, user.getPhoneNumber() )
                .putString( ACADEMIC_STATUS, user.getAcademicStatus() );
        editor.apply();

    }


    public User getUserDetails(Context context) {

        SharedPreferences mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String userID = mPreferences.getString(USER_ID, "no ID defined");
        String firstName = mPreferences.getString(FIRST_NAME, "no firstName defined");
        String lastName = mPreferences.getString(LAST_NAME, "no lastName defined");
        String email = mPreferences.getString(EMAIL, "no email defined");
        String phoneNumber = mPreferences.getString(PHONE_NO, "no phoneNo defined");
        String academicStatus = mPreferences.getString(ACADEMIC_STATUS, "no academicStatus defined");

        return new User(userID, firstName, lastName, email, phoneNumber, academicStatus);

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
    public void saveFCMToken(String fcmToken, Context context){
        editor = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE).edit();
        editor.putString(FCM_TOKEN,fcmToken);
        editor.apply();
    }
    public String getFCMToken(Context context){
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
