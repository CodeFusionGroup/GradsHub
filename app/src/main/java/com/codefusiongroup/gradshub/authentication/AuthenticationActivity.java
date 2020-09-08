package com.codefusiongroup.gradshub.authentication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.codefusiongroup.gradshub.R;


public class AuthenticationActivity extends AppCompatActivity {


    private static final String CHANNEL_ID = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        createNotificationChannel();
    }


    private void createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "gradsHub_notification_channel"; // must be something meaningful
            String description = "gradsHub_notification_description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }

    }


}
