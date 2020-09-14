package com.codefusiongroup.gradshub.common;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;


// singleton class that provides the application context
public class GradsHubApplication extends Application {

    private static GradsHubApplication instance;

    // NOTE: constructor must be public so that android OS can instantiate an instance of the application
    public GradsHubApplication() {
        instance = this;
    }

    public static Context getContext() {
        if(instance == null) {
            instance = new GradsHubApplication();
        }

        return instance;
    }

    public static void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
