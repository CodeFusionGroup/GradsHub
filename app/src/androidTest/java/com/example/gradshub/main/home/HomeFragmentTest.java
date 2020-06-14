package com.example.gradshub.main.home;

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

public class HomeFragmentTest {

    @Rule
    public ActivityTestRule<HomeTestActivity> activityTestrule = new ActivityTestRule<HomeTestActivity>(HomeTestActivity.class);
    private HomeTestActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = activityTestrule.getActivity();
    }

    @Test
    public void testingLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.hometest_container);
        assertNotNull(rlContainer);

        HomeFragment Fragment = new HomeFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View fView = Fragment.getView().findViewById(R.id.homeFragmentView);
        assertNotNull(fView);
    }

    @Test
    public void testing()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.hometest_container);
        assertNotNull(rlContainer);

        HomeFragment Fragment = new HomeFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View fView = Fragment.getView().findViewById(R.id.postsRecyclerView);
        assertNotNull(fView);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;

        mActivity = null;
    }
}