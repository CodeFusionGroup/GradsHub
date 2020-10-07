package com.codefusiongroup.gradshub.profile;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
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

public class ProfileFragmentTest {
    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<AuthenticationActivity>(AuthenticationActivity.class);

    @Before
    public void setUp() throws Exception {
        logInUser();
        openDrawer();
        onView(withText("Profile")).perform(click());
        onView(withId(R.id.editProfileBtn)).perform(click());
    }

    @After
    public void tearDown() throws Exception {
        logUserOut();
    }

    @Test
    public void testViewProfile() throws InterruptedException {
        onView(withId(R.id.firstNameET)).perform(clearText(), typeText("Test"), closeSoftKeyboard());
        onView(withId(R.id.lastNameET)).perform(clearText(), typeText("User"), closeSoftKeyboard());
        onView(withId(R.id.emailET)).perform(clearText(), typeText("testuser@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.phoneNumberET)).perform(clearText(), typeText("01234567"), closeSoftKeyboard());
        onView(withId(R.id.updateBtn)).perform(click());
        onView(withId(R.id.phoneNumberET)).perform(typeText("89"));
        onView(withId(R.id.updateBtn)).perform(click());
    }

}