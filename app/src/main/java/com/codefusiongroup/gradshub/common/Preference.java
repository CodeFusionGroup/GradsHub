package com.codefusiongroup.gradshub.common;

import android.content.Context;
import android.content.SharedPreferences;


public class Preference {


    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;

    public Preference(){
    }

    public static void setLoggedIn(Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putBoolean("loggedIn",true);
        editor.commit();
    }
    public static void setLoggedOut(Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putBoolean("loggedIn",false);
        editor.commit();
    }
    public static boolean getLoggedIn(Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        return prefs.getBoolean("loggedIn",false);
    }


    public static void saveID(String id,Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putString("user_id",id);
        editor.commit();
    }
    public static String getID(Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        return prefs.getString("user_id","ID not found");
    }

    public static void saveFName(String fname, Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putString("user_fname",fname);
        editor.commit();
    }
    public static String getFName(Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        return prefs.getString("user_fname","Fname Not Found");
    }

    public static void saveLName(String lname, Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putString("user_lname",lname);
        editor.commit();
    }
    public static String getLName(Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        return prefs.getString("user_lname","Lname Not Found");
    }

    public static void saveEmail(String email, Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putString("user_email",email);
        editor.commit();
    }
    public static String getEmail(Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        return prefs.getString("user_email","Email Not Found");
    }

    public static void savePhoneNo(String phoneNo, Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putString("user_phone_no",phoneNo);
        editor.commit();
    }
    public static String getPhoneNo(Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        return prefs.getString("user_phone_no","Phone Number Not Found");
    }

    public static void saveAcadStatus(String acadStatus, Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putString("user_acad_status",acadStatus);
        editor.commit();
    }
    public static String getAcadStatus(Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        return prefs.getString("user_acad_status","Acad Status Not Found");
    }

    public static void saveFCMToken(String fcmToken, Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putString("user_fcm_token",fcmToken);
        editor.commit();
    }
    public static String getFCMToken(Context context){
        prefs = context.getSharedPreferences("com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY",Context.MODE_PRIVATE);
        return prefs.getString("user_fcm_token","Token Not Found");
    }





}
