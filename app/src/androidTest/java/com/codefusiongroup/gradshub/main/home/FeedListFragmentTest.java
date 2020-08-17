package com.codefusiongroup.gradshub.main.home;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.rule.ActivityTestRule;

import com.codefusiongroup.gradshub.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
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

    @Test
    public void testNavigateToS(){
        TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());
        navController.setGraph(R.navigation.main_navigation);
        FragmentScenario<FeedListFragment> fragment = FragmentScenario.launchInContainer(FeedListFragment.class);

//        onView(withId(R.id.feedList))
  //              .check(matches(isDisplayed()));

    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;

        mActivity = null;
    }
}