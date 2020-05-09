package com.example.gradshub.authentication;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.gradshub.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class ResetPasswordFragmentTest {

   /* @Rule
    public FragmentTestRule<?,ResetPasswordFragment> fragmentTestRule = FragmentTestRule.create(ResetPasswordFragment.class);

    @Test
    public void ResetPasswordLaunched()
    {
        onView(withId(R.id.resetPasswordTV)).check(matches(isDisplayed()));
        onView(withId(R.id.newPasswordET)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmNewPasswordTV)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmNewPasswordET)).check(matches(isDisplayed()));
        onView(withId(R.id.resetPasswordBtn)).check(matches(isDisplayed()));
    }*/

    /*@Test
    public void ResetPasswordTest()
    {
        onView(withId(R.id.newPasswordET)).perform(typeText("3333nnnn"));
        onView(withId(R.id.confirmNewPasswordET)).perform(typeText("3333nnnn"));
        onView(withId(R.id.resetPasswordBtn)).perform(click());
    }*/

}