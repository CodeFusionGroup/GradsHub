package com.codefusiongroup.gradshub.messaging.searchableUsers;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.uiautomator.UiObjectNotFoundException;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;
import com.codefusiongroup.gradshub.common.MyViewAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.codefusiongroup.gradshub.authentication.AuthenticationActivityTest.waitForResources;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logInUser;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logUserOut;
import static com.codefusiongroup.gradshub.common.AssisterMethods.openDrawer;

public class UsersListFragmentTest {
    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<AuthenticationActivity>(AuthenticationActivity.class);

    @Before
    public void setUp() throws UiObjectNotFoundException, InterruptedException {
        logInUser();
        openDrawer();
        onView(withText("Search Users")).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        logUserOut();
    }

    @Test
    public void testSearchableUsersList() throws InterruptedException, UiObjectNotFoundException {
        onView(withId(R.id.usersList)).check(matches(isDisplayed()));
        waitForResources(1500);
        onView(withId(R.id.usersList)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withText("ADD FRIEND")).perform(click());
        waitForResources(1000);
        onView(withText("REMOVE FRIEND")).perform(click());
        onView(withText("BLOCK")).perform(click());
        waitForResources(1000);
        //unblock the blocked friend
        logUserOut();
        setUp();
        onView(withId(R.id.usersList)).check(matches(isDisplayed()));
        waitForResources(1500);
        onView(withId(R.id.usersList)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withText("UN-BLOCK")).perform(click());
        waitForResources(1000);
    }

}