package com.codefusiongroup.gradshub.authentication;

import android.view.View;

import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.codefusiongroup.gradshub.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class AuthanticationActivityTest {
    @Rule
    public ActivityTestRule<AuthenticationActivity>rule =
            new ActivityTestRule<>(AuthenticationActivity.class);

    //This test can be changed whenever the first activity is changed
    @Test
    public void ensureTheAppStarts() throws Exception{
        AuthenticationActivity activity = rule.getActivity();
        View viewById = activity.findViewById(R.id.authentication_nav_host_fragment);
        assertThat(viewById, notNullValue());
    }

    //Testing navigation
    @Test
    public void testAuthanticationNavigationUI(){
        TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());
        navController.setGraph(R.navigation.authentication_navigation);
        //FragmentScenario<AuthenticationActivity> fragment = FragmentScenario.launchInContainer(AuthenticationActivity.class);
        onView(withId(R.id.registerBtn))
                .check(matches(isDisplayed()));
        onView(withId(R.id.registerBtn))
                .perform(click());
        pressBack();

        onView(withId(R.id.forgotPasswordBtn))
                .perform(click());

        onView(withId(R.id.resetPasswordBtn))
                .perform(click());
    }


}
