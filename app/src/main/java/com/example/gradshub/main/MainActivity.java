package com.example.gradshub.main;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gradshub.R;
import com.example.gradshub.authentication.AuthenticationActivity;
import com.example.gradshub.main.availablegroups.AvailableGroupsListFragment;
import com.example.gradshub.main.eventsSchedule.ScheduleListFragment;
import com.example.gradshub.main.mygroups.MyGroupsListFragment;
import com.example.gradshub.main.mygroups.MyGroupsProfileFragment;
import com.example.gradshub.model.Post;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.model.Schedule;
import com.example.gradshub.model.User;
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

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements MyGroupsListFragment.OnMyGroupsListFragmentInteractionListener,
        AvailableGroupsListFragment.OnAvailableGroupsListFragmentInteractionListener,
        MyGroupsProfileFragment.OnPostsListFragmentInteractionListener,
        ScheduleListFragment.OnScheduleListFragmentInteractionListener {


    public User user; // used in other fragments, so has public access.
    private AppBarConfiguration mAppBarConfiguration;
    private static final String CHANNEL_ID = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("USER");

        /*
         NOTIFICATION HANDLING:
         It seems the time frame used when the notification is sent to the user's device is the the time that it was
         created when the user was using the app cause each time a notification instance is created when user uses the
         app.

         before we call scheduleNotification we want to get the list of favourite events and send a notification
         to those users for the first event in the list (list must be sorted according to the nearest event to
         the furthest event) We check whether the current user id and event id pair is in that list and according to
         that condition decide whether to send a notification to that user in due course.

         NB: maybe we can only retrieve the user ids based on the event id of the event that's nearest.
         the database must store the favoured events sorted in date from most nearest to furthest then only retrieve
         user ids for nearest event. The event must be removed after past due date.
        */

        //scheduleNotification(this.getApplicationContext(), 0);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.feedListFragment, R.id.profileFragment,R.id.myGroupsListFragment, R.id.createGroupFragment,
                R.id.availableGroupsListFragment, R.id.scheduleFragment).setDrawerLayout(drawer).build();

        // NavController is responsible for replacing the contents of the NavHost with the new destination.
        // (layout content_main contains the navigation host fragment for MainActivity)
        NavController navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
        // By calling this method, the title in the action bar will automatically be updated when the destination changes.
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        // updates the UI with the contents of the current destination.
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        TextView fullNameTV = headerView.findViewById(R.id.userFullNameTV);
        TextView emailTV = headerView.findViewById(R.id.userEmailTV);
        String userFullName = user.getFirstName() + " " + user.getLastName();
        fullNameTV.setText(userFullName);
        emailTV.setText(user.getEmail());

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

        switch (item.getItemId()) {
            case R.id.action_logout:

                Intent intent = new Intent(this, AuthenticationActivity.class);
                startActivity(intent);
                finish(); // finish MainActivity
                return true;

            case R.id.action_settings:
                // nothing implemented yet
            default:
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
    public void onAvailableGroupsListFragmentInteraction(ResearchGroup item) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("group_item", item);
        NavController navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
        navController.navigate(R.id.action_availableGroupsListFragment_to_availableGroupProfileFragment, bundle);

    }


    @Override
    public void onPostsListFragmentInteraction(Post item) {
        // not implemented
    }


    @Override
    public void onScheduleListFragmentInteraction(Schedule item) {

        if ( !item.getLink().startsWith("https://") && !item.getLink().startsWith("http://")) {
            String link = "http://" + item.getLink();
            Uri uri = Uri.parse(link);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(Intent.createChooser(intent, "dialogTitle"));
        }
        else {
            Uri uri = Uri.parse(item.getLink());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(Intent.createChooser(intent, "dialogTitle"));
        }

        Toast.makeText(this, "selected: "+ item.getTitle(), Toast.LENGTH_SHORT).show();
    }


    // N.B: notificationId allows you to update the notification later on.
    // notificationId is unique int for each notification that you must define
    public void scheduleNotification(Context context, int notificationId) {

        //==================================================================
        /*
        Set the notification's tap action:
        Every notification should respond to a tap, usually to open an activity in your app
        that corresponds to the notification. To do so, you must specify a content intent
        defined with a PendingIntent object and pass it to setContentIntent().
        PendingIntent wraps the Intent that you want to launch when the user taps on the
        notification.

        NOTE: In our case there's no activity we want to launch when the user taps the notification. Ideally we would
        want to launch MainActivity but MainActivity requires initialisation of a User object obtained from the
        AuthenticationActivity. We can maybe launch the AuthenticationActivity if the user is not currently logged in
        on the app but what happens when they are currently using the app and launching AuthenticationActivity when
        they tap the notification might mean refering them to the login screen which is not what we want if they're
        already logged in. So for these reasons we won't launch any activity.

        If we were to launch one say MainActivity then we do the following:
        Create an explicit intent for an Activity in your app
        //Intent mainActivityIntent = new Intent(context, MainActivity.class);

        When creating your Intent, you need to take into account the back state, i.e., what happens after your
        Activity launches and the user presses the back button. In this case it sets the Activity to start in a
        new, empty task.
        //mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Wrap the Intent in a PendingIntent object
        NOTE: 2nd argument is the requestCode which is used for later cancelling this PendingIntent and
        4th argument can be PendingIntent.FLAG_UPDATE_CURRENT which means that if this intent already exits
        and we create a new one, it will get updated with the new values. Can also pass 0 for the 4th argument
        //PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, 0);
        */
        //===================================================================

        // set notification properties of the notification received by the user
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                // Notification Channel Id is ignored for Android pre O (26).
                .setSmallIcon(R.mipmap.applogo) // shows app icon next to the notification
                .setContentTitle("Event: Robotics")
                .setContentText("Deadline: 20 Aug 2020")

                // On Android 7.1 (API level 25) and below, importance of each notification is determined
                // by the notification's priority.
                // Sets priority for 25 and below. For 26 and above, 'priority' is deprecated for
                // 'importance' which is set in the NotificationChannel. The integers representing
                // 'priority' are different from 'importance', so make sure you don't mix them.
                .setPriority(NotificationCompat.PRIORITY_HIGH)

                // However, you are not required to set a system-wide category and should only
                // do so if your notifications match one of the categories defined by in NotificationCompat.
                //.setCategory(NotificationCompat.CATEGORY_EVENT)

                // Set the intent that will fire when the user taps the notification
                //.setContentIntent(mainPendingIntent)

                // Notice this code calls setAutoCancel(), which automatically removes the notification when
                // the user taps it.
                // when notification is clicked it will disappear and open our activity if intent for activity
                // is set.
                .setAutoCancel(true);

        /*
        Create a channel and set the importance:
        Before you can deliver the notification on Android 8.0 and higher, you must register
        your app's notification channel with the system by passing an instance of
        NotificationChannel to createNotificationChannel(). So the following code is
        blocked by a condition on the SDK_INT version:

        Because you must create the notification channel before posting any notifications
        on Android 8.0 and higher, you should execute this code as soon as your app starts.
        It's safe to call this repeatedly because creating an existing notification channel
        performs no operation.
        */
        createNotificationChannel();

        // builder.build() returns the notification to be published
        Notification notification = builder.build();

        // this part schedules the notification and calls EventNotificationPublisher to publish the
        // notification
        Intent notificationIntent = new Intent(context, EventNotificationPublisher.class);
        notificationIntent.putExtra(EventNotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(EventNotificationPublisher.NOTIFICATION, notification);
        // 4th argument can be PendingIntent.FLAG_UPDATE_CURRENT or 0
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);

        // set when will the notification appear
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2020);
        calendar.set(Calendar.MONTH, 7);// months count start from 0
        calendar.set(Calendar.DAY_OF_MONTH, 18);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        //calendar.set(Calendar.AM_PM, Calendar.AM);
        //calendar.add(Calendar.DATE, 2); // sends notification in 2 days from today at the given time above (08hrs:00min:00sec)

        // ALWAYS recompute the calendar after using add, set, roll
        Date date = calendar.getTime();

        // for testing if notification works use this with the appropriate alarmManager below
        //int delay = 15000; // delay of 15s
        //long futureInMillis = SystemClock.elapsedRealtime() + delay;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
        //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);

    }


    private void createNotificationChannel() {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "gradsHub_notification_channel";
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
