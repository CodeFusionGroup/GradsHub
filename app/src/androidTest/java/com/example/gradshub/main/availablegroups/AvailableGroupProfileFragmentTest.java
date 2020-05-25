package com.example.gradshub.main.availablegroups;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.gradshub.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class AvailableGroupProfileFragmentTest {

    @Rule
    public FragmentTestRule<?,AvailableGroupProfileFragment> fragmentTestRule = FragmentTestRule.create(AvailableGroupProfileFragment.class);

    @Test
    public void Launched()
    {
        onView(withId(R.id.launchedAvailableGroup)).check(matches(isDisplayed()));
    }

    @Test
    public void AvailableGroupProfileFragmentLaunched()
    {
        onView(withId(R.id.joinBtn)).check(matches(isDisplayed()));
    }

    @Test
    public void AvailableGroupProfileFragmentTest()
    {
        onView(withId(R.id.joinBtn)).perform(click());
    }

}