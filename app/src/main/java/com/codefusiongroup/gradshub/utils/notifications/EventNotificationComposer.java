package com.codefusiongroup.gradshub.utils.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.models.Schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class EventNotificationComposer {

    private static final String CHANNEL_ID = "0";
    private static EventNotificationComposer instance;

    private EventNotificationComposer() { }

    // singleton pattern
    public static EventNotificationComposer getInstance() {
        if (instance == null) {
            instance = new EventNotificationComposer();
        }
        return instance;
    }

    // method ensure that only events that are coming in the near future are scheduled for notification.
    public void setNotificationCalender(Schedule event, int eventMonth, int eventStartDay, int currentMonth) {

        // calendar instance for which we use to set notification date
        Calendar notificationCalendar = Calendar.getInstance();

        // used to check if calender properties changed so notification can be scheduled.
        boolean calendarFieldChanged = false;

        // schedule the notification to appear on the 1st of the month before the event month
        if (currentMonth + 2 == eventMonth) {
            notificationCalendar.set(Calendar.MONTH, currentMonth + 1);
            notificationCalendar.set(Calendar.DAY_OF_MONTH, 1);
            calendarFieldChanged = true;
        }

        // schedule the notification to appear on the 1st of the event month
        else if (currentMonth + 1 == eventMonth && eventStartDay == 8) {
            notificationCalendar.set(Calendar.MONTH, eventMonth);
            notificationCalendar.set(Calendar.DAY_OF_MONTH, 1);
            calendarFieldChanged = true;
        }

        // events that start from the 9th onwards of the event month
        else if (currentMonth == eventMonth && eventStartDay > 8) {
            notificationCalendar.set(Calendar.MONTH, eventMonth);
            notificationCalendar.set(Calendar.DAY_OF_MONTH, eventStartDay - 7);
            calendarFieldChanged = true;
        }

        // events that start on the first week of the event month but currently its the month before the
        // event month.
        else if (currentMonth + 1 == eventMonth && eventStartDay <= 7) {

            notificationCalendar.set(Calendar.MONTH, currentMonth);

            switch (eventStartDay) {

                case 1:
                    notificationCalendar.set(Calendar.DAY_OF_MONTH, 22);
                    break;

                case 2:
                    notificationCalendar.set(Calendar.DAY_OF_MONTH, 23);
                    break;

                case 3:
                    notificationCalendar.set(Calendar.DAY_OF_MONTH, 24);
                    break;

                case 4:
                    notificationCalendar.set(Calendar.DAY_OF_MONTH, 25);
                    break;

                case 5:
                    notificationCalendar.set(Calendar.DAY_OF_MONTH, 26);
                    break;

                case 6:
                    notificationCalendar.set(Calendar.DAY_OF_MONTH, 27);
                    break;

                case 7:
                    // take 28 as max since  Feb is at most 28 days long in case the current month is Feb
                    notificationCalendar.set(Calendar.DAY_OF_MONTH, 28);
                    break;
            }

            calendarFieldChanged = true;

        }

        // if one of the conditions above was satisfied then schedule a notification for this event
        if ( calendarFieldChanged ) {

            // validate the event link
            String link = validateEventLinkFormat( event.getLink() );
            Uri uri = Uri.parse(link);

            // set the time when the notification should be sent
            notificationCalendar.set( Calendar.HOUR_OF_DAY, 15 );
            notificationCalendar.set( Calendar.MINUTE, 0 );
            notificationCalendar.set( Calendar.SECOND, 0 ); // not important, so set to 0

            // ALWAYS recompute the calendar after using add, set, roll
            Date date = notificationCalendar.getTime();
            Random random = new Random();
            int notificationID = random.nextInt(Integer.MAX_VALUE); // ensure uniqueness of notifications
            scheduleNotification(GradsHubApplication.getContext(), notificationID, event, date, uri);

        }

    }


    // N.B: notificationId allows you to update the notification later on.
    // notificationId is unique int for each notification that you must define
    private void scheduleNotification(Context context, int notificationId, Schedule event, Date date, Uri uri) {

        Log.d("EventNotifComposer", "scheduleNotification for --> "+event.getTitle()+", on the date: "+date);
        PendingIntent openEventLinkPendingIntent = null;
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            openEventLinkPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        }

        String eventTitle = event.getTitle();
        String dateStr = event.getDate();
        String eventDate = dateStr.substring(dateStr.indexOf(":") + 2);
        String placeStr = event.getPlace();
        String eventPlace = placeStr.substring(placeStr.indexOf(":") + 2);

        // set notification properties
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                // Notification Channel Id is ignored for Android pre O (26).
                .setSmallIcon(R.mipmap.applogo) // shows app icon next to the notification
                .setContentTitle("upcoming event, " + eventTitle + ".")
                .setContentText("held on " + eventDate + " at " + eventPlace)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setShowWhen(false) // doesn't show notification timestamp
                .setAutoCancel(true)// automatically removes the notification when the user taps it.
                .setOngoing(false); // dismiss notification on swipe gesture

        if(openEventLinkPendingIntent != null) {
            builder.setContentIntent(openEventLinkPendingIntent);
        }

        // builder.build() returns the notification to be published
        Notification notification = builder.build();

        // this part schedules the notification and calls EventNotificationPublisher to publish the notification
        Intent notificationIntent = new Intent(context, EventNotificationPublisher.class);
        notificationIntent.putExtra( EventNotificationPublisher.NOTIFICATION_ID, notificationId );
        notificationIntent.putExtra( EventNotificationPublisher.NOTIFICATION, notification );
        // 4th argument can be PendingIntent.FLAG_UPDATE_CURRENT or 0
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);

    }


    private String validateEventLinkFormat(String eventLink) {

        String link;
        if ( !eventLink.startsWith("https://") && !eventLink.startsWith("http://") ) {
            link = "http://" + eventLink;
        }
        else {
            link = eventLink;
        }

        return link;
    }


    public void processFavouredEvents(List<String> userPreviouslyFavouredEvents, List<Schedule> eventsSchedule, int currentDay, int currentMonth) {

        if ( userPreviouslyFavouredEvents.size() > 0 ) {

            for ( String favoured_event_id : userPreviouslyFavouredEvents ) {

                // search for the current event id in the favoured events ids
                for (Schedule event : eventsSchedule) {
                    String eventID = event.getId();

                    if ( eventID.equals(favoured_event_id) ) {

                        // extract the event date
                        String dateStr = event.getDate();
                        String compSubstring = dateStr.substring(dateStr.indexOf(":") + 2);

                        // extract event month
                        String month = compSubstring.substring(0, compSubstring.indexOf(" "));
                        int eventStartMonth = MonthsConstants.setMonths(month);

                        // extract event start day
                        int eventStartDay;
                        String eventDuration = compSubstring.substring(compSubstring.indexOf(" ") + 1, compSubstring.indexOf(","));
                        if (eventDuration.length() == 1) {
                            eventStartDay = Integer.parseInt(eventDuration);
                        } else {
                            // check if second character is a number
                            if (Character.isDigit(eventDuration.charAt(1))) {
                                eventStartDay = Integer.parseInt(eventDuration.substring(0, 2));
                            } else {
                                // must be a hyphen
                                eventStartDay = Integer.parseInt(eventDuration.substring(0, 1));
                            }
                        }

                        // check if the notification based on the date of this event needs to be
                        // scheduled to appear a week before the event start day if it has already
                        // been shown a month before the event month OR if it still needs to be scheduled
                        // to appear a month before the event month IF it happens in the near future.
                        // (NOTE: eventsSchedule only holds upcoming events so we are scheduling notifications
                        // for valid dates but we want to check how soon in the near future is this event
                        // going to happen)

                        //==========================================================================
                        // NOTE: if this condition is satisfied, then the notification that is set
                        // to appear a week before this event day hasn't been scheduled before.
                        // check if the notification for this event needs to be scheduled to appear
                        // a month before.
                        if (eventStartMonth - 2 == currentMonth && currentDay <= 28) { // feb at most 28 days long
                            //setNotificationCalender(event, eventStartMonth, eventStartDay);
                            setNotificationCalender(event, eventStartMonth, eventStartDay, currentMonth);
                        }
                        //==========================================================================

                        // NOTE: if one of the following conditions is true, then it means the notification
                        // that is set to appear a month before the event month for this event has already
                        // been shown.
                        // we check if the notification for this event needs to be scheduled to appear
                        // a week before the event day given that the number of days that remain
                        // between the event day and the day when the notification is set to appear are >= 7.

                        //==========================================================================
                        // for events that start on the first week of the event month but its currently
                        // the month before the event month, then the date to be notified
                        // is in the range of days between (22 - 28) inclusive, taking 28 as max and
                        // 22 as the min since Feb is at most 28 days long
                        if (currentMonth + 1 == eventStartMonth && eventStartDay <= 7) {

                            // notified on the 22nd if event day is on the 1st of the event month.
                            if (eventStartDay == 1 && currentDay < 22) {
                                //setNotificationCalender(event, eventStartMonth, eventStartDay);
                                setNotificationCalender(event, eventStartMonth, eventStartDay, currentMonth);
                            }

                            // notified on the 23rd if event day is on the 2nd of the event month.
                            else if (eventStartDay == 2 && currentDay < 23) {
                                //setNotificationCalender(event, eventStartMonth, eventStartDay);
                                setNotificationCalender(event, eventStartMonth, eventStartDay, currentMonth);
                            }

                            // notified on the 24th if event day is on the 3rd of the event month.
                            else if (eventStartDay == 3 && currentDay < 24) {
                                //setNotificationCalender(event, eventStartMonth, eventStartDay);
                                setNotificationCalender(event, eventStartMonth, eventStartDay, currentMonth);
                            }

                            // notified on the 25th if event day is on the 4th of the event month.
                            else if (eventStartDay == 4 && currentDay < 25) {
                                //setNotificationCalender(event, eventStartMonth, eventStartDay);
                                setNotificationCalender(event, eventStartMonth, eventStartDay, currentMonth);
                            }

                            // notified on the 26th if event day is on the 5th of the event month.
                            else if (eventStartDay == 5 && currentDay < 26) {
                                //setNotificationCalender(event, eventStartMonth, eventStartDay);
                                setNotificationCalender(event, eventStartMonth, eventStartDay, currentMonth);
                            }

                            // notified on the 27th if event day is on the 6th of the event month.
                            else if (eventStartDay == 6 && currentDay < 27) {
                                //setNotificationCalender(event, eventStartMonth, eventStartDay);
                                setNotificationCalender(event, eventStartMonth, eventStartDay, currentMonth);
                            }

                            // notified on the 28th if event day is on the 7th of the event month.
                            else if (eventStartDay == 7 && currentDay < 28) {
                                //setNotificationCalender(event, eventStartMonth, eventStartDay);
                                setNotificationCalender(event, eventStartMonth, eventStartDay, currentMonth);
                            }

                        }
                        //==========================================================================

                        // for events that start on the 8th exactly of the event month but it's currently
                        // the month before the event month, then the date to be notified is the 1st
                        // of the event month.
                        else if (currentMonth + 1 == eventStartMonth && eventStartDay == 8) {
                            if (currentDay <= 28) { // feb is at most 28 days long.
                                //setNotificationCalender(event, eventStartMonth, eventStartDay);
                                setNotificationCalender(event, eventStartMonth, eventStartDay, currentMonth);
                            }
                        }
                        //==========================================================================

                        // for events that start from the 9th onwards on the event month, the date
                        // to be notified a week before is the offset that remains after 7 days are
                        // subtracted from the evenStartDay
                        else if (currentMonth == eventStartMonth && eventStartDay > 8) {
                            if (currentDay < (eventStartDay - 7)) {
                                //setNotificationCalender(event, eventStartMonth, eventStartDay);
                                setNotificationCalender(event, eventStartMonth, eventStartDay, currentMonth);
                            }
                        }
                        //==========================================================================

                        // break out of inner loop since we have found the matching event id and consider
                        // the next favoured event id.
                        break;
                    }
                }

            }

        }

    }

}
