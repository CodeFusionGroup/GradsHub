package com.example.gradshub.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import com.example.gradshub.R;
import com.example.gradshub.authentication.AuthenticationActivity;
import com.example.gradshub.main.availablegroups.AvailableGroupProfileFragment;
import com.example.gradshub.main.availablegroups.AvailableGroupsListFragment;
//import com.example.gradshub.main.mygroups.MyGroupsListFragment;
import com.example.gradshub.main.mygroups.MyGroupsProfileFragment;
import com.example.gradshub.model.Post;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.model.User;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity implements //MyGroupsListFragment.OnMyGroupsListFragmentInteractionListener,
        AvailableGroupsListFragment.OnAvailableGroupsListFragmentInteractionListener,
        MyGroupsProfileFragment.OnPostsListFragmentInteractionListener {


    public User user;
    private AppBarConfiguration mAppBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        user = intent.getParcelableExtra("USER");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.feedListFragment, R.id.profileFragment,R.id.myGroupsListFragment, R.id.createGroupFragment,
                R.id.availableGroupsListFragment).setDrawerLayout(drawer).build();


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
        String userFullName = user.getFirstName()+" "+user.getLastName();
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


//    @Override
//    public void onMyGroupsListFragmentInteraction(ResearchGroup item) {
//
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("group_item", item);
//        bundle.putParcelable("user", user);
//        NavController navController = Navigation.findNavController(this, R.id.main_nav_host_fragment);
//        navController.navigate(R.id.action_myGroupsFragment_to_myGroupProfileFragment, bundle);
//
//    }


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


}
