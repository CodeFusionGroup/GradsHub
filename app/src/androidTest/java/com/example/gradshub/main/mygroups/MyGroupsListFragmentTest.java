package com.example.gradshub.main.mygroups;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.example.gradshub.R;
//import com.example.gradshub.authentication.LoginFragment;
//import com.example.gradshub.main.availablegroups.GroupListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class MyGroupsListFragmentTest {

    /*@Rule
    public ActivityTestRule<GroupListlistActivity> activityTestRule = new ActivityTestRule<GroupListlistActivity>(GroupListlistActivity.class);
    private GroupListlistActivity mActivity = null;

    @Before
    public void setUp() throws Exception {

        mActivity = activityTestRule.getActivity();
    }

    @Test
    public void testingLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.my_group_list);
        assertNotNull(rlContainer);

        MyGroupsListFragment Fragment = new MyGroupsListFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View fView = Fragment.getView().findViewById(R.id.group_list_launched);
        assertNotNull(fView);
    }

    @Test
    public void testLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.my_group_list);
        assertNotNull(rlContainer);

        MyGroupsListFragment Fragment = new MyGroupsListFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View fView = Fragment.getView().findViewById(R.id.myGroupsList);
        assertNotNull(fView);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;

        mActivity = null;
    }*/
}