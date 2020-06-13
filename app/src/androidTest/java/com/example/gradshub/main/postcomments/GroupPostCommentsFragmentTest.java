package com.example.gradshub.main.postcomments;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.example.gradshub.R;
import com.example.gradshub.authentication.LoginFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class GroupPostCommentsFragmentTest {

    /*@Rule
    public ActivityTestRule<PostCommentActivity> activityTestRule = new ActivityTestRule<PostCommentActivity>(PostCommentActivity.class);
    private PostCommentActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = activityTestRule.getActivity();
    }

    @Test
    public void testingLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.postcomment_container);
        assertNotNull(rlContainer);

        GroupPostCommentsFragment Fragment = new GroupPostCommentsFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View fView = Fragment.getView().findViewById(R.id.post_comment);
        assertNotNull(fView);
    }

    @Test
    public void Launched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.postcomment_container);
        assertNotNull(rlContainer);

        GroupPostCommentsFragment Fragment = new GroupPostCommentsFragment();
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
        View View4 = Fragment.getView().findViewById(R.id.commentsListView);
        assertNotNull(View4);
        View View5 = Fragment.getView().findViewById(R.id.typeCommentET);
        assertNotNull(View5);
        View View6 = Fragment.getView().findViewById(R.id.submitCommentBtn);
        assertNotNull(View6);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;

        mActivity = null;
    }*/
}