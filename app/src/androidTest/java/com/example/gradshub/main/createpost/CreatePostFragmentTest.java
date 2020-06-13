package com.example.gradshub.main.createpost;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.rule.ActivityTestRule;

import com.example.gradshub.R;
import com.example.gradshub.main.creategroup.CreateGroupActivity;
import com.example.gradshub.main.creategroup.CreateGroupFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class CreatePostFragmentTest {

    /*@Rule
    public ActivityTestRule<CreatePostActivity> activityTestRule = new ActivityTestRule<CreatePostActivity>(CreatePostActivity.class);
    private CreatePostActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
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

    @After
    public void tearDown() throws Exception {
    }*/
}