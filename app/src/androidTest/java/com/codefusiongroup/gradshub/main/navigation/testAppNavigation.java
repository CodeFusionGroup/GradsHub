package com.codefusiongroup.gradshub.main.navigation;

import android.app.Instrumentation;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.ActivityResultMatchers;
import androidx.test.espresso.contrib.DrawerActions;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthanticationActivityTest;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;
import com.codefusiongroup.gradshub.authentication.LoginFragment;
import com.codefusiongroup.gradshub.authentication.TestingActivity;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.core.internal.deps.guava.base.Preconditions.checkNotNull;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.EasyMock2Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class testAppNavigation {
    //We will be using login rule as it is the first activity required to access other activities(I.e activities we cannot test in isolation)
    @Rule
    //public ActivityTestRule<TestingActivity> activityActivityTestRule = new ActivityTestRule<TestingActivity>(TestingActivity.class);
    public ActivityTestRule<AuthenticationActivity>rule = new ActivityTestRule<>(AuthenticationActivity.class);
    //private TestingActivity loginFragment = null;

/*    @Before
    public void setUp() throws Exception{
        loginFragment = activityActivityTestRule.getActivity();
    }
*/
    private void createGroupTest(){
        onView(withText("Create Group"))
                .perform(click());

        //Click when there is no available input
        onView(withText("DONE"))
                .perform(click());

        onView(withId(R.id.groupNameET))
                .perform(typeText("The_Private_Group"));
        closeSoftKeyboard();

        //Click when there no radio button option selected
        onView(withText("DONE"))
                .perform(click());


        //View view22 = Fragment.getView().findViewById(R.id.publicRB);
        //view22.performClick();
        onView(withId(R.id.privateRB))
                .perform(click());

        //Click when there no radio button option selected
        onView(withText("DONE"))
                .perform(click());

        //Give enough time delay to get server response
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void openDrawer(){
        //Reopen drawer
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());
    }

    private void createPost() throws InterruptedException {
        openDrawer();
        onView(withText("My Groups"))
                .perform(click());

        waitForResources(2500);

        onView(withText("The_Private_Group"))
                .perform(click());
        waitForResources(2500);

        onView(withId(R.id.fab)).perform(click());

        //Invalid operation
        onView(withId(R.id.postBtn)).perform(click());

        onView(withId(R.id.postSubjectET))
                .perform(typeText("testPost"));
        closeSoftKeyboard();

        //Invalid operation
        onView(withId(R.id.postBtn)).perform(click());

        onView(withId(R.id.postDescriptionET))
                .perform(typeText("www.google.com"));
        closeSoftKeyboard();
        //valid operation

       onView(withId(R.id.postBtn)).perform(click());
        //wait for server response
        waitForResources(2500);
        pressBack();

    }

    private void chatTest() throws InterruptedException {
        openDrawer();
        onView(withText("Direct Messages")).perform(click());
        onView(withText("Kamo Kamo")).perform(click());
        waitForResources(2000);
        onView(withId(R.id.typeMessageET)).perform(typeText("Hey There!"));
        closeSoftKeyboard();
        onView(withId(R.id.submitMessageBtn)).perform(click());
        pressBack();

    }

    private void tesPostComments() throws InterruptedException {

        //Like post first
        onView(withText("Tutoring Science"))
                .perform(click());
        waitForResources(2500);
        onView(withId(R.id.postLikeBtn)).perform(click());

        onView(withId(R.id.commentBtn)).perform(click());
        waitForResources(2500);

        //Post Our comment
        Random rd = new Random();
        String comment =  "We are" + Integer.toString(rd.nextInt()) + "% Happy";

        onView(withId(R.id.typeCommentET)).perform(typeText(comment));
        onView(withId(R.id.submitCommentBtn)).perform(click());

        waitForResources(3000);
        closeSoftKeyboard();
        pressBack();
    }

    //Matcher helper for the testTaskScheduler below
    private Matcher<Object> withItemContent(String s) {
        checkNotNull(s);
        return withItemContent(equalTo(s));
    }
    //TODO: Update this test, after it has been fully implemented

    private void testTaskScheduler() throws InterruptedException {

        openDrawer();
        onView(withText("Schedule")).perform(click());
        waitForResources(3000);
        //This will upvote any first event. TODO: Update this test when implementation of Task Scheduler is finished
        onView(ViewMatchers.withId(R.id.scheduleList)).perform(RecyclerViewActions.actionOnItemAtPosition(1, MyViewAction.clickChildWithId(R.id.upVoteBtn)));
        //Star the event
        onView(ViewMatchers.withId(R.id.scheduleList)).perform(RecyclerViewActions.actionOnItemAtPosition(1, MyViewAction.clickChildWithId(R.id.favouriteBtn)));
    }

    private void testShareInviteCode() {

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
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.action_share)).perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("CANCEL")).perform(click());

    };

    //Test the whole app activities in general, mostly those with private methods
    //@Ignore("Intend to see if it is the one failing travis build")
    @Test
    public void testUserActivities() throws InterruptedException {
       TestNavHostController navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());
        navController.setGraph(R.navigation.authentication_navigation);

        //Log test user in
        onView(withId(R.id.emailET))
                .perform(ViewActions.typeText("testuser@gmail.com"));
        onView(withId(R.id.passwordET))
                .perform(ViewActions.typeText("simple1"));
        closeSoftKeyboard();
        onView(withId(R.id.loginBtn))
                .perform(click());
        //Wait for 12 seconds to  log in, else the log in fails due to slow network!!!
        waitForResources(12000);
        //waitForResources(6000);

        openDrawer();
        //Click on profile option
        onView(withText("Profile"))
                .perform(click());


        openDrawer();
        //Must Add a create group interaction
        onView(withText("Search Groups"))
                .perform(click());

        waitForResources(3000);

        openDrawer();

        onView(withText("Home"))
                .perform(click());

        //Create group
        openDrawer();

        createGroupTest();

        //NOTE: This two methods depend on each other, so must strictly be in this order
        createPost();
        tesPostComments();

       testTaskScheduler();

       testShareInviteCode();

       chatTest();

    }

    //TODO: Implement a test for a logout button


    private void waitForResources(long millis) throws InterruptedException {
            Thread.sleep(millis);
    }
}

class MyViewAction{
    static ViewAction clickChildWithId(final int id){
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with a specific id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
    }
}