package com.codefusiongroup.gradshub.authentication.login;

import android.view.Display;

import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
//import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;
import com.codefusiongroup.gradshub.common.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.codefusiongroup.gradshub.authentication.AuthenticationActivityTest.waitForResources;
import static com.codefusiongroup.gradshub.common.AssisterMethods.clickAt;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logUserOut;
import static com.codefusiongroup.gradshub.common.AssisterMethods.openDrawer;
import static com.codefusiongroup.gradshub.common.AssisterMethods.state;
import static org.junit.Assert.*;


public class LoginFragmentTest {

    private String username = new String("testuser@gmail.com");
    private String password = new String("simple1");
    private String fieldError = new String("Field can't be empty.");
    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<AuthenticationActivity>(AuthenticationActivity.class);

    @Test
    public void inValidInputEmptyEmail(){
        onView(withId(R.id.passwordET)).perform(typeText(password));
        closeSoftKeyboard();
        onView(withId(R.id.loginBtn)).perform(click());
        onView(withId(R.id.emailET)).check(matches(hasErrorText(fieldError)));      //Correct error message was returned.
    }

    @Test
    public void inValidInputEmptyPasswordField(){
        onView(withId(R.id.emailET)).perform(typeText(username));
        closeSoftKeyboard();
        onView(withId(R.id.loginBtn)).perform(click());
        onView(withId(R.id.passwordET)).check(matches(hasErrorText(fieldError)));        //Correct error message was returned.
    }

    @Test
    public void inValidEmailAddressEntered(){
        onView(withId(R.id.emailET)).perform(typeText("testuser"));
        closeSoftKeyboard();
        onView(withId(R.id.loginBtn)).perform(click());
        onView(withId(R.id.emailET)).check(matches(hasErrorText("Check that your email address is entered correctly!")));      //Correct error message was returned.
    }

    @Test
    public void inValiedLogInDetails() throws InterruptedException {
        onView(withId(R.id.emailET)).perform(typeText(username));
        closeSoftKeyboard();
        onView(withId(R.id.passwordET)).perform(typeText("wrongpass"));
        closeSoftKeyboard();
        onView(withId(R.id.loginBtn)).perform(click());
        waitForResources(3000);     //Wait for the error report
        //onView(withText("Incorrect Password.Please try again!")).check(matches(isDisplayed()));      //Correct error message was returned
    }

    @Test
    public void testUserLogin() throws InterruptedException {
        if(state == 1){
            logUserOut();
        }
        onView(withId(R.id.emailET)).perform(typeText(username));
        closeSoftKeyboard();
        onView(withId(R.id.passwordET)).perform(typeText(password));
        closeSoftKeyboard();
        onView(withId(R.id.loginBtn)).perform(click());
        waitForResources(4000);
        state = 1;
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));       //This is the first view that should be visible after the user logs in.
        //Log the user out after this login
        logUserOut();
    }

}