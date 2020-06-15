package com.example.gradshub.main.home;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.example.gradshub.R;
import com.example.gradshub.main.creategroup.CreateGroupFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class FeedListFragmentTest {
    @Rule
    public ActivityTestRule<FeedListActivity> activityTestRule = new ActivityTestRule<FeedListActivity>(FeedListActivity.class);
    private FeedListActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = activityTestRule.getActivity();
    }

    @Test
    public void FeedListFragmentLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.feedlist_container);
        assertNotNull(rlContainer);
        FeedListFragment Fragment = new FeedListFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.feed_launched);
        assertNotNull(view);
    }

    @Test
    public void FeedListFragmentLaunching()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.feedlist_container);
        assertNotNull(rlContainer);
        FeedListFragment Fragment = new FeedListFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.feedList);
        assertNotNull(view);

        View view1 = Fragment.getView().findViewById(R.id.progress_circular);
        assertNotNull(view1);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;

        mActivity = null;
    }
}