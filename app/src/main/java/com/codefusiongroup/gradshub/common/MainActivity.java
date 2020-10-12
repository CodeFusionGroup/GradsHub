package com.codefusiongroup.gradshub.common;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;
import com.codefusiongroup.gradshub.friends.FriendsFragment;
import com.codefusiongroup.gradshub.groups.searchGroups.exploreGroupsList.ExploreGroupsFragment;
import com.codefusiongroup.gradshub.messaging.openChats.OpenChatsFragment;
import com.codefusiongroup.gradshub.events.ScheduleListFragment;
import com.codefusiongroup.gradshub.groups.userGroups.userGroupsList.MyGroupsListFragment;
import com.codefusiongroup.gradshub.groups.userGroups.userGroupProfile.MyGroupsProfileFragment;
import com.codefusiongroup.gradshub.posts.postcomments.Comment;
import com.codefusiongroup.gradshub.posts.postcomments.GroupPostCommentsFragment;
import com.codefusiongroup.gradshub.messaging.searchableUsers.UsersListFragment;
import com.codefusiongroup.gradshub.common.models.Chat;
import com.codefusiongroup.gradshub.common.models.Post;
import com.codefusiongroup.gradshub.common.models.ResearchGroup;
import com.codefusiongroup.gradshub.common.models.Schedule;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.NetworkRequestQueue;
import com.codefusiongroup.gradshub.profile.ProfileFragment;
import com.codefusiongroup.gradshub.utils.EventNotificationPublisher;
import com.codefusiongroup.gradshub.utils.MonthsConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.VisibleForTesting;
import androidx.core.app.NotificationCompat;
import androidx.core.view.MenuCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity implements MyGroupsListFragment.OnMyGroupsListFragmentInteractionListener,
        ExploreGroupsFragment.OnExploreGroupsFragmentInteractionListener,
        MyGroupsProfileFragment.OnPostsListFragmentInteractionListener,
        GroupPostCommentsFragment.OnCommentsListFragmentInteractionListener,
        ScheduleListFragment.OnScheduleListFragmentInteractionListener,
        OpenChatsFragment.OnOpenChatsFragmentInteractionListener,
        UsersListFragment.OnUsersListFragmentInteractionListener,
        FriendsFragment.OnFriendsListFragmentInteractionListener,
        ProfileFragment.OnProfileUpdateSuccessfulListener {


    private static final String TAG = "MainActivity";

    private static final String CHANNEL_ID = "0";

    private ImageView mImageView;
    private TextView mFullNameTV;
    private TextView mEmailTV;

    private AppBarConfiguration mAppBarConfiguration;

    public User user; // used in other fragments, so has public access.
    private int currentYear, currentMonth, currentDay;

    private List<Schedule> eventsSchedule = new ArrayList<>();
    // used to form notifications for the current user's favourite events
    private ArrayList<String> userPreviouslyFavouredEvents = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar rightNow = Calendar.getInstance();
        currentYear = rightNow.get(Calendar.YEAR);
        currentMonth = rightNow.get(Calendar.MONTH);
        currentDay = rightNow.get(Calendar.DAY_OF_MONTH);

        // user must first be initialised before calling getUserFavouredEvents()
        Intent intent = getIntent();
        user = intent.getParcelableExtra("USER");

        getUserFavouredEvents();

        Log.d(TAG, "onCreate() --> global values, currentYear: "+currentYear+", currentMonth: "+currentMonth+", currentDay: "+currentDay);
        readScheduleFromFile(); // must be called after setting calendar date as above

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        // NavController is responsible for replacing the contents of the NavHost with the new destination.
        // (layout content_main contains the navigation host fragment for MainActivity)
        NavController navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);

        // setting up app bar with navigation graph destinations
        mAppBarConfiguration = new AppBarConfiguration.Builder( navController.getGraph() ).setOpenableLayout(drawer).build();


        // By calling this method, the title in the action bar will automatically be updated when the destination changes.
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        // updates the UI with the contents of the current destination.
        NavigationUI.setupWithNavController(navigationView, navController);


        // pass arguments to start destination (Home screen)
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        navController.setGraph(R.navigation.main_navigation, bundle);


        // setting side menu bar with selected user profile details
        View headerView = navigationView.getHeaderView(0);
        mImageView = headerView.findViewById(R.id.img_container);

        if ( !user.getProfilePicture().equals("no profilePicture set") ) {
            Uri uri = Uri.parse( user.getProfilePicture() );
            Glide.with( this ).load(uri).into(mImageView);
        }
        else {
            Glide.with( this).load(R.drawable.ic_account_circle).into(mImageView);
        }

        mFullNameTV = headerView.findViewById(R.id.userFullNameTV);
        mEmailTV = headerView.findViewById(R.id.userEmailTV);

        mFullNameTV.setText( user.getFullName() );
        mEmailTV.setText( user.getEmail() );

    }


    private void processFavouredEvents(ArrayList<String> userPreviouslyFavouredEvents) {

        if ( userPreviouslyFavouredEvents.size() > 0 ) {

            for ( String favoured_event_id : userPreviouslyFavouredEvents ) {

                // search for the current event id in the favoured events ids
                for (Schedule event : eventsSchedule) {
                    String eventID = event.getId();

                    if ( eventID.equals(favoured_event_id) ) {

                        Log.d(TAG, "processFavouredEvents() --> processing event: "+event.getTitle()+", with id: " + favoured_event_id);

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
                            Log.d(TAG, "set notificationCalendar to show notification on the 1st of the month before event month, for the event: " + event.getTitle());
                            setNotificationCalender(event, eventStartMonth, eventStartDay);
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
                            Log.d(TAG, "executed condition: ( currentMonth + 1 == eventStartMonth && eventStartDay <= 7 ) true");

                            // notified on the 22nd if event day is on the 1st of the event month.
                            if (eventStartDay == 1 && currentDay < 22) {
                                Log.d(TAG, "executed sub-condition: ( eventStartDay == 1 && currentDay < 22 ) true");
                                Log.d(TAG, "set notificationCalendar to show notification a week before, for the event: " + event.getTitle());
                                setNotificationCalender(event, eventStartMonth, eventStartDay);
                            }

                            // notified on the 23rd if event day is on the 2nd of the event month.
                            else if (eventStartDay == 2 && currentDay < 23) {
                                Log.d(TAG, "executed sub-condition: ( eventStartDay == 2 && currentDay < 23 ) true");
                                Log.d(TAG, "set notificationCalendar to show notification a week before, for the event: " + event.getTitle());
                                setNotificationCalender(event, eventStartMonth, eventStartDay);
                            }

                            // notified on the 24th if event day is on the 3rd of the event month.
                            else if (eventStartDay == 3 && currentDay < 24) {
                                Log.d(TAG, "executed sub-condition: ( eventStartDay == 3 && currentDay < 24) true");
                                Log.d(TAG, "set notificationCalendar to show notification a week before, for the event: " + event.getTitle());
                                setNotificationCalender(event, eventStartMonth, eventStartDay);
                            }

                            // notified on the 25th if event day is on the 4th of the event month.
                            else if (eventStartDay == 4 && currentDay < 25) {
                                Log.d(TAG, "executed sub-condition: ( eventStartDay == 4 && currentDay < 25 ) true");
                                Log.d(TAG, "set notificationCalendar to show notification a week before, for the event: " + event.getTitle());
                                setNotificationCalender(event, eventStartMonth, eventStartDay);
                            }

                            // notified on the 26th if event day is on the 5th of the event month.
                            else if (eventStartDay == 5 && currentDay < 26) {
                                Log.d(TAG, "executed sub-condition: ( eventStartDay == 5 && currentDay < 26 ) true");
                                Log.d(TAG, "set notificationCalendar to show notification a week before, for the event: " + event.getTitle());
                                setNotificationCalender(event, eventStartMonth, eventStartDay);
                            }

                            // notified on the 27th if event day is on the 6th of the event month.
                            else if (eventStartDay == 6 && currentDay < 27) {
                                Log.d(TAG, "executed sub-condition: ( eventStartDay == 6 && currentDay < 27 ) true");
                                Log.d(TAG, "set notificationCalendar to show notification a week before, for the event: " + event.getTitle());
                                setNotificationCalender(event, eventStartMonth, eventStartDay);
                            }

                            // notified on the 28th if event day is on the 7th of the event month.
                            else if (eventStartDay == 7 && currentDay < 28) {
                                Log.d(TAG, "executed sub-condition: ( eventStartDay == 7 && currentDay < 28 ) true");
                                Log.d(TAG, "set notificationCalendar to show notification a week before, for the event: " + event.getTitle());
                                setNotificationCalender(event, eventStartMonth, eventStartDay);
                            }

                        }
                        //==========================================================================

                        // for events that start on the 8th exactly of the event month but it's currently
                        // the month before the event month, then the date to be notified is the 1st
                        // of the event month.
                        else if (currentMonth + 1 == eventStartMonth && eventStartDay == 8) {
                            Log.d(TAG, "executed condition: ( currentMonth + 1 == eventStartMonth && eventStartDay == 8 ) true ");
                            if (currentDay <= 28) { // feb is at most 28 days long.
                                Log.d(TAG, "executed sub-condition: ( currentDay <= 28 ) true");
                                Log.d(TAG, "set notificationCalendar to show notification on the 1st of the event month, for the event: " + event.getTitle());
                                setNotificationCalender(event, eventStartMonth, eventStartDay);
                            }
                        }

                        //==========================================================================

                        // for events that start from the 9th onwards on the event month, the date
                        // to be notified a week before is the offset that remains after 7 days are
                        // subtracted from the evenStartDay
                        else if (currentMonth == eventStartMonth && eventStartDay > 8) {
                            Log.d(TAG, "executed condition: ( currentMonth == eventStartMonth && eventStartDay > 8 ) true ");
                            if (currentDay < (eventStartDay - 7)) {
                                Log.d(TAG, "executed sub-condition: ( currentDay < (eventStartDay-7) ) true ");
                                Log.d(TAG, "set notificationCalendar to show notification 7 days before the event start day, for the event: " + event.getTitle());
                                setNotificationCalender(event, eventStartMonth, eventStartDay);
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


    // method ensure that only events that are coming in the near future are scheduled for notification.
    private void setNotificationCalender(Schedule event, int eventMonth, int eventStartDay) {

        // calendar instance for which we use to set notification date
        Calendar notificationCalendar = Calendar.getInstance();

        // used to check if calender properties changed so notification can be scheduled.
        boolean calendarFieldChanged = false;

        // schedule the notification to appear on the 1st of the month before the event month
        if (currentMonth + 2 == eventMonth) {
            Log.d(TAG, "condition executed: ( currentMonth + 2 == eventMonth && currentDay <= 28 ) true");
            notificationCalendar.set(Calendar.MONTH, currentMonth + 1);
            notificationCalendar.set(Calendar.DAY_OF_MONTH, 1);
            calendarFieldChanged = true;
        }

        // schedule the notification to appear on the 1st of the event month
        else if (currentMonth + 1 == eventMonth && eventStartDay == 8) {
            Log.d(TAG, "condition executed: ( currentMonth + 1 == eventMonth && eventStartDay == 8 ) true");
            notificationCalendar.set(Calendar.MONTH, eventMonth);
            notificationCalendar.set(Calendar.DAY_OF_MONTH, 1);
            calendarFieldChanged = true;
        }

        // events that start from the 9th onwards of the event month
        else if (currentMonth == eventMonth && eventStartDay > 8) {
            Log.d(TAG, "condition executed: ( currentMonth == eventMonth && eventStartDay >= 8 ) true");
            notificationCalendar.set(Calendar.MONTH, eventMonth);
            notificationCalendar.set(Calendar.DAY_OF_MONTH, eventStartDay - 7);
            calendarFieldChanged = true;
        }

        // events that start on the first week of the event month but currently its the month before the
        // event month.
        else if (currentMonth + 1 == eventMonth && eventStartDay <= 7) {

            notificationCalendar.set(Calendar.MONTH, currentMonth);
            Log.d(TAG, "condition executed: ( currentMonth + 1 == eventMonth && eventStartDay <= 7 ) true, eventStartDay: "+eventStartDay);

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
            Uri uri = null;
            String link = validateEventLinkFormat( event.getLink() );
            if (link != null) {
                Log.d(TAG, "event link is: "+link+", for event: "+event.getTitle());
                uri = Uri.parse(link);
            }
            else {
                Log.d(TAG, "event link is null for event: "+ event.getTitle());
            }

            // set the time when the notification should be sent
            notificationCalendar.set( Calendar.HOUR_OF_DAY, 15 );
            notificationCalendar.set( Calendar.MINUTE, 0 );
            notificationCalendar.set( Calendar.SECOND, 0 ); // not important, so set to 0

            // ALWAYS recompute the calendar after using add, set, roll
            Date date = notificationCalendar.getTime();
            Log.d(TAG, "calender fields changed, notification scheduled for event: " + event.getTitle()+" on date: "+ date);
            Log.d(TAG, "===============================================================================================" );
            Random random = new Random();
            int notificationID = random.nextInt(Integer.MAX_VALUE); // ensure uniqueness of notifications
            scheduleNotification(this.getApplicationContext(), notificationID, event, date, uri);

        }
        else {
            Log.d(TAG, "calender fields were not changed, notification was not scheduled for event: " + event.getTitle() );
            Log.d(TAG, "===============================================================================================" );
        }

    }


    public String validateEventLinkFormat(String eventLink) {

        String link;
        if ( !eventLink.startsWith("https://") && !eventLink.startsWith("http://") ) {
            link = "http://" + eventLink;
        }
        else {
            link = eventLink;
        }

        return link;
    }


    // N.B: notificationId allows you to update the notification later on.
    // notificationId is unique int for each notification that you must define
    public void scheduleNotification(Context context, int notificationId, Schedule event, Date date, Uri uri) {

        PendingIntent openEventLinkPendingIntent = null;
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            openEventLinkPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        }
        else {
            Log.d(TAG, "uri is null for event: "+event.getTitle());
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


    // the options menu is the primary collection of menu items for an activity.
    // initialise the contents of the Activity's standard menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ) {

            case R.id.action_logout:

                // Set the login Shared Preferences to false
                UserPreferences.getInstance().setLogOutState(MainActivity.this);
                Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
                startActivity(intent);
                finish(); // finish MainActivity
                return true;

            default:
                // default to any selected item on the app bar if no case is found above
                return super.onOptionsItemSelected(item);
        }

    }


    // this method is called whenever the user chooses to navigate up (back button) within your application's activity hierarchy from
    // the action bar
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }


    @Override
    public void onMyGroupsListFragmentInteraction(ResearchGroup group) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("group_item", group);
        bundle.putParcelable("user", user);
        NavController navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
        navController.navigate(R.id.action_myGroupsFragment_to_myGroupProfileFragment, bundle);
    }


    @Override
    public void onExploreGroupsFragmentInteraction(ResearchGroup group) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("group_item", group);
        NavController navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
        navController.navigate(R.id.action_exploreGroupsFragment_to_exploredGroupProfileFragment, bundle);
    }


    @Override
    public void onPostsListFragmentInteraction(Post post) {
        // empty on purpose
    }


    @Override
    public void onCommentsListFragmentInteraction(Comment comment) {
        // empty on purpose
    }


    @Override
    public void onScheduleListFragmentInteraction(Schedule event) {

        if ( !event.getLink().startsWith("https://") && !event.getLink().startsWith("http://") ) {

            String link = "http://" + event.getLink();
            Uri uri = Uri.parse(link);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity( Intent.createChooser(intent, "choose Browser") );
        }
        else {
            Uri uri = Uri.parse( event.getLink() );
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(Intent.createChooser( intent, "choose Browser") );
        }

        Toast.makeText(this, "selected: " + event.getTitle(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onOpenChatsFragmentInteraction(Chat chat) {
        Bundle bundle = new Bundle();
        bundle.putString("name", chat.getCorrespondentName());
        bundle.putString("id", chat.getCorrespondentID());
        NavController navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
        navController.navigate(R.id.action_chatsListFragment_to_chatMessagesFragment, bundle);
    }


    @Override
    public void onUsersListFragmentInteraction(User user) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("selected_user", user);
        NavController navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
        navController.navigate(R.id.action_usersListFragment_to_userProfileFragment, bundle);
    }


    @Override
    public void onFriendsListFragmentInteraction(User user) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("selected_user", user);
        NavController navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
        navController.navigate(R.id.action_friendsListFragment_to_userProfileFragment, bundle);
    }


    // this method gets schedule from website and writes the schedules to a file (Schedule-data.txt)
    // NOTE: method is not called anywhere to avoid making multiple request to the website for data
    // still have to schedule the requests since data on the website can change
    private void requestSchedule() {

        try {

            Document document = Jsoup.connect("https://github.com/abhshkdz/ai-deadlines/blob/gh-pages/_data/conferences.yml").get();
            Elements scheduleData = document.getElementsByTag("tr");

            // Create a new file to store the events
            //File scheduleFile = new File("Schedule-data.txt");
            File scheduleFile = new File("test.txt");

            if ( scheduleFile.exists() ) {
                Log.d(TAG, "schedule file exits, name is: "+ scheduleFile.getName() );
            }
            else {
                Log.d(TAG, "schedule file test.txt does not exist");
            }


            // write the events to the specified file
            FileWriter fileWriter = new FileWriter( scheduleFile.getName() );
            //FileWriter fileWriter = new FileWriter( "Schedule-data.txt" );

            int count = 0;
            for (Element data : scheduleData) {
                count++;

                if (count < 3) {
                    continue;
                }

                fileWriter.write( data.text() );
                //fileWriter.write(System.lineSeparator()); // lineSeparator() giving error when uncommented

                //==========================================================
                String lineSeparator = System.getProperty("line.separator");
                assert lineSeparator != null;
                fileWriter.write(lineSeparator);
                //==========================================================
            }

            // must check for null
            fileWriter.close();

            //====================================================================================
            // Parse the file
            FileReader fileReader = new FileReader("Schedule-data.txt");
            Scanner scanner = new Scanner(fileReader);

            while (scanner.hasNext()) {
                String scanLine = scanner.nextLine();

                // skips empty lines
                if (scanLine.length() == 0) {
                    continue;
                }
                else {
                    Log.d(TAG, "scanLine: "+ scanLine);
                }

            }
            scanner.close();
            //====================================================================================

        } catch (IOException e) {
            Log.e(TAG, "Error connecting to url: "+ "https://github.com/abhshkdz/ai-deadlines/blob/gh-pages/_data/conferences.yml", e.getCause());
            e.printStackTrace();
        }

    }


    private void readScheduleFromFile() {

        eventsSchedule.clear(); // clear in case activity is recreated due to configuration changes
        StringBuilder contents = new StringBuilder();
        InputStream is = null;
        BufferedReader reader = null;

        try {
            AssetManager am = this.getAssets();
            is = am.open("Schedule-data.txt");
            reader = new BufferedReader(new InputStreamReader(is));
            contents = new StringBuilder(reader.readLine());
            String line;

            while ((line = reader.readLine()) != null) {
                contents.append('\n').append(line);
            }

            // temp holds all the events schedules
            String[] temp = contents.toString().split("\n\n");

            // from temp we take each event and form an object of type Schedule
            for (String eventStr : temp) {

                String title = null, id = null, link = null, deadline = null, timezone = null, date = null, place = null;
                String[] eventComponents = eventStr.split("\n");

                for (String component : eventComponents) {

                    // select only relevant parts of the schedule item
                    String compSubstring = component.substring( component.indexOf(":") + 2 ); // for title, id and link

                    if ( component.startsWith("- title") ) {
                        title = compSubstring;

                    } else if ( component.startsWith("id") ) {
                        id = compSubstring;

                    } else if ( component.startsWith("link") ) {
                        link = compSubstring;

                    } else if ( component.startsWith("deadline") ) {
                        deadline = component;

                    } else if ( component.startsWith("timezone") ) {
                        timezone = component;

                    } else if ( component.startsWith("date") ) {

                        // check if date is relevant

                        String year = compSubstring.substring( compSubstring.lastIndexOf(" ") + 1 );
                        int eventYear = Integer.parseInt(year);

                        String month = compSubstring.substring( 0, compSubstring.indexOf(" ") );
                        int eventStartMonth = MonthsConstants.setMonths(month);

                        int eventStartDay;
                        String eventDuration = compSubstring.substring( compSubstring.indexOf(" ") + 1, compSubstring.indexOf(",") );
                        if (eventDuration.length() == 1) {
                            eventStartDay = Integer.parseInt(eventDuration);
                        }
                        else {
                            // check if second character is a number
                            if ( Character.isDigit( eventDuration.charAt(1) ) ) {
                                eventStartDay = Integer.parseInt( eventDuration.substring(0, 2) );
                            }
                            else {
                                // must be a hyphen
                                eventStartDay = Integer.parseInt( eventDuration.substring(0, 1) );
                            }
                        }

                        if (eventYear < currentYear) {
                            Log.d(TAG, "readScheduleFromFile() --> event year has passed for event: "+title);
                            break;
                        }
                        else {
                            Log.d(TAG, "readScheduleFromFile() --> else executed since either eventYear >= currentYear for event: "+title+", eventYear:  "+eventYear+", currentYear: "+currentYear);

                            if (eventYear > currentYear) {
                                date = component;
                            }
                            else {
                                Log.d(TAG, "readScheduleFromFile() --> else executed since eventYear must be equal to currentYear,  eventYear:  "+eventYear+", currentYear: "+currentYear);
                                if (eventStartMonth > currentMonth) {
                                    Log.d(TAG, "readScheduleFromFile() --> executed sub-condition: ( eventStartMonth > currentMonth ) true, eventStartMonth:  "+eventStartMonth+", currentMonth: "+currentMonth);
                                    date = component;
                                }
                                else if (eventStartMonth == currentMonth && eventStartDay > currentDay) {
                                    Log.d(TAG, "readScheduleFromFile() --> executed sub-condition: ( eventStartMonth == currentMonth && eventStartDay > currentDay ) true, eventStartMonth: "+eventStartMonth+", currentMonth: "+currentMonth+", eventStartDay:  "+eventStartDay+", currentDay: "+currentDay);
                                    date = component;
                                }
                                else {
                                    Log.d(TAG, "readScheduleFromFile() --> event month or day has passed for event: "+title);
                                    break;
                                }
                            }

                        }

                    } else if ( component.startsWith("place") ) {
                        place = component;
                    }

                }

                // filter based on date
                if (date != null) {
                    Log.i("MainActivity", "readScheduleFromFile() --> added event: "+title+", with eventDate: "+date);
                    eventsSchedule.add(new Schedule(id, title, link, deadline, timezone, date, place));
                }

            }


        } catch (final Exception e) {
            e.printStackTrace();

        } finally {
            // releases system resources associated with this stream and reader
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }

        }

    }


    private void getUserFavouredEvents() {

        String url = "https://gradshub.herokuapp.com/api/User/retrievefavourites.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("user_id", user.getUserID());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverGetUserFavouredEventsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /*
                        NOTE: regarding the display of a toast message here, its also a similar explanation given below
                        on the corresponding server response method for this method (i.e serverGetUserFavouredEventsResponse())
                         */
                        Toast.makeText(MainActivity.this, "Failed to retrieve favoured events.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }


    private void serverGetUserFavouredEventsResponse(JSONObject response) {

        try {

            userPreviouslyFavouredEvents.clear(); // clear in case activity is recreated due to configuration changes
            String success = response.getString("success");

            switch (success) {

                // there are favoured events by user
                case "1":

                    JSONArray favouredEventsJA = response.getJSONArray("message");
                    for (int i = 0; i < favouredEventsJA.length(); i++) {

                        JSONObject favouredEventJO = (JSONObject) favouredEventsJA.get(i);
                        String event_id = favouredEventJO.getString("EVENT_ID");
                        userPreviouslyFavouredEvents.add(event_id);
                    }

                    // after receiving the favoured events then perform necessary check for scheduling notifications
                    processFavouredEvents(userPreviouslyFavouredEvents);

                    break;

                case "0":
                    // no toast message shown.
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // called in ScheduleListFragment for the recycler view
    public List<Schedule> getScheduleList() {

        if (eventsSchedule.size() == 0) {
            return null;
        }

        return eventsSchedule;
    }


    @Override
    public void onProfileUpdateSuccessfulListener(boolean value) {

        if (value) {
            user = UserPreferences.getInstance().getUserDetails(this);
            if ( !user.getProfilePicture().equals("no profilePicture set") ) {
                Uri uri = Uri.parse( user.getProfilePicture() );
                Glide.with( this ).load(uri).into(mImageView);
            }
            else {
                Glide.with( this).load(R.drawable.ic_account_circle).into(mImageView);
            }

            mFullNameTV.setText( user.getFullName() );
            mEmailTV.setText( user.getEmail() );
        }

    }

    //================================Testing code==================================================

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public void processFavouredEventsTest(ArrayList<String>events){
        //String id, String title, String link, String deadline, String timezone, String date, String place
        eventsSchedule.add(new Schedule("wmt20", "WMT", "https://wits.ac.za","2020-08-15 23:59:00","UTC-12", "November 19-20, 202", "Wits"));
        processFavouredEvents(events);
    }
}
