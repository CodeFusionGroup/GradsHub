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
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;
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
import com.codefusiongroup.gradshub.utils.EventNotificationPublisher;
import com.codefusiongroup.gradshub.utils.MonthsConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;

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
import java.util.Scanner;


public class MainActivity extends AppCompatActivity implements MyGroupsListFragment.OnMyGroupsListFragmentInteractionListener,
        ExploreGroupsFragment.OnExploreGroupsFragmentInteractionListener,
        MyGroupsProfileFragment.OnPostsListFragmentInteractionListener,
        GroupPostCommentsFragment.OnCommentsListFragmentInteractionListener,
        ScheduleListFragment.OnScheduleListFragmentInteractionListener,
        OpenChatsFragment.OnOpenChatsFragmentInteractionListener,
        UsersListFragment.OnUsersListFragmentInteractionListener {


    public User user; // used in other fragments, so has public access.
    private AppBarConfiguration mAppBarConfiguration;

    private Calendar rightNow;
    private static final String CHANNEL_ID = "0";
    private List<Schedule> eventsSchedule = new ArrayList<>();

    // used to form notifications for the current user's favourite events
    private ArrayList<String> userPreviouslyFavouredEvents = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("USER");

        readScheduleFromFile();

        getUserFavouredEvents();

//        performEventDateChecksForNotification();

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


        View headerView = navigationView.getHeaderView(0);
        TextView fullNameTV = headerView.findViewById(R.id.userFullNameTV);
        TextView emailTV = headerView.findViewById(R.id.userEmailTV);
        String userFullName = user.getFirstName() + " " + user.getLastName();
        fullNameTV.setText(userFullName);
        emailTV.setText(user.getEmail());


    }


    private void performEventDateChecksForNotification() {

        // NOTIFICATION HANDLING
        rightNow = Calendar.getInstance();
        int currentYear = rightNow.get(Calendar.YEAR);
        int currentMonth = rightNow.get(Calendar.MONTH);
        int currentDay = rightNow.get(Calendar.DAY_OF_MONTH);

        //===============================================================
        // IMPORTANT READ FOR TESTING NOTIFICATION YOURSELF.
        // You can test the notification with one event id added to userPreviouslyFavouredEvents. However this
        // approach might mean waiting for days or months :( since the corresponding event with that id in Schedule-data.txt
        // might have a date that's far in the future. So instead use the approach BELOW where a dummy event is
        // created (NOTE: certain sections of the code below are commented out for this approach to work).
        // ADJUST the DATE and/or MONTH in the event.setDate() method BELOW so that when you subtract 7 days from it,
        // (for the DATE part) it brings you to the present day. The notification is set to appear approximately
        // 5 minutes after the current time for testing purposes only.
        //===============================================================

        // if the user has any favourite event(s)
//        if ( userPreviouslyFavouredEvents.size() > 0 ) {
//
//            for (String event_id : userPreviouslyFavouredEvents) {
//                // check for the event id in schedules
//                for (Schedule event : schedules) {
//
//                    if (event.getId().equals(event_id)) {

                //***************************************
                // TESTING NOTIFICATION with dummy event
                Schedule event = new Schedule();
                event.setTitle("Robotics");
                event.setDate("date: September 22, 2020"); // adjust month/date appropriately & leave formatting as it is.
                event.setPlace("place: Vancouver, Canada");
                //***************************************

                String dateStr = event.getDate();
                String eventDate = dateStr.substring( dateStr.indexOf(":") + 2 );
                String[] dateComponents = eventDate.split(" ");

                int eventYear = Integer.parseInt(dateComponents[2]);
                int eventMonth = MonthsConstants.setMonths(dateComponents[0]);
                String eventDuration = dateComponents[1];

                int eventStartDay;
                if (eventDuration.length() > 2) {
                    eventStartDay = Integer.parseInt( eventDuration.substring(0, 2) );
                } else {
                    eventStartDay = Integer.parseInt( eventDuration.substring(0, 1) );
                }

                // build notifications only for valid dates of events
                if ( eventYear >= currentYear) {
                    if( eventMonth > currentMonth || eventMonth == currentMonth && eventStartDay > currentDay ) {
                        int notificationId = 0;
                        scheduleNotification(this.getApplicationContext(), notificationId, event, eventYear, eventMonth, eventStartDay);
                    }
                }

//                    }
//
//                }
//
//            }
//
//        }


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

            case R.id.action_refresh:


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
    public void onMyGroupsListFragmentInteraction(ResearchGroup item) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("group_item", item);
        bundle.putParcelable("user", user);
        NavController navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
        navController.navigate(R.id.action_myGroupsFragment_to_myGroupProfileFragment, bundle);

    }


    @Override
    public void onExploreGroupsFragmentInteraction(ResearchGroup item) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("group_item", item);
        NavController navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
        navController.navigate(R.id.action_exploreGroupsFragment_to_exploredGroupProfileFragment, bundle);

    }


    @Override
    public void onPostsListFragmentInteraction(Post item) {
        // not implemented
    }


    @Override
    public void onCommentsListFragmentInteraction(Comment comment) {}


    @Override
    public void onScheduleListFragmentInteraction(Schedule item) {

        if (!item.getLink().startsWith("https://") && !item.getLink().startsWith("http://")) {
            String link = "http://" + item.getLink();
            Uri uri = Uri.parse(link);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(Intent.createChooser(intent, "dialogTitle"));
        } else {
            Uri uri = Uri.parse(item.getLink());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(Intent.createChooser(intent, "dialogTitle"));
        }

        Toast.makeText(this, "selected: " + item.getTitle(), Toast.LENGTH_SHORT).show();
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


    // this method gets schedule from website and writes the schedules to a file (Schedule-data.txt)
    // NOTE: method is not called anywhere to avoid making multiple request to the website for data
    // still have to schedule the requests since data on the website can change
    private void requestSchedule() {

        try {

            Document document = Jsoup.connect("https://github.com/abhshkdz/ai-deadlines/blob/gh-pages/_data/conferences.yml").get();
            Elements scheduleData = document.getElementsByTag("tr");

            // Create a new file
            File file = new File("Schedule-data.txt");

            // File writer for the new file
            FileWriter fileWriter = new FileWriter("Schedule-data.txt");
            int count = 0;
            for (Element data : scheduleData) {
                count++;
                if (count < 3) {
                    continue;
                }
                fileWriter.write(data.text());
                //fileWriter.write(System.lineSeparator()); // lineSeparator() giving error when uncommented
            }
            fileWriter.close();

            // Parse the file
            FileReader fileReader = new FileReader("Schedule-data.txt");
            Scanner scanner = new Scanner(fileReader);

            while (scanner.hasNext()) {
                String scanLine = scanner.nextLine();
                if (scanLine.length() == 0) {
                    continue;
                }

                //System.out.println(scanLine);
            }
            scanner.close();


        } catch (IOException e) {
            System.out.println("Error connecting to url");
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
            for (String s : temp) {

                String title = null, id = null, link = null, deadline = null, timezone = null, date = null, place = null;
                String[] scheduleItem = s.split("\n");

                for (String value : scheduleItem) {

                    // select only relevant parts of the schedule item
                    String substring = value.substring(value.indexOf(":") + 2); // for title, id and link
                    if (value.startsWith("- title")) {
                        title = substring;
                    } else if (value.startsWith("id")) {
                        id = substring;
                    } else if (value.startsWith("link")) {
                        link = substring;
                    } else if (value.startsWith("deadline")) {
                        deadline = value;
                    } else if (value.startsWith("timezone")) {
                        timezone = value;
                    } else if (value.startsWith("date")) {
                        date = value;
                    } else if (value.startsWith("place")) {
                        place = value;
                    }
                }

                eventsSchedule.add(new Schedule(id, title, link, deadline, timezone, date, place));

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
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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

                    break;

                /*
                NOTE: Since these network calls happen asynchronously and there are multiple methods in this fragment
                that make network requests, it is not always clear as to which method will finish its network request
                first thus it becomes difficult as to which Toast message will be shown to the user especially in the
                case of the DB not having any records of the requested data since then these methods display different
                Toast messages in that instance which can be confusing to the user. It is therefore important to consider
                which toast messages are important to show to the user when a network request has been made.
                So for methods like this one there's no need to display toast msg for network calls that get data
                but don't necessarily have to inform the user of the outcome of that response from server.
                */
                case "0":
                    // no toast message shown.
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // N.B: notificationId allows you to update the notification later on.
    // notificationId is unique int for each notification that you must define
    public void scheduleNotification(Context context, int notificationId, Schedule event, int eventYear, int eventMonth, int eventStartDay) {

        Intent authActivityIntent = new Intent(context, AuthenticationActivity.class);
        authActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent authPendingIntent = PendingIntent.getActivity(context, 0, authActivityIntent, 0);

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
                .setContentIntent(authPendingIntent);

        // builder.build() returns the notification to be published
        Notification notification = builder.build();


        // this part schedules the notification and calls EventNotificationPublisher to publish the notification
        Intent notificationIntent = new Intent(context, EventNotificationPublisher.class);
        notificationIntent.putExtra(EventNotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(EventNotificationPublisher.NOTIFICATION, notification);
        // 4th argument can be PendingIntent.FLAG_UPDATE_CURRENT or 0
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);


        // set notification parameters based on the constraints met
        Calendar notificationCalendar = Calendar.getInstance();
        notificationCalendar.set(Calendar.YEAR, eventYear);


        // NOTE: it has to be eventMonth - currentMonth since current month has a smaller int value.
        // should be strictly greater than 1 since notification is sent a month before the event month
        int currentMonth = rightNow.get(Calendar.MONTH);
        if (eventMonth - currentMonth > 1) {
            notificationCalendar.set(Calendar.MONTH, eventMonth - 1);
            notificationCalendar.set(Calendar.DAY_OF_MONTH, 1); // first day of that month
        }

        // events that start from the 8th onwards of the event month
        else if (currentMonth == eventMonth && eventStartDay >= 8) {
            notificationCalendar.set(Calendar.DAY_OF_MONTH, eventStartDay - 7); // first day of event month
        }

        // events that start on the first week of the event month but currently its the month just before the
        // event month. No need to worry about the case: if its already the event month but the eventStartDay <= 7
        else if (currentMonth + 1 == eventMonth && eventStartDay <= 7) {

            switch (eventStartDay) {
                // not considering how many days the current month has makes things simple and might not be that important
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

        }


        // set the time when the notification should be sent
        //***** VALUES ARE SET FOR TESTING NOTIFICATION ******
        // TODO: set time correctly
        notificationCalendar.set(Calendar.HOUR_OF_DAY, rightNow.get(Calendar.HOUR_OF_DAY)); // current hour,for testing
        notificationCalendar.set(Calendar.MINUTE, rightNow.get(Calendar.MINUTE) + 5); // current minute + 5 minute delay, for testing
        notificationCalendar.set(Calendar.SECOND, 0); // not important, so set to 0
        //************************************************************

        // ALWAYS recompute the calendar after using add, set, roll
        Date date = notificationCalendar.getTime();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);

    }


    // called in ScheduleListFragment for the recycler view
    public List<Schedule> getScheduleList() {

        if (eventsSchedule.size() == 0) {
            return null;
        }

        return eventsSchedule;
    }


}
