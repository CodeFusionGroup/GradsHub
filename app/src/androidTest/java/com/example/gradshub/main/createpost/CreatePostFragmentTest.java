package com.example.gradshub.main.createpost;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.gradshub.R;
import com.example.gradshub.main.creategroup.CreateGroupActivity;
import com.example.gradshub.main.creategroup.CreateGroupFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

//@RunWith(AndroidJUnit4::class)
public class CreatePostFragmentTest {

    @Rule
    public ActivityTestRule<CreatePostActivity> activityTestRule = new ActivityTestRule<CreatePostActivity>(CreatePostActivity.class);
    private CreatePostActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = activityTestRule.getActivity();
        
    }

    @Test
    public void justFrag(){

    }

    @Test
    public void Launched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.postcreator_testing);
        assertNotNull(rlContainer);
        CreatePostFragment Fragment = new CreatePostFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.crete_post);
        assertNotNull(view);
    }

    @Test
    public void AutoLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.postcreator_testing);
        assertNotNull(rlContainer);
        CreatePostFragment Fragment = new CreatePostFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.postTitleTV);
        assertNotNull(view);

        View view1 = Fragment.getView().findViewById(R.id.postSubjectContainer);
        assertNotNull(view1);

        //View view2 = Fragment.getView().findViewById(R.id.postSubjectET);
        //assertNotNull(view2);
        ViewInteraction view2 = onView(withId(R.id.postSubjectET));
        view2.perform(ViewActions.typeText("Creating a post"));

        View view3 = Fragment.getView().findViewById(R.id.descriptionTV);
        assertNotNull(view3);

        View view4 = Fragment.getView().findViewById(R.id.postDescriptionContainer);
        assertNotNull(view4);

        //View view5 = Fragment.getView().findViewById(R.id.postDescriptionET);
        //assertNotNull(view5);
        ViewInteraction view5 = onView(withId(R.id.postDescriptionET));
        view5.perform(ViewActions.typeText("http link to post"));
        closeSoftKeyboard();

        //View view6 = Fragment.getView().findViewById(R.id.postBtn);
        //view6.performClick();
        ViewInteraction view6 = onView(withId(R.id.postBtn));
        view6.perform(click());
    }

    @After
    public void tearDown() throws Exception {
    }
}
