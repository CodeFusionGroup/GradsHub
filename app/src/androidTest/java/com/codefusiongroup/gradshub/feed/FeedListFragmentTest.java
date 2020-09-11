package com.codefusiongroup.gradshub.feed;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.codefusiongroup.gradshub.authentication.AuthenticationActivityTest.waitForResources;
import static org.junit.Assert.*;

public class FeedListFragmentTest {

    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<>(AuthenticationActivity.class);

    //User must login before accessing the feed
    @Before
    public void logUserIn() throws InterruptedException {
        onView(withId(R.id.emailET)).perform(typeText("testuser@gmail.com"));
        closeSoftKeyboard();
        onView(withId(R.id.passwordET)).perform(typeText("simple1"));
        closeSoftKeyboard();
        onView(withId(R.id.loginBtn)).perform(click());
        waitForResources(6000);
    }

    @Ignore("Not yet implemented")
    @Test
    public void FeedListLaunches(){

    }

}