package com.example.gradshub.authentication;

import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import com.example.gradshub.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class InvalidLoginTest {
    @Rule
    public ActivityTestRule<AuthenticationActivity> rule = new ActivityTestRule<>(AuthenticationActivity.class);

    @Test
    public void testInvalidEmail()
    {
        TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());
        navController.setGraph(R.navigation.authentication_navigation);

        //Log test user in
        onView(withId(R.id.emailET))
                .perform(ViewActions.typeText("testuser1@gmail.com"));
        onView(withId(R.id.passwordET))
                .perform(ViewActions.typeText("simple1"));
        closeSoftKeyboard();
        onView(withId(R.id.loginBtn))
                .perform(click());
//        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
//Wait for 1 minute to  log in, else the log in fails due to slow network!!!
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNoEmailLogin() {
        TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());
        navController.setGraph(R.navigation.authentication_navigation);

        //Log test user in
        onView(withId(R.id.emailET))
                .perform(ViewActions.typeText(""));
        onView(withId(R.id.passwordET))
                .perform(ViewActions.typeText("simple1"));
        closeSoftKeyboard();
        onView(withId(R.id.loginBtn))
                .perform(click());
    }

    @Test
    public void testNoPassLogin(){
        TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());
        navController.setGraph(R.navigation.authentication_navigation);

        //Log test user in
        onView(withId(R.id.emailET))
                .perform(ViewActions.typeText("testuser1@gmail.com"));
        onView(withId(R.id.passwordET))
                .perform(ViewActions.typeText(""));
        closeSoftKeyboard();
        onView(withId(R.id.loginBtn))
                .perform(click());
    }

}
