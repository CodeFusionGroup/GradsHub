package com.example.gradshub.authentication;

import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.TypeTextAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.rule.ActivityTestRule;

import com.example.gradshub.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
//import static java.util.EnumSet.allOf;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;


/*
Important to note that travis CI uses IRM emulators, which makes instrumantation tests to take longer. So we will be avoiding them where possible.

 */
public class RegisterFragmentTest {

    @Rule
    public ActivityTestRule<FragmentRegisterActivity> mActivityTestRule = new ActivityTestRule<FragmentRegisterActivity>(FragmentRegisterActivity.class);

    private FragmentRegisterActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void TestingLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.registerfragment_testing_test);
        assertNotNull(rlContainer);
        RegisterFragment Fragment = new RegisterFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.registerLaunched);
        assertNotNull(view);
    }

    @Test
    public void TestLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.registerfragment_testing_test);
        assertNotNull(rlContainer);
        RegisterFragment Fragment = new RegisterFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.firstNameET);
        assertNotNull(view);
        View view1 = Fragment.getView().findViewById(R.id.lastNameET);
        assertNotNull(view1);
        View view2 = Fragment.getView().findViewById(R.id.emailET);
        assertNotNull(view2);
        View view3 = Fragment.getView().findViewById(R.id.phoneNumberET);
        assertNotNull(view3);
        View view4 = Fragment.getView().findViewById(R.id.academicStatusTV);
        assertNotNull(view4);
        View view5 = Fragment.getView().findViewById(R.id.spinner);
        assertNotNull(view5);
        View view6 = Fragment.getView().findViewById(R.id.passwordET);
        assertNotNull(view6);
        View view7 = Fragment.getView().findViewById(R.id.confirmNewPasswordET);
        assertNotNull(view7);
        View view8 = Fragment.getView().findViewById(R.id.submitBtn);
        assertNotNull(view8);
    }

    @Test
    public void testValidInput(){

        //Unit test
        RegisterFragment Fragment = new RegisterFragment();
		//All input is valid
        Fragment.setFirstName("Wits");
        Fragment.setLastName("University");
        Fragment.setAcademicStatus("Honors");
        Fragment.setEmail("stud@wits.ac.za");
        Fragment.setPassword("1234Abbssa");
        Fragment.setConfirmPassword("1234Abbssa");
        Fragment.setPhoneNumber("0123456789");

        assertTrue(Fragment.isValidInput());
    }
    
    
    @Test
    public void testInvalidInputFName(){
    	//All input is correct except first name
    	//RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.registerfragment_testing_test);
        RegisterFragment Fragment = new RegisterFragment();
        //mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(), Fragment).commitAllowingStateLoss();
        Fragment.setFirstName("");
        Fragment.setLastName("University");
        Fragment.setAcademicStatus("Honors");
        Fragment.setEmail("stud@wits.ac.za");
        Fragment.setPassword("1234Abbssa");
        Fragment.setConfirmPassword("1234Abbssa");
        Fragment.setPhoneNumber("0123456789");

        assertFalse(Fragment.isValidInput());

    
    }

	@Test
    public void testInvalidInputLName(){
        RegisterFragment Fragment = new RegisterFragment();
        Fragment.setFirstName("Wits");
        Fragment.setLastName("");
        Fragment.setAcademicStatus("Honors");
        Fragment.setEmail("stud@wits.ac.za");
        Fragment.setPassword("1234Abbssa");
        Fragment.setConfirmPassword("1234Abbssa");
        Fragment.setPhoneNumber("0123456789");

        assertFalse(Fragment.isValidInput());
    }

    @Test
    public void testInvalidInputAcadStatus(){
    	//All input is correct except academic status
        RegisterFragment Fragment = new RegisterFragment();
        Fragment.setFirstName("Wits");
        Fragment.setLastName("University");
        Fragment.setAcademicStatus("Select your academic status here");
        Fragment.setEmail("stud@wits.ac.za");
        Fragment.setPassword("1234Abbssa");
        Fragment.setConfirmPassword("1234Abbssa");
        Fragment.setPhoneNumber("0123456789");

        assertFalse(Fragment.isValidInput());

    }

    @Test
    public void testInvalidInputEmail(){
    	//All input is correct except email
        RegisterFragment Fragment = new RegisterFragment();
        Fragment.setFirstName("Wits");
        Fragment.setLastName("University");
        Fragment.setAcademicStatus("Honors");
        Fragment.setEmail("");
        Fragment.setPassword("1234Abbssa");
        Fragment.setConfirmPassword("1234Abbssa");
        Fragment.setPhoneNumber("0123456789");

        assertFalse(Fragment.isValidInput());
    }

    @Test
    public void testInvalidInputPassword(){
    	//All input is correct except password
        RegisterFragment Fragment = new RegisterFragment();
        Fragment.setFirstName("Wits");
        Fragment.setLastName("University");
        Fragment.setAcademicStatus("Honors");
        Fragment.setEmail("stud@wits.ac.za");
        Fragment.setPassword("");
        Fragment.setConfirmPassword("1234Abbssa");
        Fragment.setPhoneNumber("0123456789");

        assertFalse(Fragment.isValidInput());
    }
	
    @Test
    public void testInvalidInputPasswordNotMatching(){
        RegisterFragment Fragment = new RegisterFragment();
        Fragment.setFirstName("Wits");
        Fragment.setLastName("University");
        Fragment.setAcademicStatus("Honors");
        Fragment.setEmail("stud@wits.ac.za");
        Fragment.setPassword("1234Abbssa1");
        Fragment.setConfirmPassword("1234Abbssa");
        Fragment.setPhoneNumber("0123456789");

        assertFalse(Fragment.isValidInput());
    }
	

    @Test
    public void testInvalidInputPhoneNumber(){
    	//All input is correct except phone number is invalid
        RegisterFragment Fragment = new RegisterFragment();
        Fragment.setFirstName("Wits");
        Fragment.setLastName("University");
        Fragment.setAcademicStatus("Honors");
        Fragment.setEmail("stud@wits.ac.za");
        Fragment.setPassword("1234Abbssa1");
        Fragment.setConfirmPassword("1234Abbssa");
        Fragment.setPhoneNumber("");

        assertFalse(Fragment.isValidInput());
    }
    
    

    //User interface testing, slows down travis buid.
    //Possible solution: Use Jenkins(alternate CI) or use googles firebase(Good emulators)
    /*@Test
    public void UnitTesting() {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.registerfragment_testing_test);
        assertNotNull(rlContainer);
        RegisterFragment Fragment = new RegisterFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(), Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        //View view = Fragment.getView().findViewById(R.id.firstNameET);
        //assertNotNull(view);
        ViewInteraction view = onView(withId(R.id.firstNameET));
        view.perform(ViewActions.typeText("Tshepo"));

        //View view1 = Fragment.getView().findViewById(R.id.lastNameET);
        //assertNotNull(view1);
        ViewInteraction view1 = onView(withId(R.id.lastNameET));
        view1.perform(ViewActions.typeText("Kgatle"));

        //View view2 = Fragment.getView().findViewById(R.id.emailET);
        //assertNotNull(view2);
        ViewInteraction view2 = onView(withId(R.id.emailET));
        view2.perform(ViewActions.typeText("Tshepo@gmail.com"));

        //View view3 = Fragment.getView().findViewById(R.id.phoneNumberET);
        //assertNotNull(view3);
        ViewInteraction view3 = onView(withId(R.id.phoneNumberET));
        view3.perform(ViewActions.typeText("0799732867"));

        //View view4 = Fragment.getView().findViewById(R.id.academicStatusTV);
        //assertNotNull(view4);
        ViewInteraction view4 = onView(withId(R.id.academicStatusTV));
        assertNotNull(view4);

        //View view5 = Fragment.getView().findViewById(R.id.spinner);
        //assertNotNull(view5);
        //ViewInteraction view5 = onView(withId(R.id.lastNameET));
        //view5.perform(ViewActions.typeText("Kgatle"));
        onView(withId(R.id.spinner)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("PhD"))).perform(click());
        onView(withId(R.id.spinner)).check(matches(withSpinnerText(containsString("PhD"))));
        //ViewInteraction customTextView = onView(allOf(withId(R.id.spinner), withText("PhD"), isDisplayed()));
        //customTextView.perform(click());

        //View view6 = Fragment.getView().findViewById(R.id.passwordET);
        //assertNotNull(view6);
        ViewInteraction view6 = onView(withId(R.id.passwordET));
        view6.perform(ViewActions.typeText("mmmm1234"));

        //View view7 = Fragment.getView().findViewById(R.id.confirmNewPasswordET);
        //assertNotNull(view7);
        ViewInteraction view7 = onView(withId(R.id.confirmNewPasswordET));
        view7.perform(ViewActions.typeText("mmmm1234"));

        //View view8 = Fragment.getView().findViewById(R.id.submitBtn);
        //assertNotNull(view8);
        ViewInteraction view8 = onView(withId(R.id.submitBtn));
        view8.perform(click());
    }*/

    @After
    public void tearDown() throws Exception {
        mActivity = null;

        mActivity = null;
    }
}
