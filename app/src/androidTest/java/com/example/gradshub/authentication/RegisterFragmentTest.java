package com.example.gradshub.authentication;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

//import static com.google.android.apps.common.testing.ui.espresso.Espresso.onData;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.gradshub.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class RegisterFragmentTest {
    @Rule
    public FragmentTestRule<?, RegisterFragment> fragmentTestRule =
            FragmentTestRule.create(RegisterFragment.class);

    @Test
    public void RegistrationFragmentLaunched()
    {
        onView(withId(R.id.firstNameET)).check(matches(isDisplayed()));
        onView(withId(R.id.lastNameET)).check(matches(isDisplayed()));
        onView(withId(R.id.emailET)).check(matches(isDisplayed()));
        onView(withId(R.id.phoneNumberET)).check(matches(isDisplayed()));
        onView(withId(R.id.academicStatusTV)).check(matches(isDisplayed()));
        onView(withId(R.id.spinner)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordET)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmNewPasswordET)).check(matches(isDisplayed()));
        onView(withId(R.id.submitBtn)).check(matches(isDisplayed()));
    }

   /* @Test
    public void RegistrationTest()
    {
        onView(withId(R.id.firstNameET)).perform(typeText("Mac"));
        onView(withId(R.id.lastNameET)).perform(typeText("Cauley"));
        onView(withId(R.id.emailET)).perform(typeText("mac@gmail.com"));
        onView(withId(R.id.phoneNumberET)).perform(typeText("0791234568"));
        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("PhD"))).perform(click());
        onView(withId(R.id.spinner)).check(matches(withSpinnerText(containsString("PhD"))));
        onView(withId(R.id.passwordET)).perform(typeText("2222cccc"));
        onView(withId(R.id.confirmNewPasswordET)).perform(typeText("2222cccc"));
        onView(withId(R.id.submitBtn)).perform(click());
    }*/

}