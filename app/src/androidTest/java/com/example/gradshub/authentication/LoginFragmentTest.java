package com.example.gradshub.authentication;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.test.rule.ActivityTestRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.gradshub.R;
import com.example.gradshub.main.MainActivity;
import com.example.gradshub.main.TestActivity;

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
import static org.junit.Assert.*;

// import android.support.v4.app.Fragment;
// import android.support.v4.app.FragmentManager;
// import android.support.v4.app.FragmentTransaction;

public class LoginFragmentTest {

    @Rule
    public FragmentTestRule<?, LoginFragment> fragmentTestRule =
            FragmentTestRule.create(LoginFragment.class);


    @Test
    public void LoginLaunched() throws Exception {
        //onView(withText(R.string.button)).perform(click());

        onView(withId(R.id.emailET)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordET)).check(matches(isDisplayed()));
        onView(withId(R.id.forgotPasswordBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.registerBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.noAccountTV)).check(matches(isDisplayed()));
        onView(withId(R.id.loginBtn)).check(matches(isDisplayed()));

        //onView(withText("Hello world!")).check(matches(isDisplayed()));
    }

    @Test
    public void LoginTest()
    {
        onView(withId(R.id.emailET)).perform(typeText("maccauley@gmail.com"));
        onView(withId(R.id.passwordET)).perform(typeText("1234mmmm"));
        closeSoftKeyboard();
        onView(withId(R.id.loginBtn)).perform(click());
        //onView(withId(R.id.homeFragmentView)).check(matches(isDisplayed()));
    }

    //@Rule
    //public ActivityTestRule<TestActivity> activityActivityTestRule = new ActivityTestRule<TestActivity>(TestActivity.class);
    //private TestActivity loginFragment = null;

    /*@Before
    public void setUp() throws Exception{
        //loginFragment = activityActivityTestRule.getActivity();
    }*/

   // @Test
   // public void LoginfragmentLaunching()
   // {
        /*
        RelativeLayout rlContainer = (RelativeLayout) loginFragment.findViewById(R.id.test_container);
        assertNotNull(rlContainer);

        LoginFragment Fragment = new LoginFragment();
        //loginFragment.getFragmentManager().beginTransaction().add(rlContainer,Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View fView = Fragment.getView().findViewById(R.id.emailET);
        assertNotNull(fView);

        */
        //View EmailET = loginFragment.findViewById(R.id.emailET);
        //assertNotNull(EmailET);
        /*
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
        */

    //}


    /*@After
    public void tearDown() throws Exception {
        //loginFragment = null;

        //loginFragment = null;

    }*/
}