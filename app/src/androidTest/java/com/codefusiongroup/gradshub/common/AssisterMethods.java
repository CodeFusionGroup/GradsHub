package com.codefusiongroup.gradshub.common;

import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.espresso.util.TreeIterables;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.codefusiongroup.gradshub.R;

import org.hamcrest.Matcher;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.codefusiongroup.gradshub.authentication.AuthenticationActivityTest.waitForResources;

//Assister methods will contain methods that can be used in many tests
public class AssisterMethods {

    private static String username = new String("testuser@gmail.com");
    private static String password = new String("simple1");
    //Keeps the user state, if the user is logged in, state will be zero else 1.
    public static int state = 0;
    private static int recursiveCounter = 0;

    public static void logInUser() throws InterruptedException, UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        onView(withId(R.id.emailET)).perform(typeText(username));
        closeSoftKeyboard();
        onView(withId(R.id.passwordET)).perform(typeText(password));
        closeSoftKeyboard();
        //onView(withId(R.id.loginBtn)).perform(click());
        //waitForResources(4000);
        UiObject loginButton = device.findObject(new UiSelector().text("LOGIN").className("android.widget.Button"));
        loginButton.clickAndWaitForNewWindow();
        onView(isRoot()).perform(waitId(R.id.toolbar, TimeUnit.SECONDS.toMillis(15)));

    }

    public static void logUserOut(){
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
        onView(withText("Logout")).perform(click());
    }


    //ToDo:Add error checkers to avoid opening a drawer when it is not visible
    public static void openDrawer(){
        //Reopen drawer
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());
    }

    //Custom espresso click
    public static GeneralClickAction clickAt(final float pctX, final float pctY){
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {
                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);
                        int w = view.getWidth();
                        int h = view.getHeight();

                        float x = w * pctX;
                        float y = h * pctY;

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX,screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }

    //Better version of wait for resources implementation
    public static ViewAction waitId(final int viewId, final long millis){
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for a specific view with id <" + viewId +"> during " + millis + "millis.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);

                do{
                    for(View child : TreeIterables.breadthFirstViewTraversal(view)){
                        //Found view with required ID
                        if(viewMatcher.matches(child)){
                            return;
                        }
                    }
                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                //Timeout happens
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }

}