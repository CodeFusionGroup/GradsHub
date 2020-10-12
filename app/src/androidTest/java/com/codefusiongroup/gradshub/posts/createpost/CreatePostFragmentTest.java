package com.codefusiongroup.gradshub.posts.createpost;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.codefusiongroup.gradshub.authentication.AuthenticationActivityTest.waitForResources;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logInUser;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logUserOut;
import static com.codefusiongroup.gradshub.common.AssisterMethods.openDrawer;
import static org.junit.Assert.*;

public class CreatePostFragmentTest {

    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<AuthenticationActivity>(AuthenticationActivity.class);

    @Before
    public void setUp() throws Exception {
        logInUser();
    }

    @Test
    public void createPostTest() throws InterruptedException {
        openDrawer();
        onView(withText("My Groups"))
                .perform(click());

        waitForResources(2500);

        onView(withText("The_Private_Group")).perform(click());
        waitForResources(2500);

        onView(withId(R.id.fab)).perform(click());

        //Invalid operation
        onView(withId(R.id.postBtn)).perform(click());
        onView(withId(R.id.postSubjectET)).check(matches(hasErrorText("Not a valid post subject!")));
        onView(withId(R.id.postSubjectET)).perform(typeText("testPost"), closeSoftKeyboard());

        //Another invalid operation
        onView(withId(R.id.postBtn)).perform(click());
        onView(withId(R.id.postDescriptionET)).check(matches(hasErrorText("Not a valid post description!")));

        //Finally a valid operation
        onView(withId(R.id.postDescriptionET)).perform(typeText("wwww.google.com"), closeSoftKeyboard());
        onView(withId(R.id.postBtn)).perform(click());
        waitForResources(2500);
        onView(withId(R.id.fab)).check(matches(isDisplayed()));                 //Check that the create post button is now visible for successful post created

  //      waitForResources(45000);
    }
    @After
    public void tearDown() throws Exception {
        logUserOut();
    }
}