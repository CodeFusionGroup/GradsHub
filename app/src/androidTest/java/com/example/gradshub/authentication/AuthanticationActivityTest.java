package com.example.gradshub.authentication;

import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.gradshub.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
public class AuthanticationActivityTest {
    @Rule
    public ActivityTestRule<AuthenticationActivity>rule =
            new ActivityTestRule<>(AuthenticationActivity.class);

    //This test can be changed whenever the first activity is changed
    @Test
    public void ensureTheAppStarts() throws Exception{
        AuthenticationActivity activity = rule.getActivity();
        View viewById = activity.findViewById(R.id.authentication_nav_host_fragment);
        assertThat(viewById, notNullValue());
    }
}
