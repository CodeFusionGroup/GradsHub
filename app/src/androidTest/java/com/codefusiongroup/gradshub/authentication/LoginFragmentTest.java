package com.codefusiongroup.gradshub.authentication;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import com.codefusiongroup.gradshub.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;


public class LoginFragmentTest {

    @Rule
    public ActivityTestRule<TestingActivity> activityActivityTestRule = new ActivityTestRule<TestingActivity>(TestingActivity.class);
    private TestingActivity loginFragment = null;

    @Before
    public void setUp() throws Exception{
        loginFragment = activityActivityTestRule.getActivity();

    }

    @Test
    public void testLoginFragmentLaunched()
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
    public void testLoginfragmentViews() {
        RelativeLayout rlContainer = (RelativeLayout) loginFragment.findViewById(R.id.containing_tests);
        assertNotNull(rlContainer);
        LoginFragment Fragment = new LoginFragment();
        loginFragment.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(), Fragment).commitAllowingStateLoss();
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


    @After
    public void tearDown() throws Exception {
        loginFragment = null;
    }
}