package com.codefusiongroup.gradshub.authentication.registration;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logUserOut;
import static org.hamcrest.Matchers.allOf;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;

import org.junit.Before;
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

    @Before
    public void nagivateToRegister() throws InterruptedException {
        try {
            //Nagivate to registration screen
            onView(withId(R.id.registerBtn)).perform(click());
        }
        catch (NoMatchingViewException e){
            logUserOut();
        }
    }

    @Test
    public void registerUserTest() throws InterruptedException {
        onView(withId(R.id.firstNameET)).perform(typeText("f_name"));
        onView(withId(R.id.lastNameET)).perform(typeText("lname"));
        onView(withId(R.id.emailET)).perform(typeText("tester141414fhfvbd@gmail.com"));
        closeSoftKeyboard();
        onView(withId(R.id.phoneNumberET)).perform(typeText(String.valueOf("123456789")));
        closeSoftKeyboard();
        onView(withId(R.id.spinner)).perform(click());
        onView(allOf(withText("Honours"))).perform(click());
        ViewInteraction view4 = onView(withId(R.id.passwordET));
        view4.perform(typeText("aBC123xyZ!"));
        closeSoftKeyboard();
        ViewInteraction view5 = onView(withId(R.id.confirmNewPasswordET));
        view5.perform(scrollTo(),typeText("aBC123xyZ!"));
        closeSoftKeyboard();
        onView(withId(R.id.submitBtn)).perform(scrollTo(), click());
        Thread.sleep(5500);
        onView(withId(R.id.Fragment_container)).check(matches(isDisplayed()));          //On success registration, the user is taken back to login screen.
    }

    @Test
    public void testInvalidFirstName(){
        onView(withId(R.id.submitBtn)).perform(click());
        onView(withId(R.id.firstNameET)).check(matches(hasErrorText(fieldError)));      //Correct error message was returned.
    }

    @Test
    public void testInvalidLastName(){
        onView(withId(R.id.firstNameET)).perform(typeText("f_name"));
        onView(withId(R.id.submitBtn)).perform(click());
        onView(withId(R.id.lastNameET)).check(matches(hasErrorText(fieldError)));      //Correct error message was returned.
    }

    @Test
    public void testInvalidEmail(){
        //first test is for no email was provided
        onView(withId(R.id.firstNameET)).perform(typeText("f_name"));
        onView(withId(R.id.lastNameET)).perform(typeText("lname"));
        onView(withId(R.id.submitBtn)).perform(click());
        onView(withId(R.id.emailET)).check(matches(hasErrorText(fieldError)));      //Correct error message was returned.
        //second test is for checking if correct error is shown when incorrect email is provided
        onView(withId(R.id.emailET)).perform(typeText("tester141414fhfvbdcom"));
        onView(withId(R.id.submitBtn)).perform(click());
        onView(withId(R.id.emailET)).check(matches(hasErrorText("check that your email address is entered correctly!")));      //Correct error message was returned.
    }

    @Test
    public void testInvalidPhoneNumber(){
        //Invalid phone number is provided.
        onView(withId(R.id.firstNameET)).perform(typeText("f_name"));
        onView(withId(R.id.lastNameET)).perform(typeText("lname"));
        onView(withId(R.id.emailET)).perform(typeText("tester141414fhfvbd@gmail.com"));
        closeSoftKeyboard();
        onView(withId(R.id.submitBtn)).perform(click());
        onView(withId(R.id.phoneNumberET)).check(matches(hasErrorText(fieldError)));      //Correct error message was returned.

    }

    @Test
    public void testNoAccademicStatusSelected(){
        onView(withId(R.id.firstNameET)).perform(typeText("f_name"));
        onView(withId(R.id.lastNameET)).perform(typeText("lname"));
        onView(withId(R.id.emailET)).perform(typeText("tester141414fhfvbd@gmail.com"));
        closeSoftKeyboard();
        onView(withId(R.id.phoneNumberET)).perform(typeText(String.valueOf("123456789")));
        closeSoftKeyboard();
        onView(withId(R.id.submitBtn)).perform(click());
//        onView(withId(R.id.spinner)).check(matches(hasErrorText("Please select valid academic status!")));      //Correct error message was returned.
    }

    @Test
    public void testNoPassowordIsEntered(){
        onView(withId(R.id.firstNameET)).perform(typeText("f_name"));
        onView(withId(R.id.lastNameET)).perform(typeText("lname"));
        onView(withId(R.id.emailET)).perform(typeText("tester141414fhfvbd@gmail.com"));
        closeSoftKeyboard();
        onView(withId(R.id.phoneNumberET)).perform(typeText(String.valueOf("123456789")));
        closeSoftKeyboard();
        onView(withId(R.id.spinner)).perform(click());
        onView(allOf(withText("Honours"))).perform(click());
        onView(withId(R.id.submitBtn)).perform(click());
        onView(withId(R.id.passwordET)).check(matches(hasErrorText(fieldError)));      //Correct error message was returned.
    }

    @Test
    public void testPasswordValidator(){
        //First test is when no password was entered.
        onView(withId(R.id.firstNameET)).perform(typeText("f_name"));
        onView(withId(R.id.lastNameET)).perform(typeText("lname"));
        onView(withId(R.id.emailET)).perform(typeText("tester141414fhfvbd@gmail.com"));
        closeSoftKeyboard();
        onView(withId(R.id.phoneNumberET)).perform(typeText(String.valueOf("123456789")));
        closeSoftKeyboard();
        onView(withId(R.id.spinner)).perform(click());
        onView(allOf(withText("Honours"))).perform(click());
        ViewInteraction view4 = onView(withId(R.id.passwordET));
        view4.perform(typeText("aBC123xyZ!"));
        closeSoftKeyboard();
        onView(withId(R.id.submitBtn)).perform(click());
        onView(withId(R.id.confirmNewPasswordET)).check(matches(hasErrorText(fieldError)));      //Correct error message was returned.
        //Second test is when the passwords do not match
        ViewInteraction view5 = onView(withId(R.id.confirmNewPasswordET));
        view5.perform(scrollTo(),typeText("aBC123xydZ!"));
        closeSoftKeyboard();
        onView(withId(R.id.submitBtn)).perform(click());
        onView(withId(R.id.confirmNewPasswordET)).check(matches(hasErrorText("password doesn't match the above entered password!")));      //Correct error message was returned.

    }

}