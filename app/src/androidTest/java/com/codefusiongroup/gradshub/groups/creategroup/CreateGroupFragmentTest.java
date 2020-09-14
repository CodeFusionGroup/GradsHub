package com.codefusiongroup.gradshub.groups.creategroup;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.uiautomator.UiObjectNotFoundException;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.codefusiongroup.gradshub.authentication.AuthenticationActivityTest.waitForResources;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logInUser;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logUserOut;
import static com.codefusiongroup.gradshub.common.AssisterMethods.openDrawer;
import static org.junit.Assert.*;

public class CreateGroupFragmentTest {
    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<>(AuthenticationActivity.class);

    //User must login before accessing the feed
    @Before
    public void logIn() throws InterruptedException, UiObjectNotFoundException {
        logInUser();
    }

    @After
    public void logOut(){
        logUserOut();
    }

    @Test
    public void createGroupTest() throws InterruptedException {
        openDrawer();
        onView(withText("Create Group"))
                .perform(click());


        onView(withId(R.id.groupNameET))
                .perform(typeText("The_Private_Group"));
        closeSoftKeyboard();

        onView(withId(R.id.privateRB))
                .perform(click());

        //Give enough time delay to get server response
        waitForResources(1500);
    }

    @Test
    public void testInvalidGroupEmptyName() throws InterruptedException {
        openDrawer();
        onView(withText("Create Group")).perform(click());
        //Click when there is no available input
        onView(withText("DONE"))
                .perform(click());
        onView(withId(R.id.groupNameET)).check(matches(hasErrorText("Not a valid group name!")));
    }

    @Test
    public void testInvalidGroupLongName(){
        openDrawer();
        onView(withText("Create Group"))
                .perform(click());

        onView(withId(R.id.groupNameET))
                .perform(typeText("The_Private_Group With Many Names and The_Private_Group With Many Names"));
        closeSoftKeyboard();

        onView(withId(R.id.privateRB))
                .perform(click());

        //Click when there no radio button option selected
        onView(withText("DONE"))
                .perform(click());

        onView(withId(R.id.groupNameET)).check(matches(hasErrorText("Exceeded the maximum number of characters allowed!")));
    }

    @Test
    public void testInvalidGroupType(){
        openDrawer();
        onView(withText("Create Group"))
                .perform(click());

        //Click when there is no available input
        onView(withText("DONE"))
                .perform(click());

        onView(withId(R.id.groupNameET))
                .perform(typeText("The_Private_Group"));
        closeSoftKeyboard();

        //Click when there no radio button option selected
        onView(withText("DONE"))
                .perform(click());
        //Checks if the toast is showing a correct error message
    }

    //TODO: Edit a php file to return true when a dummy group is created, then create a createGroupSuccessTest()

}