package com.example.gradshub.main.profile;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.gradshub.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class ProfileFragmentTest {

    @Rule
    public FragmentTestRule<?,ProfileFragment> fragmentTestRule = FragmentTestRule.create(ProfileFragment.class);

    @Test
    public void ProfileFragmentLaunched()
    {
        onView(withId(R.id.profile)).check(matches(isDisplayed()));
    }

}