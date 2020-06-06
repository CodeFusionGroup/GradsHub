package com.example.gradshub.main.profile;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.gradshub.R;
import com.example.gradshub.authentication.LoginFragment;
import com.example.gradshub.authentication.TestingActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class ProfileFragmentTest {

    /*@Rule
    public FragmentTestRule<?,ProfileFragment> fragmentTestRule = FragmentTestRule.create(ProfileFragment.class);

    @Test
    public void ProfileFragmentLaunched()
    {
        onView(withId(R.id.profile)).check(matches(isDisplayed()));
    }*/
    @Rule
    public ActivityTestRule<ProfileTestActivity> activityActivityTestRule = new ActivityTestRule<ProfileTestActivity>(ProfileTestActivity.class);
    private ProfileTestActivity profileFragment = null;

    @Before
    public void setUp() throws Exception{
        profileFragment = activityActivityTestRule.getActivity();
    }

    @Test
    public void testingLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) profileFragment.findViewById(R.id.profile_testing_test);
        assertNotNull(rlContainer);

        ProfileFragment Fragment = new ProfileFragment();
        profileFragment.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View fView = Fragment.getView().findViewById(R.id.profile);
        assertNotNull(fView);
    }

    @After
    public void tearDown() throws Exception {
        profileFragment = null;

        profileFragment = null;

    }
}