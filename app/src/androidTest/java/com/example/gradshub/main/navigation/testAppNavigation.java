package com.example.gradshub.main.navigation;

import android.view.Gravity;
import android.widget.RelativeLayout;
import androidx.test.espresso.contrib.DrawerActions;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import com.example.gradshub.R;
import com.example.gradshub.authentication.LoginFragment;
import com.example.gradshub.authentication.TestingActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;

public class testAppNavigation {
    //We will be using login rule as it is the first activity required to access other activities(I.e activities we cannot test in isolation)
    @Rule
    public ActivityTestRule<TestingActivity> activityActivityTestRule = new ActivityTestRule<TestingActivity>(TestingActivity.class);
    private TestingActivity loginFragment = null;

    @Before
    public void setUp() throws Exception{
        loginFragment = activityActivityTestRule.getActivity();
    }

    @Test
    public void testUserActivities()
    {
        RelativeLayout rlContainer = (RelativeLayout) loginFragment.findViewById(R.id.containing_tests);
        assertNotNull(rlContainer);

        LoginFragment Fragment = new LoginFragment();
        loginFragment.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();

        ViewInteraction view2 = onView(withId(R.id.emailET));
        view2.perform(ViewActions.typeText("testuser@gmail.com"));
        ViewInteraction view = onView(withId(R.id.passwordET));
        view.perform(ViewActions.typeText("simple1"));
        closeSoftKeyboard();
        ViewInteraction view1 = onView(withId(R.id.loginBtn));
        view1.perform(click());
//        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
//Wait for 1 minute to  log in, else the log in fails due to slow network!!!
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        //Click on profile option
        onView(withText("Profile"))
                .perform(click());

        //Reopen drawer
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        onView(withText("My Groups"))
                .perform(click());

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());
        //Must Add a create group interaction
        onView(withText("Search Groups"))
                .perform(click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        onView(withText("Home"))
                .perform(click());


    }


    @After
    public void tearDown() throws Exception {
        loginFragment = null;
    }
}

