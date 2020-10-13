package com.codefusiongroup.gradshub.messaging.openChats;

import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeRight;
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

public class OpenChatsFragmentTest {
    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<AuthenticationActivity>(AuthenticationActivity.class);

    @Before
    public void setUp() throws Exception {
        logInUser();
        openDrawer();
        onView(withText("Direct Messages")).perform(click());
        waitForResources(1500);
    }

    @After
    public void tearDown() throws Exception {
        logUserOut();
    }

    @Test
    public void testViewDirectMessaged(){
        onView(withId(R.id.chats)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoadChatHistory() throws InterruptedException {
        onView(withId(R.id.chatsList)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        waitForResources(1500);
        onView(withId(R.id.chatMessagesList)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoadReceivedText() throws InterruptedException {
        onView(withId(R.id.chatsList)).perform(RecyclerViewActions.actionOnItemAtPosition(2,click()));
        waitForResources(1500);
    }

    @Test
    public void testClearChat() throws InterruptedException {
        openDrawer();
        onView(withText("Search Users")).perform(click());
        waitForResources(1000);
        onView(withId(R.id.usersList)).perform(RecyclerViewActions.actionOnItemAtPosition(4,click()));
        waitForResources(1000);
        //Initiate chat
        onView(withText("START CHAT")).perform(click());
        onView(withId(R.id.chatMessagesList)).check(matches(isDisplayed()));        //Checks if the message list is visible
        onView(withId(R.id.typeMessageET)).perform(typeText("Hello World! This is a test!"), closeSoftKeyboard());
        onView(withId(R.id.sendMessageBtn)).perform(click());

        //Remove chat
        openDrawer();
        onView(withText("Direct Messages")).perform(click());
        waitForResources(1200);

        //
        onView(withId(R.id.chatsList)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, swipeRight()));
    }

}