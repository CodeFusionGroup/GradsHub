package com.example.gradshub.main.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gradshub.R;

import com.example.gradshub.main.ui.mygroups.MyGroupsFragment;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity implements MyGroupsFragment.OnListFragmentInteractionListener {

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

        //===========================================================================================
        // NOTE: will be moved later so that it appears only in specific destinations where its relevant.
        // a floating action button is a circular button that triggers the primary action in your app's UI.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Temporary code
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //==========================================================================================

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.profileFragment,R.id.myGroupsFragment, R.id.createGroupFragment).setDrawerLayout(drawer).build();


        // NavController is responsible for replacing the contents of the NavHost with the new destination.
        // (layout content_main contains the navigation host fragment for MainActivity)
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
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
        return true;
    }

    // this method is called whenever the user chooses to navigate up (back button) within your application's activity hierarchy from
    // the action bar
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    // added so that main activity can be notified of the selected item in the recycler view on My Groups fragment and potentially
    // notify other fragments that also need this information.
    @Override
    public void onListFragmentInteraction(ResearchGroup item) {
        // does nothing for now, only displays a toast of the selected item.
        Toast.makeText(this, "selected "+item.getGroupName(), Toast.LENGTH_SHORT).show();
    }

}
