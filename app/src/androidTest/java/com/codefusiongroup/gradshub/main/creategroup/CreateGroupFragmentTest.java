package com.codefusiongroup.gradshub.main.creategroup;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.codefusiongroup.gradshub.R;
//import com.example.gradshub.authentication.RegisterFragment;
//import com.example.gradshub.main.availablegroups.AvailableTestingActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class CreateGroupFragmentTest {

    @Rule
    public ActivityTestRule<CreateGroupActivity> activityTestRule = new ActivityTestRule<CreateGroupActivity>(CreateGroupActivity.class);
    private CreateGroupActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = activityTestRule.getActivity();
    }

    @Test
    public void CreateGroupFragmentLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.creatgroup_test);
        assertNotNull(rlContainer);
        CreateGroupFragment Fragment = new CreateGroupFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.create_group);
        assertNotNull(view);
    }

    @Test
    public void CreateGroupFragmentLaunching()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.creatgroup_test);
        assertNotNull(rlContainer);
        CreateGroupFragment Fragment = new CreateGroupFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.groupNameET);
        assertNotNull(view);
        View view1 = Fragment.getView().findViewById(R.id.groupVisibilityTV);
        assertNotNull(view1);
        View view2 = Fragment.getView().findViewById(R.id.radioGroup);
        assertNotNull(view2);
        View view3 = Fragment.getView().findViewById(R.id.doneBtn);
        assertNotNull(view3);
    }


    @After
    public void tearDown() throws Exception {
        mActivity = null;

    }

}