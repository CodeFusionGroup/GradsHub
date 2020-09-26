package com.codefusiongroup.gradshub.events;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;
import com.codefusiongroup.gradshub.common.MyViewAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.codefusiongroup.gradshub.authentication.AuthenticationActivityTest.waitForResources;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logInUser;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logUserOut;
import static com.codefusiongroup.gradshub.common.AssisterMethods.openDrawer;
import static org.junit.Assert.*;

@Ignore
public class ScheduleListFragmentTest {
    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<AuthenticationActivity>(AuthenticationActivity.class);

    @Before
    public void setUp() throws Exception {
        logInUser();
    }

    @After
    public void tearDown() throws Exception {
        logUserOut();
    }

    @Test
    public void testTaskScheduler() throws InterruptedException {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        openDrawer();
        onView(withText("Schedule")).perform(click());
        waitForResources(3000);
        onView(ViewMatchers.withId(R.id.scheduleList)).perform(RecyclerViewActions.actionOnItemAtPosition(1, MyViewAction.clickChildWithId(R.id.favouriteBtn)));//Star event
        waitForResources(1500);
        onView(ViewMatchers.withId(R.id.scheduleList)).perform(RecyclerViewActions.actionOnItemAtPosition(1, MyViewAction.clickChildWithId(R.id.favouriteBtn)));//Unstar even
        waitForResources(1500);
        onView(withText("WMT")).perform(click());
        device.pressBack();
    }
}