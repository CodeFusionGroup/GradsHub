package com.example.gradshub.authentication;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.example.gradshub.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;


public class RegisterFragmentTest {

    @Rule
    public ActivityTestRule<FragmentRegisterActivity> mActivityTestRule = new ActivityTestRule<FragmentRegisterActivity>(FragmentRegisterActivity.class);

    private FragmentRegisterActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    //Register screen can succesfully launch
    @Test
    public void RegisterFragmentLaunched()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.registerfragment_testing_test);
        assertNotNull(rlContainer);
        RegisterFragment Fragment = new RegisterFragment();
        mActivity.getSupportFragmentManager().beginTransaction().add(rlContainer.getId(),Fragment).commitAllowingStateLoss();
        getInstrumentation().waitForIdleSync();
        View view = Fragment.getView().findViewById(R.id.registerLaunched);
        assertNotNull(view);
    }

    //Register screen contents successfullly load
    @Test
    public void RegisterFragmentLaunching()
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

    //Register user

    @Test
    public void testRegisterUser(){
        //try {
            //Create a TestNavHostController
            TestNavHostController navController = new TestNavHostController(
                    ApplicationProvider.getApplicationContext());
            navController.setGraph(R.navigation.authentication_navigation);
            FragmentScenario<RegisterFragment> fragment = FragmentScenario.launchInContainer(RegisterFragment.class);
            fragment.onFragment(fragment1 -> Navigation.setViewNavController(fragment1.requireView(), navController));
            ViewInteraction view = onView(withId(R.id.firstNameET));
            view.perform(ViewActions.typeText("f_name"));
            ViewInteraction view1 = onView(withId(R.id.lastNameET));
            view1.perform(ViewActions.typeText("lname"));
            ViewInteraction view2 = onView(withId(R.id.emailET));
            view2.perform(ViewActions.typeText("tester141414fhfvbd@gmail.com"));
            closeSoftKeyboard();
            onView(withId(R.id.phoneNumberET)).perform(typeText(String.valueOf("123456789")));
            closeSoftKeyboard();
            onView(withId(R.id.spinner)).perform(click());
            onView(allOf(withText("Honours"))).perform(click());
            ViewInteraction view4 = onView(withId(R.id.passwordET));
            view4.perform(ViewActions.typeText("aBC123xyZ!"));
            closeSoftKeyboard();
            ViewInteraction view5 = onView(withId(R.id.confirmNewPasswordET));
            view5.perform(ViewActions.typeText("aBC123xyZ!"));
            closeSoftKeyboard();


            //ViewInteraction view6 = onView(withId(R.id.submitBtn));
            //view6.perform(click());
            onView(ViewMatchers.withId(R.id.submitBtn)).perform(scrollTo(), ViewActions.click());

        //}
        //catch (PerformException e) {
            /*Error loading login fragment, success registration*/
            //assertTrue("Test succeeded", true);
        //}

    }


    //Unit tests for register fragment
    //Different inputs validation testing
    @Test
    public void testValidInput(){
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

    @Test
    public void testValidInputPhD(){

        //Unit test
        RegisterFragment Fragment = new RegisterFragment();
        //All input is valid
        Fragment.setFirstName("weWits");
        Fragment.setLastName("weUniversity");
        Fragment.setAcademicStatus("PhD");
        Fragment.setEmail("westud@wits.ac.za");
        Fragment.setPassword("1313lnln");
        Fragment.setConfirmPassword("1313lnln");
        Fragment.setPhoneNumber("0123456799");

        assertTrue(Fragment.isValidInput());
    }

    @Test
    public void testValidInputMasters(){

        //Unit test
        RegisterFragment Fragment = new RegisterFragment();
        //All input is valid
        Fragment.setFirstName("IamWits");
        Fragment.setLastName("IamUniversity");
        Fragment.setAcademicStatus("Masters");
        Fragment.setEmail("iam@wits.ac.za");
        Fragment.setPassword("1212nmnm");
        Fragment.setConfirmPassword("1212nmnm");
        Fragment.setPhoneNumber("0123456788");

        assertTrue(Fragment.isValidInput());
    }
    


    @After
    public void tearDown() throws Exception {
        mActivity = null;

    }
}
