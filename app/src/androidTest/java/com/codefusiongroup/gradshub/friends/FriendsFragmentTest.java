package com.codefusiongroup.gradshub.friends;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.uiautomator.UiObjectNotFoundException;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;

import org.junit.After;
import org.junit.Before;
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
import static org.junit.Assert.*;

public class FriendsFragmentTest {
    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<AuthenticationActivity>(AuthenticationActivity.class);

    @Before
    public void setUp() throws UiObjectNotFoundException, InterruptedException {
        logInUser();
        openDrawer();
        onView(withText("Friends")).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        logUserOut();
    }

    @Test
    public void startChatTest() throws InterruptedException {
        onView(withId(R.id.list)).check(matches(isDisplayed()));
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));//Start event
        onView(withText("START CHAT")).perform(click());
        onView(withId(R.id.chatMessagesList)).check(matches(isDisplayed()));        //Checks if the message list is visible
        onView(withId(R.id.typeMessageET)).perform(typeText("Hello World! This is a test!"), closeSoftKeyboard());
        onView(withId(R.id.sendMessageBtn)).perform(click());

    }
}