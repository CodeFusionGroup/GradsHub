package com.codefusiongroup.gradshub.authentication.registration;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.codefusiongroup.gradshub.authentication.AuthenticationActivityTest.waitForResources;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logUserOut;
import static org.hamcrest.Matchers.allOf;

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
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class RegisterFragmentTest {
    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<AuthenticationActivity>(AuthenticationActivity.class);

    private String fieldError = new String("Field can't be empty!");

    private ViewInteraction reg = onView(withId(R.id.registerBtn));
    @Before
    public void nagivateToRegister(){
        //Nagivate to registration screen
       reg.perform(click());
    }

    @After
    public void tearDown(){
        reg = null;
    }

    @Test
    public void registerUserTest() throws InterruptedException {
        onView(withId(R.id.firstNameET)).perform(typeText("f_nameSelf"));
        onView(withId(R.id.lastNameET)).perform(typeText("lname"));
        onView(withId(R.id.emailET)).perform(typeText("tester141414fhfvbd@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.phoneNumberET)).perform(typeText(String.valueOf("123456789")), closeSoftKeyboard());
        onView(withId(R.id.spinner)).perform(click());
        onView(allOf(withText("Honours"))).perform(scrollTo(), click());
        ViewInteraction view4 = onView(withId(R.id.passwordET));
        view4.perform(typeText("!"),closeSoftKeyboard());
        ViewInteraction view5 = onView(withId(R.id.confirmNewPasswordET));
        view5.perform(scrollTo(),typeText("!"), closeSoftKeyboard());
        onView(withId(R.id.submitBtn)).perform(scrollTo(), click());
        Thread.sleep(1500);
        //onView(withId(R.id.Fragment_container)).check(matches(isDisplayed()));          //On success registration, the user is taken back to login screen.
    }

    @Test
    public void testInvalidFirstName(){
        onView(withId(R.id.submitBtn)).perform(scrollTo(), click());
        onView(withId(R.id.firstNameET)).check(matches(hasErrorText(fieldError)));      //Correct error message was returned.
    }

    @Test
    public void testInvalidLastName(){
        onView(withId(R.id.firstNameET)).perform(typeText("f_name"), closeSoftKeyboard());
        onView(withId(R.id.submitBtn)).perform(scrollTo(), click());
        onView(withId(R.id.lastNameET)).check(matches(hasErrorText(fieldError)));      //Correct error message was returned.
    }

    @Test
    public void testInvalidEmail(){
        //first test is for no email was provided
        onView(withId(R.id.firstNameET)).perform(typeText("f_name"));
        onView(withId(R.id.lastNameET)).perform(typeText("lname"));
        //second test is for checking if correct error is shown when incorrect email is provided
        onView(withId(R.id.emailET)).perform(typeText("tester141414fhfvbdcom"), closeSoftKeyboard());
        onView(withId(R.id.submitBtn)).perform(scrollTo(), click());
        onView(withId(R.id.emailET)).check(matches(hasErrorText("check that your email address is entered correctly!")));      //Correct error message was returned.
    }

    @Test
    public void testInvalidPhoneNumber() throws InterruptedException {
        //Invalid phone number is provided.
        onView(withId(R.id.firstNameET)).perform(typeText("f_name"), closeSoftKeyboard());
        onView(withId(R.id.lastNameET)).perform(typeText("lname"), closeSoftKeyboard());
        onView(withId(R.id.emailET)).perform(typeText("tester141414fhfvbd@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.submitBtn)).perform(scrollTo());
        onView(withId(R.id.submitBtn)).perform(click());
        onView(withId(R.id.phoneNumberET)).check(matches(hasErrorText(fieldError)));      //Correct error message was returned.

    }

    @Test
    public void testNoAccademicStatusSelected(){
        onView(withId(R.id.firstNameET)).perform(typeText("f_name"));
        onView(withId(R.id.lastNameET)).perform(typeText("lname"));
        onView(withId(R.id.emailET)).perform(typeText("tester141414fhfvbd@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.phoneNumberET)).perform(typeText(String.valueOf("123456789")), closeSoftKeyboard());
        onView(withId(R.id.submitBtn)).perform(scrollTo(), click());
//        onView(withId(R.id.spinner)).check(matches(hasErrorText("Please select valid academic status!")));      //Correct error message was returned.
    }

    @Test
    public void testNoPassowordIsEntered() throws InterruptedException {
        onView(withId(R.id.firstNameET)).perform(typeText("f_name"));
        onView(withId(R.id.lastNameET)).perform(typeText("lname"), closeSoftKeyboard());
        onView(withId(R.id.emailET)).perform(typeText("tester141414fhfvbd@gmail.com"),closeSoftKeyboard());
        //closeSoftKeyboard();
        onView(withId(R.id.phoneNumberET)).perform(typeText(String.valueOf("123456789")), closeSoftKeyboard());
        onView(withId(R.id.spinner)).perform(click());
        onView(allOf(withText("Honours"))).perform(click());
        onView(withId(R.id.submitBtn)).perform(scrollTo(), click());
        onView(withId(R.id.passwordET)).check(matches(hasErrorText(fieldError)));      //Correct error message was returned.
    }

    @Test
    public void testPasswordValidator(){
        //First test is when no password was entered.
        onView(withId(R.id.firstNameET)).perform(typeText("f_name"));
        onView(withId(R.id.lastNameET)).perform(typeText("lname"));
        onView(withId(R.id.emailET)).perform(typeText("tester141414fhfvbd@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.phoneNumberET)).perform(typeText(String.valueOf("123456789")), closeSoftKeyboard());
        onView(withId(R.id.spinner)).perform(click());
        onView(allOf(withText("Honours"))).perform(click());
        ViewInteraction view4 = onView(withId(R.id.passwordET));
        view4.perform(typeText("aBC123xyZ!"), closeSoftKeyboard());
        onView(withId(R.id.submitBtn)).perform(scrollTo(), click());
        onView(withId(R.id.confirmNewPasswordET)).check(matches(hasErrorText(fieldError)));      //Correct error message was returned.
        //Second test is when the passwords do not match
        ViewInteraction view5 = onView(withId(R.id.confirmNewPasswordET));
        view5.perform(scrollTo(),typeText("aBC123xydZ!"), closeSoftKeyboard());
        onView(withId(R.id.submitBtn)).perform(scrollTo(), click());
        onView(withId(R.id.confirmNewPasswordET)).check(matches(hasErrorText("password doesn't match the above entered password!")));      //Correct error message was returned.

    }

}
