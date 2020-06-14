package com.example.gradshub.main.mygroups;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.example.gradshub.R;
//import com.example.gradshub.authentication.LoginFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class MyGroupsProfileFragmentTest {

    /*@Rule
    public ActivityTestRule<GroupProfileActivity> activityTestRule = new ActivityTestRule<GroupProfileActivity>(GroupProfileActivity.class);
    private GroupProfileActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = activityTestRule.getActivity();
    }

    @Test
    public void testingLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.pgroupprofile_test);
        assertNotNull(rlContainer);

        MyGroupsProfileFragment Fragment = new MyGroupsProfileFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View fView = Fragment.getView().findViewById(R.id.Launched__);
        assertNotNull(fView);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;

        mActivity = null;
    }*/
}