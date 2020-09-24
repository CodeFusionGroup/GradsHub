package com.codefusiongroup.gradshub.messaging.openChats;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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

}