package com.codefusiongroup.gradshub.posts.postcomments;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.codefusiongroup.gradshub.authentication.AuthenticationActivityTest.waitForResources;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logInUser;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logUserOut;
import static com.codefusiongroup.gradshub.common.AssisterMethods.openDrawer;
import static org.junit.Assert.*;


public class GroupPostCommentsFragmentTest {
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
    public void testPostComments() throws InterruptedException {
        openDrawer();
        onView(withText("My Groups"))
                .perform(click());
        waitForResources(2500);
        //Like post first
        onView(withText("Tutoring Science"))
                .perform(click());
        waitForResources(2500);
        onView(withId(R.id.postLikeBtn)).perform(click());

        onView(withId(R.id.commentBtn)).perform(click());
        waitForResources(2500);

        //Invalid comment
        onView(withId(R.id.submitCommentBtn)).perform(click());

        //Post Our comment
        String comment =  "We are 100% Happy";

        onView(withId(R.id.typeCommentET)).perform(typeText(comment));
        onView(withId(R.id.submitCommentBtn)).perform(click());

        waitForResources(3000);
        closeSoftKeyboard();
        pressBack();
    }
}