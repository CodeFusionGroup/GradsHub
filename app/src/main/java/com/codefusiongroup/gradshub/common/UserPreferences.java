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
    private final String PROFILE_PICTURE = "profile_picture";
    private final String USER_NAME = "username";
    private final String LOGIN_STATE = "is_loggedIn";
    private final String FCM_TOKEN = "fcm_token";
    private final String FCM_TOKEN_CHANGED = "fcm_token_changed";
    private final String PREF_NAME = "com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY";

    // update this variable with the current user id without retrieving the whole user object to access
    // the id of the user (cannot be null since every user has an id and the user state is always
    // set when the user logs in)
    public static String userID;//TODO: this is null??? fix message api user id

    private SharedPreferences.Editor editor;
    private static UserPreferences instance;

    // must explicitly define as private so cannot be instantiated directly by another class
    private UserPreferences() { }

    // singleton pattern
    public static UserPreferences getInstance() {
        if (instance == null) {
            instance = new UserPreferences();
        }
        return instance;
    }


    public void saveUserState(User user, Context context) {
        userID = user.getUserID();
        editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
                .putString( USER_ID, user.getUserID() )
                .putString( FIRST_NAME, user.getFirstName() )
                .putString( LAST_NAME, user.getLastName() )
                .putString( EMAIL, user.getEmail() )
                .putString( PHONE_NO, user.getPhoneNumber() )
                .putString( ACADEMIC_STATUS, user.getAcademicStatus() )
                .putString( PROFILE_PICTURE, user.getProfilePicture() )
                .putString( USER_NAME, user.getUsername() )
                .putBoolean( LOGIN_STATE, true );
        editor.apply();

    }


    public User getUserState(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String userID = preferences.getString(USER_ID, "no ID set");
        String firstName = preferences.getString(FIRST_NAME, "no firstName set");
        String lastName = preferences.getString(LAST_NAME, "no lastName set");
        String email = preferences.getString(EMAIL, "no email set");
        String phoneNumber = preferences.getString(PHONE_NO, "no phoneNo set");
        String academicStatus = preferences.getString(ACADEMIC_STATUS, "no academicStatus set");
        String profilePicture = preferences.getString(PROFILE_PICTURE, "no profilePicture set");
        String username = preferences.getString(USER_NAME, "no username set");
        boolean isLoggedIn = preferences.getBoolean(LOGIN_STATE, false);

        User user = new User(firstName, lastName, email, phoneNumber, academicStatus, null, null);
        user.setUserID(userID);
        user.setProfilePicture(profilePicture);
        user.setUsername(username);
        user.setLoginState(isLoggedIn);

        return user;

    }

    public void setLogOutState(Context context) {
        editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(LOGIN_STATE, false);
        editor.apply();
    }

    public void saveTokenState(String token, Context context) {
        editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
                .putBoolean(FCM_TOKEN_CHANGED, true)
                .putString(FCM_TOKEN, token);
        editor.apply();
    }

    public boolean isTokenChanged(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(FCM_TOKEN_CHANGED, false);
    }

    public String getToken(Context context) {
        return context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE).getString(FCM_TOKEN,"no token set");
    }

}
