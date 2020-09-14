package com.codefusiongroup.gradshub.groups.searchGroups.exploreGroupsList;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.codefusiongroup.gradshub.authentication.AuthenticationActivityTest.waitForResources;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logInUser;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logUserOut;
import static com.codefusiongroup.gradshub.common.AssisterMethods.openDrawer;
import static org.junit.Assert.*;

public class ExploreGroupsFragmentTest {
    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<AuthenticationActivity>(AuthenticationActivity.class);

    @Before
    public void setUp() throws Exception {
        logInUser();
        openDrawer();
        onView(withText("Search Groups")).perform(click());
        waitForResources(1500);
    }

    @Test
    public void testSearchGroups(){
        onView(withId(R.id.list)).check(matches(isDisplayed()));
    }

    @Test
    public void testJoinGroupInvalid(){
        onView(withId(R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.joinBtn)).perform(click());
        onView(withText("CONFIRM")).perform(click());
    }

    @Test
    public void testJoinGroupValid(){
        //TODO: Edit php file to allow dummy joining of groups(Private/Public)

    }

    @After
    public void tearDown() throws Exception {
        logUserOut();
    }
}