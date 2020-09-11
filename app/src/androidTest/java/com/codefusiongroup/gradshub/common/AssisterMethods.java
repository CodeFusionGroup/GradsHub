package com.codefusiongroup.gradshub.common;

import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.contrib.DrawerActions;

import com.codefusiongroup.gradshub.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.codefusiongroup.gradshub.authentication.AuthenticationActivityTest.waitForResources;

//Assister methods will contain methods that can be used in many tests
public class AssisterMethods {

    //Keeps the user state, if the user is logged in, state will be zero else 1.
    public static int state = 0;
    private static int recursiveCounter = 0;
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

    public static void logUserOut() throws InterruptedException {
        try {
            openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
            onView(withText("logout")).perform(click());
        }
        catch (PerformException e){
            //recursive counter avoids stucking in an infinite recursive call if the view is nor available
            if(recursiveCounter < 1) {
                ++recursiveCounter;
                waitForResources(5000);
                logUserOut();
            }
            else throw e;
        }
        }


}
