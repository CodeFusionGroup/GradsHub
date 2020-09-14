package com.codefusiongroup.gradshub.authentication;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.codefusiongroup.gradshub.R;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logUserOut;
import static org.junit.Assert.*;
//@RunWith(AndroidJUnit4.class)

public class AuthenticationActivityTest {
    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<AuthenticationActivity>(AuthenticationActivity.class);

    @Test
    public void fragmentContainerLaunches(){
        //check views are visible

        onView(withId(R.id.Fragment_container)).check(matches(isDisplayed()));
    }

    @Test
    public void allViewsAreVisible(){
        onView(withId(R.id.emailET)).check(matches(isDisplayed()));     //Email edit field is visible
        onView(withId(R.id.passwordET)).check(matches(isDisplayed()));  //Password edit field is visible
        onView(withId(R.id.forgotPasswordBtn)).check(matches(isDisplayed()));   //Forgot password button is visible
        onView(withId(R.id.loginBtn)).check(matches(isDisplayed()));        //login button is visible
        onView(withId(R.id.registerBtn)).check(matches(isDisplayed()));     //Register button is visible
    }

    @Test
    public void canNavigateToRegisterScreen(){
        onView(withId(R.id.registerBtn)).perform(click());  //Click register button
        onView(withId(R.id.registerLaunched)).check(matches(isDisplayed()));    //Ater success, check if register screen is visible
    }

    //ToDo: Add the navigation to forgot password screen

    //Time to wait while the app loads
    public static void waitForResources(long n) throws InterruptedException {
        Thread.sleep(n);
    }

}
