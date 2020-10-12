package com.codefusiongroup.gradshub.common;


import android.content.Intent;
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
import com.bumptech.glide.Glide;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;
import com.codefusiongroup.gradshub.friends.FriendsFragment;
import com.codefusiongroup.gradshub.groups.searchGroups.exploreGroupsList.ExploreGroupsFragment;
import com.codefusiongroup.gradshub.messaging.openChats.OpenChatsFragment;
import com.codefusiongroup.gradshub.events.ScheduleListFragment;
import com.codefusiongroup.gradshub.groups.userGroups.userGroupsList.MyGroupsListFragment;
import com.codefusiongroup.gradshub.messaging.searchableUsers.UsersListFragment;
import com.codefusiongroup.gradshub.common.models.Chat;
import com.codefusiongroup.gradshub.common.models.ResearchGroup;
import com.codefusiongroup.gradshub.common.models.Schedule;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.profile.ProfileFragment;
import com.codefusiongroup.gradshub.utils.api.Resource;
import com.codefusiongroup.gradshub.utils.notifications.EventNotificationComposer;
import com.codefusiongroup.gradshub.utils.notifications.MonthsConstants;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.MenuCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import java.util.List;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity implements MyGroupsListFragment.OnMyGroupsListFragmentInteractionListener,
        ExploreGroupsFragment.OnExploreGroupsFragmentInteractionListener,
        ScheduleListFragment.OnScheduleListFragmentInteractionListener,
        OpenChatsFragment.OnOpenChatsFragmentInteractionListener,
        UsersListFragment.OnUsersListFragmentInteractionListener,
        FriendsFragment.OnFriendsListFragmentInteractionListener,
        ProfileFragment.OnProfileUpdateSuccessfulListener {


    private static final String TAG = "MainActivity";

    private TextView emailTV;
    private TextView fullNameTV;
    private ImageView imageView;
    private AppBarConfiguration appBarConfiguration;

    public User user; // used in other fragments, so has public access.
    private UserPreferences userPreferences;
    private int currentYear, currentMonth, currentDay;

    private List<Schedule> eventsSchedule = new ArrayList<>();
    // used to form notifications for the current user's favourite events
    private List<String> userFavouredEvents = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                user = bundle.getParcelable("user");
            }
        }

        //====================== SETTING UP MAIN ACTIVITY LAYOUT RESOURCES =========================
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        // NavController is responsible for replacing the contents of the NavHost with the new destination.
        // (layout content_main contains the navigation host fragment for MainActivity)
        final NavController navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);

        // setting up app bar with navigation graph destinations
        appBarConfiguration = new AppBarConfiguration.Builder( navController.getGraph() ).setOpenableLayout(drawer).build();

        // By calling this method, the title in the action bar will automatically be updated when the destination changes.
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // updates the UI with the contents of the current destination.
        NavigationUI.setupWithNavController(navigationView, navController);


        //===== INITIALISING USER, CALENDAR DATE FOR EVENTS NOTIFICATIONS AND EVENTS SCHEDULE ======
        userPreferences = UserPreferences.getInstance();
        user = userPreferences.getUserState(this);

        Calendar rightNow = Calendar.getInstance();
        currentYear = rightNow.get(Calendar.YEAR);
        currentMonth = rightNow.get(Calendar.MONTH);
        currentDay = rightNow.get(Calendar.DAY_OF_MONTH);

        // called after initialising calendar date to be used to filter schedule based on events dates
        readScheduleFromFile();


        //====================== INITIALISING SIDE BAR MENU WITH USER PROFILE ======================
        View headerView = navigationView.getHeaderView(0);
        imageView = headerView.findViewById(R.id.img_container);

        if ( !user.getProfilePicture().equals("no profilePicture set") ) {
            Uri uri = Uri.parse( user.getProfilePicture() );
            Glide.with( this ).load(uri).into(imageView);
        }
        else {
            Glide.with( this).load(R.drawable.ic_account_circle).into(imageView);
        }

        fullNameTV = headerView.findViewById(R.id.userFullNameTV);
        fullNameTV.setText( user.getFullName() );
        emailTV = headerView.findViewById(R.id.userEmailTV);
        emailTV.setText( user.getEmail() );


        //========== PASSING USER OBJECT TO START DESTINATION (FEED FRAGMENT) ======================
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        navController.setGraph(R.navigation.main_navigation, bundle);


        //====== GET MAIN ACTIVITY VIEW MODEL AND OBSERVER LIVE DATA FOR USER FAVOURED EVENTS=======
        MainActivityViewModel viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        viewModel.fetchFavouriteEvents( user.getUserID() );
        observeViewModel(viewModel);

    }

    //private boolean favouredEventsUpdated = false;
//    private boolean favouredEventsNetworkRequestFailed = false;
//
//    public void setFavouredEventsUpdated(boolean value) {
//        favouredEventsUpdated = value;
//    }
//
//    public boolean favouredEventsUpdated() {
//        return favouredEventsUpdated;
//    }
//
//    public boolean favouredEventsNetworkRequestFailed() {
//        return favouredEventsNetworkRequestFailed;
//    }

    private void observeViewModel(MainActivityViewModel viewModel) {

        viewModel.getFavouredEventsResponse().observe(this, listResource -> {
            if (listResource != null) {

                if (listResource.status == Resource.Status.API_DATA_SUCCESS) {
                    if (listResource.data != null) {
                        //Log.d(TAG, "favoured events network request passed");
                        userFavouredEvents.clear();
                        userFavouredEvents.addAll(listResource.data);
                        EventNotificationComposer.getInstance().processFavouredEvents(userFavouredEvents, eventsSchedule, currentDay, currentMonth);
                        //Log.d(TAG, "userFavouredEvents size: " + userFavouredEvents.size());
                    } else {
                        Log.d(TAG, "listResource.data is null");
                    }
                }
//                else if(listResource.status == Resource.Status.ERROR) {
//                    Log.d(TAG, "favoured events network request failed");
//                    favouredEventsNetworkRequestFailed = true;
//                }

            }
        });
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

    @Override
    public void onProfileUpdateSuccessfulListener(boolean value) {
        if (value) {
            // get updated saved details from preferences
            user = UserPreferences.getInstance().getUserState(this);
            if ( !user.getProfilePicture().equals("no profilePicture set") ) {
                Uri uri = Uri.parse( user.getProfilePicture() );
                Glide.with( this ).load(uri).into(imageView);
            }
            else {
                Glide.with( this).load(R.drawable.ic_account_circle).into(imageView);
            }
            fullNameTV.setText( user.getFullName() );
            emailTV.setText( user.getEmail() );
        }
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

        if (item.getItemId() == R.id.action_logout) {
            userPreferences.setLogOutState(MainActivity.this);
            Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            finish(); // finish MainActivity
            return true;
        }

        // default to any selected item on the app bar if no case is found above
        return super.onOptionsItemSelected(item);
    }


    // this method is called whenever the user chooses to navigate up (back button) within your application's activity hierarchy from
    // the action bar
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
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
                            break;
                        }
                        else {

                            if (eventYear > currentYear) {
                                date = component;
                            }
                            else {

                                if (eventStartMonth > currentMonth) {
                                    date = component;
                                }
                                else if (eventStartMonth == currentMonth && eventStartDay > currentDay) {
                                    date = component;
                                }
                                else {
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

    //====== METHODS CALLED IN SCHEDULE FRAGMENT =======
    public List<Schedule> getScheduleList() {
        if (eventsSchedule.size() == 0) {
            return null;
        }
        return eventsSchedule;
    }

    public List<String> getFavouredEvents() {
        if (userFavouredEvents.size() == 0) {
            return null;
        }
        return userFavouredEvents;
    }
    //=================================================

    // this method gets schedule from website and writes the schedules to a file (Schedule-data.txt)
    // NOTE: method is not called anywhere to avoid making multiple request to the website for data
    // still have to schedule the requests since data on the website can change
    private void requestScheduleFromSource() {

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

}
