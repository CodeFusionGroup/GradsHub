package com.example.gradshub.authentication;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.espresso.action.TypeTextAction;
import androidx.test.rule.ActivityTestRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.gradshub.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class ResetPasswordFragmentTest {

    @Rule
    public FragmentTestRule<?,ResetPasswordFragment> fragmentFragmentTestRule = FragmentTestRule.create(ResetPasswordFragment.class);

    @Test
    public void Launching()
    {
        onView(withId(R.id.PassWordReset)).check(matches(isDisplayed()));
    }

    @Test
    public void IsLaunched()
    {
        onView(withId(R.id.resetPasswordTV)).check(matches(isDisplayed()));
        onView(withId(R.id.newPasswordET)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmNewPasswordTV)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmNewPasswordET)).check(matches(isDisplayed()));
        onView(withId(R.id.resetPasswordBtn)).check(matches(isDisplayed()));
    }

    @Test
    public void UnitTesting()
    {
        //onView(withId(R.id.resetPasswordTV)).perform(TypeTextAction(""));
        onView(withId(R.id.newPasswordET)).perform(typeText("12345mmmmm"));
        //onView(withId(R.id.confirmNewPasswordTV)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmNewPasswordET)).perform(typeText("12345mmmmm"));
        onView(withId(R.id.resetPasswordBtn)).perform(click());
    }
   /* @Rule
    public ActivityTestRule<TestingActivity> activityTestRule = new ActivityTestRule<TestingActivity>(TestingActivity.class);

    private TestingActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = activityTestRule.getActivity();
    }

    @Test
    public void TestingLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.containing_tests);
        assertNotNull(rlContainer);
        RegisterFragment Fragment = new RegisterFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.PassWordReset);
        assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;

        mActivity = null;
    }*/
}