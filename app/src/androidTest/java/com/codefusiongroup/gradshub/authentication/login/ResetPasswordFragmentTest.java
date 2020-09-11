package com.codefusiongroup.gradshub.authentication.login;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.SmallTest;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logUserOut;
import static org.junit.Assert.*;
@SmallTest
public class ResetPasswordFragmentTest {
    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<AuthenticationActivity>(AuthenticationActivity.class);

    @Before
    public void nagivateToForgotPasswordScreen() throws InterruptedException {
        try {
            //Nagivate to registration screen
            onView(withId(R.id.forgotPasswordBtn)).perform(click());
        }
        catch(NoMatchingViewException e){
            logUserOut();
        }
    }
    //TODO: Full implementation and assertions to be done when the reset password is fully implemented
    @Test
    public void resetPasswordTest(){
        onView(withId(R.id.resetPasswordBtn)).perform(click());
    }

}