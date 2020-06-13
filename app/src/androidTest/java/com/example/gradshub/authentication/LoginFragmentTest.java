package com.example.gradshub.authentication;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.gradshub.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;

// import android.support.v4.app.Fragment;
// import android.support.v4.app.FragmentManager;
// import android.support.v4.app.FragmentTransaction;

public class LoginFragmentTest {

    /*@Rule
    public FragmentTestRule<?, LoginFragment> fragmentTestRule = FragmentTestRule.create(LoginFragment.class);

    @Test
    public void LoginFragmentLaunched()
    {
        onView(withId(R.id.Launched)).check(matches(isDisplayed()));
    }

    @Test
    public void LoginLaunched() throws Exception {

        onView(withId(R.id.emailET)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordET)).check(matches(isDisplayed()));
        onView(withId(R.id.forgotPasswordBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.registerBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.noAccountTV)).check(matches(isDisplayed()));
        onView(withId(R.id.loginBtn)).check(matches(isDisplayed()));

    }

    @Test
    public void LoginTest()
    {
        onView(withId(R.id.emailET)).perform(typeText("maccauley@gmail.com"));
        onView(withId(R.id.passwordET)).perform(typeText("1234mmmm"));
        closeSoftKeyboard();
        onView(withId(R.id.loginBtn)).perform(click());
        //onView(withId(R.id.homeFragmentView)).check(matches(isDisplayed()));
    }*/

    @Rule
    public ActivityTestRule<TestingActivity> activityActivityTestRule = new ActivityTestRule<TestingActivity>(TestingActivity.class);
    private TestingActivity loginFragment = null;

    @Before
    public void setUp() throws Exception{
        loginFragment = activityActivityTestRule.getActivity();
    }

    @Test
    public void testingLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) loginFragment.findViewById(R.id.containing_tests);
        assertNotNull(rlContainer);

        LoginFragment Fragment = new LoginFragment();
        loginFragment.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View fView = Fragment.getView().findViewById(R.id.Launched);
        assertNotNull(fView);
    }

    @Test
    public void LoginfragmentLaunching()
    {
        RelativeLayout rlContainer = (RelativeLayout) loginFragment.findViewById(R.id.containing_tests);
        assertNotNull(rlContainer);

        LoginFragment Fragment = new LoginFragment();
        loginFragment.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View fView = Fragment.getView().findViewById(R.id.emailET);
        assertNotNull(fView);

        View EmailET = loginFragment.findViewById(R.id.emailET);
        assertNotNull(EmailET);

        View PasswordET = Fragment.getView().findViewById(R.id.passwordET);
        assertNotNull(PasswordET);
        View ForgotPasswordBT = Fragment.getView().findViewById(R.id.forgotPasswordBtn);
        assertNotNull(ForgotPasswordBT);
        View LoginBT = Fragment.getView().findViewById(R.id.loginBtn);
        assertNotNull(LoginBT);
        View NoAccountTV = Fragment.getView().findViewById(R.id.noAccountTV);
        assertNotNull(NoAccountTV);
        View RegisterBT = Fragment.getView().findViewById(R.id.registerBtn);
        assertNotNull(RegisterBT);
    }

    ///*
    @Test
    public void AutoTesting()
    {
        RelativeLayout rlContainer = (RelativeLayout) loginFragment.findViewById(R.id.containing_tests);
        assertNotNull(rlContainer);

        LoginFragment Fragment = new LoginFragment();
        loginFragment.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();

        ViewInteraction view2 = onView(withId(R.id.emailET));
        view2.perform(ViewActions.typeText("maccayley@gmail.com"));
        ViewInteraction view = onView(withId(R.id.passwordET));
        view.perform(ViewActions.typeText("1234mmmm"));
        closeSoftKeyboard();
        //spinner = (ProgressBar)viewIinflated.findViewById(R.id.progressBar1);//same case with dialogs
        //Spinner = (Button) viewIinflated.findViewById(R.id.forgotPasswordBtn);
        //spinner.setVisibility(View.GONE);
        //rootView.findViewById(R.id.forgotPasswordBtn).setVisibility(View.GONE);
        View ForgotPasswordBT = Fragment.getView().findViewById(R.id.forgotPasswordBtn);
        assertNotNull(ForgotPasswordBT);
        View NoAccountTV = Fragment.getView().findViewById(R.id.noAccountTV);
        assertNotNull(NoAccountTV);
        View RegisterBT = Fragment.getView().findViewById(R.id.registerBtn);
        assertNotNull(RegisterBT);
        ViewInteraction view1 = onView(withId(R.id.loginBtn));
        view1.perform(click());
    }
    //*/

    @Test
    public void AutoTestingTest()
    {
        RelativeLayout rlContainer = (RelativeLayout) loginFragment.findViewById(R.id.containing_tests);
        assertNotNull(rlContainer);

        LoginFragment Fragment = new LoginFragment();
        loginFragment.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();

        View registerBT = loginFragment.findViewById(R.id.registerBtn);
        assertNotNull(registerBT);
    }

    /*@Test
    public void AutoTestingRegisterTest()
    {
        RelativeLayout rlContainer = (RelativeLayout) loginFragment.findViewById(R.id.containing_tests);
        assertNotNull(rlContainer);

        LoginFragment Fragment = new LoginFragment();
        loginFragment.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();

        View fView = loginFragment.findViewById(R.id.registerBtn);
        fView.performClick();
    }*/


    @After
    public void tearDown() throws Exception {
        loginFragment = null;

        loginFragment = null;
    }
}