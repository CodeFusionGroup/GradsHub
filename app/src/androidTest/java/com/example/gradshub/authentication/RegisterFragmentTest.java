package com.example.gradshub.authentication;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.espresso.action.TypeTextAction;
import androidx.test.rule.ActivityTestRule;

import com.example.gradshub.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

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

    /*@Test
    public void UnitTesting()
    {
        RelativeLayout rlContainer = (RelativeLayout) mActivity.findViewById(R.id.containing_tests);
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
    }*/

    @After
    public void tearDown() throws Exception {
        mActivity = null;

        mActivity = null;
    }
}