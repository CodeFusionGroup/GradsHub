package com.example.gradshub.main;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;


public class EventNotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION = "notification";


    @Override
    public void onReceive(Context context, Intent intent) {

        /*
        Show the notification:
        To make the notification appear, call NotificationManagerCompat.notify(),
        passing it a unique ID for the notification and the result of
        NotificationCompat.Builder.build().
        */

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        if (intent != null) {
            // get intent extras from notificationIntent in MainActivity.class
            int notificationId = intent.getIntExtra(NOTIFICATION_ID, 0);
            Notification notification = intent.getParcelableExtra(NOTIFICATION);
            /*
             To update this notification after you've issued it, call NotificationManagerCompat.notify() again,
             passing it a notification with the same ID you used previously. If the previous notification has
             been dismissed, a new notification is created instead
            */
            assert notification != null;
            notificationManagerCompat.notify(notificationId, notification);
        }

    }

}
