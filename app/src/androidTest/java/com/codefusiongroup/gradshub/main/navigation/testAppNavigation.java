package com.codefusiongroup.gradshub.main.navigation;

import android.app.Instrumentation;
import android.content.Intent;
import android.widget.RelativeLayout;

import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.ActivityResultMatchers;
import androidx.test.espresso.contrib.DrawerActions;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
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

    private void createPost(){

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

        //TODO: The create post button has some issues
       /* onView(withId(R.id.postBtn)).perform(click());
        //wait for server response
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
/*
        onData(withItemContent("This post is for tests"))
                .onChildView(withText("COMMENTS")).perform(click());

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
  */
    }


    //Matcher helper for the testTaskScheduler below
    private Matcher<Object> withItemContent(String s) {
        checkNotNull(s);
        return withItemContent(equalTo(s));
    }
    //TODO: Update this test, after it has been fully implemented

    private void testTaskScheduler(){
        onView(withText("Schedule")).perform(click());
        //onData(withItemContent("3DV")).perform(click());
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
    //@Ignore("Intend to see if it is stalling travis build")
    @Test
    public void testUserActivities()
    {
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

        //Wait for 1 minute to  log in, else the log in fails due to slow network!!!
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        openDrawer();
        //Click on profile option
        onView(withText("Profile"))
                .perform(click());

        openDrawer();
        //Must Add a create group interaction
        onView(withText("Search Groups"))
                .perform(click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        openDrawer();

        onView(withText("Home"))
                .perform(click());

        //Create group
        openDrawer();

        createGroupTest();

        //TODO: There create post button has some issues
        openDrawer();

        createPost();

        openDrawer();

        testTaskScheduler();



        testShareInviteCode();


    }






    /*@After
    public void tearDown() throws Exception {
        loginFragment = null;
    }*/
}

