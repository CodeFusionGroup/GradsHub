package com.codefusiongroup.gradshub.authentication.login;

//import androidx.test.espresso.intent.Intents;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.uiautomator.UiObjectNotFoundException;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;
import com.codefusiongroup.gradshub.utils.notifications.MonthsConstants;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
        import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
        import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.codefusiongroup.gradshub.authentication.AuthenticationActivityTest.waitForResources;
import static com.codefusiongroup.gradshub.common.AssisterMethods.logInUser;
import static org.junit.Assert.assertEquals;

public class LoginFragmentTest {

    private String username = new String("testuser@gmail.com");
    private String password = new String("simple1");
    private String fieldError = new String("Field can't be empty.");
    @Rule
    public ActivityScenarioRule<AuthenticationActivity> rule = new ActivityScenarioRule<AuthenticationActivity>(AuthenticationActivity.class);

    @Test
    public void testUserLogin() throws InterruptedException, UiObjectNotFoundException {
        logInUser();
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));       //This is the first view that should be visible after the user logs in.
        //Log the user out after this login
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        //waitForResources(5000);
        onView(withText("Logout")).perform(click());
        //logUserOut();
    }


}