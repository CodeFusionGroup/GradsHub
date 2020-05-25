package com.example.gradshub.main.availablegroups;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.gradshub.R;
import com.example.gradshub.authentication.RegisterFragment;
import com.example.gradshub.main.MainActivity;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class AvailableGroupProfileFragmentTest {

    /*@Rule
    public FragmentTestRule<?,AvailableGroupProfileFragment> fragmentTestRule = FragmentTestRule.create(AvailableGroupProfileFragment.class);

    @Test
    public void Launch()
    {
        onView(withId(R.id.launchedAvailableGroup)).check(matches(isDisplayed()));
    }*/

    /*@Rule
    public ActivityTestRule<AvailableTestingActivity> activityTestRule = new ActivityTestRule<AvailableTestingActivity>(AvailableTestingActivity.class);
    private AvailableTestingActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = activityTestRule.getActivity();
    }

    @Test
    public void Launched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.available_testing);
        assertNotNull(rlContainer);
        RegisterFragment Fragment = new RegisterFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.launchedAvailableGroup);
        assertNotNull(view);
    }

    @Test
    public void FragmentLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.available_testing);
        assertNotNull(rlContainer);
        RegisterFragment Fragment = new RegisterFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.groupNameTV);
        assertNotNull(view);
        View view1 = Fragment.getView().findViewById(R.id.joinBtn);
        assertNotNull(view1);
        View view2 = Fragment.getView().findViewById(R.id.progress_circular);
        assertNotNull(view2);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;

        mActivity = null;
    }*/
}