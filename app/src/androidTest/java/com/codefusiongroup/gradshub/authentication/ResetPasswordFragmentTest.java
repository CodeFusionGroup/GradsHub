package com.codefusiongroup.gradshub.authentication;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import com.codefusiongroup.gradshub.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class ResetPasswordFragmentTest {

    @Rule
    public ActivityTestRule<ResetActivity> activityTestRule = new ActivityTestRule<ResetActivity>(ResetActivity.class);

    private ResetActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = activityTestRule.getActivity();
    }

    @Test
    public void TestingLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.reset_containing_tests);
        assertNotNull(rlContainer);
        ResetPasswordFragment Fragment = new ResetPasswordFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.PassWordReset);
        assertNotNull(view);
    }
    @Test
    public void ResetPasswordFragmentLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.reset_containing_tests);
        assertNotNull(rlContainer);
        ResetPasswordFragment Fragment = new ResetPasswordFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.resetPasswordTV);
        assertNotNull(view);
        View view1 = Fragment.getView().findViewById(R.id.newPasswordET);
        assertNotNull(view1);
        View view2 = Fragment.getView().findViewById(R.id.confirmNewPasswordTV);
        assertNotNull(view2);
        View view3 = Fragment.getView().findViewById(R.id.confirmNewPasswordET);
        assertNotNull(view3);
        View view4 = Fragment.getView().findViewById(R.id.resetPasswordBtn);
        assertNotNull(view4);
    }

    @Test
    public void AutoTestingResetPasswordFragment()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.reset_containing_tests);
        assertNotNull(rlContainer);
        ResetPasswordFragment Fragment = new ResetPasswordFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.resetPasswordTV);
        assertNotNull(view);
        //View view1 = Fragment.getView().findViewById(R.id.newPasswordET);
        //assertNotNull(view1);
        ViewInteraction view1 = onView(withId(R.id.newPasswordET));
        view1.perform(ViewActions.typeText("nnn123n4"));

        View view2 = Fragment.getView().findViewById(R.id.confirmNewPasswordTV);
        assertNotNull(view2);

        //View view3 = Fragment.getView().findViewById(R.id.confirmNewPasswordET);
        //assertNotNull(view3);
        ViewInteraction view3 = onView(withId(R.id.newPasswordET));
        view3.perform(ViewActions.typeText("nnn123n4"));

        View view4 = Fragment.getView().findViewById(R.id.resetPasswordBtn);
        assertNotNull(view4);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;

        mActivity = null;
    }
}