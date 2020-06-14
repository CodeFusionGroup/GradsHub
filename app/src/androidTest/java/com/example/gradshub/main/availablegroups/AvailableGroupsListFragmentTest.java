package com.example.gradshub.main.availablegroups;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.gradshub.R;
//import com.example.gradshub.authentication.LoginFragment;
//import com.example.gradshub.authentication.TestingActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class AvailableGroupsListFragmentTest {

    /*@Rule
    public FragmentTestRule<?,AvailableGroupsListFragment> fragmentTestRule = FragmentTestRule.create(AvailableGroupsListFragment.class);

    @Test
    public void AvailableGroupLaunched()
    {
        //onView(withId(R.id.listAvailable)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view)).check(matches(isDisplayed()));
    }*/

    /*@Rule
    public ActivityTestRule<GroupListActivity> activityActivityTestRule = new ActivityTestRule<GroupListActivity>(GroupListActivity.class);
    private GroupListActivity grouplistFragment = null;

    @Before
    public void setUp() throws Exception{
        grouplistFragment = activityActivityTestRule.getActivity();
    }

    @Test
    public void testingLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) grouplistFragment.findViewById(R.id.grouplist_test);
        assertNotNull(rlContainer);

        AvailableGroupsListFragment Fragment = new AvailableGroupsListFragment();
        grouplistFragment.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View fView = Fragment.getView().findViewById(R.id.listAvailable);
        assertNotNull(fView);
    }

    @After
    public void tearDown() throws Exception {
        grouplistFragment = null;

        grouplistFragment = null;

    }*/

}