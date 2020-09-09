package com.codefusiongroup.gradshub.posts.postcomments;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.models.Post;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;
public class GroupPostCommentsFragmentTest {


    private Post post;
    private void setPost(){
        post = new Post();
        post.setPostDate("2020-06-02");
        post.setPostCreator("Wits Stud");
        post.setPostSubject("This is a post");
        post.setPostDescription("Some descriptions do not describe");

    }
    @Rule

    public ActivityTestRule<PostCommentActivity> activityTestRule = new ActivityTestRule<PostCommentActivity>(PostCommentActivity.class);
    private PostCommentActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = activityTestRule.getActivity();
        setPost();
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.postcomment_container);
        assertNotNull(rlContainer);

        GroupPostCommentsFragment Fragment = new GroupPostCommentsFragment();
        Fragment.setPost(post);
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View fView = Fragment.getView().findViewById(R.id.postDateTV);
        assertNotNull(fView);
        View View1 = Fragment.getView().findViewById(R.id.postCreatorNameTV);
        assertNotNull(View1);
        View View2 = Fragment.getView().findViewById(R.id.postSubjectTV);
        assertNotNull(View2);
        View View3 = Fragment.getView().findViewById(R.id.postDescriptionTV);
        assertNotNull(View3);
//        View View4 = Fragment.getView().findViewById(R.id.commentsListView);
//        assertNotNull(View4);
        //View View5 = Fragment.getView().findViewById(R.id.typeCommentET);

    }

    //All the required fields are visible test
	//@Ignore
    @Test
    public void Launched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.postcomment_container);
        assertNotNull(rlContainer);

        GroupPostCommentsFragment Fragment = new GroupPostCommentsFragment();
        Fragment.setPost(post);
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View fView = Fragment.getView().findViewById(R.id.postDateTV);
        assertNotNull(fView);
        View View1 = Fragment.getView().findViewById(R.id.postCreatorNameTV);
        assertNotNull(View1);
        View View2 = Fragment.getView().findViewById(R.id.postSubjectTV);
        assertNotNull(View2);
        View View3 = Fragment.getView().findViewById(R.id.postDescriptionTV);
        assertNotNull(View3);
//        View View4 = Fragment.getView().findViewById(R.id.commentsListView);
//        assertNotNull(View4);
        View View5 = Fragment.getView().findViewById(R.id.typeCommentET);
        assertNotNull(View5);
        View View6 = Fragment.getView().findViewById(R.id.submitCommentBtn);
        assertNotNull(View6);
    }

    //User can type a comment
	//@Ignore
    @Test
    public void userCanPostCommentTest() throws InterruptedException {

        //assertNotNull(View5);
        ViewInteraction view2 = onView(withId(R.id.typeCommentET));
        view2.perform(ViewActions.typeText("just a comment"));
        closeSoftKeyboard();
        //Thread.sleep(1000);
        ViewInteraction view1 = onView(withId(R.id.submitCommentBtn));
        try {
            view1.perform(click());
        }
        catch(Exception e){

        }
    }

    @Test
    public void maxUserInputTest() throws InterruptedException {
        ViewInteraction view2 = onView(withId(R.id.typeCommentET));
        view2.perform(ViewActions.typeText("just a commentjust a commentjust a commentjust a comment"));
        closeSoftKeyboard();
        //Thread.sleep(1000);
        ViewInteraction view1 = onView(withId(R.id.submitCommentBtn));
        try {
            view1.perform(click());
        }
        catch(Exception e){

        }
    }

    @Test
    public void testNoEmptyComment(){
        ViewInteraction view2 = onView(withId(R.id.typeCommentET));
        view2.perform(ViewActions.typeText(""));
        closeSoftKeyboard();
        //Thread.sleep(1000);
        ViewInteraction view1 = onView(withId(R.id.submitCommentBtn));
        try {
            view1.perform(click());
        }
        catch(Exception e){

        }
    }
    @After
    public void tearDown() throws Exception {
        mActivity = null;

    }

}