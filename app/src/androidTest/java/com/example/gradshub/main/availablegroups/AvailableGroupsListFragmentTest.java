package com.example.gradshub.main.availablegroups;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.example.gradshub.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class AvailableGroupsListFragmentTest {

    @Rule
    public FragmentTestRule<?,AvailableGroupsListFragment> fragmentTestRule = FragmentTestRule.create(AvailableGroupsListFragment.class);

    @Test
    public void AvailableGroupLaunched()
    {
        //onView(withId(R.id.listAvailable)).check(matches(isDisplayed()));
        onView(withId(R.id.card_view)).check(matches(isDisplayed()));
    }

}