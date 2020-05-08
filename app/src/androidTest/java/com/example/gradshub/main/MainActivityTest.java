package com.example.gradshub.main;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import com.example.gradshub.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);
    private MainActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = activityActivityTestRule.getActivity();
    }

    /*@Test
    public void MainActivityTestLaunched()
    {
        //View nav = mActivity.findViewById(R.id.nav_view);
        //assertNotNull(nav);
        View draw = mActivity.findViewById(R.id.drawer_layout);
        assertNotNull(draw);
        //View main = mActivity.findViewById(R.id.main_navigation);
        //assertNotNull(main);
    }*/

    @After
    public void tearDown() throws Exception {
        mActivity = null;

        mActivity = null;
    }
}