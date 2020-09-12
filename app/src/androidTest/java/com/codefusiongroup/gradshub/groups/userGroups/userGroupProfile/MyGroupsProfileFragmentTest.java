package com.codefusiongroup.gradshub.groups.userGroups.userGroupProfile;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logInUser;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logUserOut;
import static com.codefusiongroup.gradshub.common.AssisterMethods.openDrawer;

public class MyGroupsProfileFragmentTest {
    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<>(AuthenticationActivity.class);

    @Before
    public void logIn() throws UiObjectNotFoundException, InterruptedException {
        logInUser();
    }

    @After
    public void logOut(){
        logUserOut();
    }

    @Test
    public void testShareGroupInviteCode() throws InterruptedException, UiObjectNotFoundException {
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        openDrawer();
        onView(withText("My Groups"))
                .perform(click());

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("The_Private_Group"))
                .perform(click());

        onView(withId(R.id.action_share)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("OK")).perform(click());
        mDevice.pressBack();
    }

    //Sharing invite code that does not belong to you.
    @Test
    public void shareNotAllowedInvitedCode(){
        openDrawer();
        onView(withText("My Groups"))
                .perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("Machine Learning")).perform(click());
        onView(withId(R.id.action_share)).perform(click());
    }


}