package com.example.gradshub.main.availablegroups;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import com.example.gradshub.R;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

public class AvailableGroupsListFragmentTest {
    @Rule
    public ActivityTestRule<GroupListActivity> activityTestRule = new ActivityTestRule<GroupListActivity>(GroupListActivity.class);
    private AvailableGroupsListFragment mActivity = null;

    @Test
    public void testGroupListView(){
        GroupListActivity activity = activityTestRule.getActivity();
        View viewById = activity.findViewById(R.id.availableGroupsListFragment);
        //assertThat(viewById, notNullValue());
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}