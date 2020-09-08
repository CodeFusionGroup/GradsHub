package com.codefusiongroup.gradshub.authentication.registration;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class RegisterFragment extends Fragment implements RegisterContract.IRegisterView, AdapterView.OnItemSelectedListener {


    private static String TAG = "RegisterFragment"; // for debugging

    private ProgressBar mProgressBar;
    private Spinner mSpinner;
    private EditText mFirstNameET, mLastNameET, mEmailET, mPhoneNoET, mPasswordET, mConfirmPasswordET;
    private Button mSubmitBtn;

    private String mFirstName, mLastName, mEmail, mPhoneNo, mAcademicStatus, mPassword, mConfirmPassword;
    private RegisterPresenter mRegisterPresenter;

    // Used for Firebase Cloud Messaging
    private String mToken;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegisterPresenter = new RegisterPresenter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        initViewComponents(view);
        initialiseSpinner(mSpinner);

        mRegisterPresenter.subscribe((RegisterContract.IRegisterView) this);
        Log.i(TAG, "register presenter has subscribed to RegisterFragment");

        mSubmitBtn.setOnClickListener(v -> {

            // must be passed inside onClick so that isValidInput() doesn't validate empty fields and that no empty fields
            // are used to register a user.
            mFirstName = mFirstNameET.getText().toString().trim();
            mLastName = mLastNameET.getText().toString().trim();
            mEmail = mEmailET.getText().toString().trim();
            mPhoneNo = mPhoneNoET.getText().toString().trim();
            mAcademicStatus = mSpinner.getSelectedItem().toString();
            mPassword = mPasswordET.getText().toString().trim();
            mConfirmPassword = mConfirmPasswordET.getText().toString().trim();

            if ( mRegisterPresenter.validateRegistrationInput(mFirstName, mLastName, mEmail, mPhoneNo, mAcademicStatus, mPassword, mConfirmPassword) ) {
                // Register User with FCM Token
                registrationWithToken();
            }

        });

    }


    private void initViewComponents(View view) {

        mProgressBar = view.findViewById(R.id.progress_circular);
        mSpinner = view.findViewById(R.id.spinner);
        mFirstNameET = view.findViewById(R.id.firstNameET);
        mLastNameET = view.findViewById(R.id.lastNameET);
        mEmailET = view.findViewById(R.id.emailET);
        mPhoneNoET = view.findViewById(R.id.phoneNumberET);
        mPasswordET = view.findViewById(R.id.passwordET);
        mConfirmPasswordET = view.findViewById(R.id.confirmNewPasswordET);
        mSubmitBtn = view.findViewById(R.id.submitBtn);

    }

    // Retrieve the FCM registration token
    public void registrationWithToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        mToken = task.getResult().getToken();
                        Log.d(TAG, mToken);

                        //Register User
                        mRegisterPresenter.registerUser( new User( mFirstName, mLastName, mEmail, mPhoneNo, mAcademicStatus, mPassword, mToken ) );

                        //Log and toast
//                        String msg = getString(R.string.msg_token_fmt, mToken);
//                        Log.d(TAG, mToken);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // this part of the code allows us to create a spinner with a drop down list of items we want to display to the user to select.
    @VisibleForTesting
    private void initialiseSpinner(Spinner spinner) {

        // style and populate spinner.
        ArrayAdapter<CharSequence> academicStatusAdapter = ArrayAdapter.createFromResource(requireContext(),R.array.ACADEMIC_STATUS,android.R.layout.simple_spinner_item);
        // spinner will have drop down layout style.
        academicStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching adapter (academicStatusAdapter) to spinner.
        spinner.setAdapter(academicStatusAdapter);

        spinner.setOnItemSelectedListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String text = parent.getItemAtPosition(position).toString();
        TextView tv = (TextView) view;

        if (position == 0) {
            // the first item (which by default is the displayed item) is set to gray to indicate its not part of the selection.
            tv.setTextColor(Color.rgb(112,128,144));
        }
        else {
            // the font colour for valid items once selected, is set to black when displayed after being selected.
            tv.setTextColor(Color.BLACK);
        }

        // if the user has selected a valid item from the drop down list, then display a short confirm message of the selected item.
        if (position > 0) {
            Toast.makeText(parent.getContext(), "selected " + text, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // empty
    }


    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void showFirstNameInputError(String message) {
        mFirstNameET.setError(message);
        mFirstNameET.requestFocus();
    }


    @Override
    public void showLastNameInputError(String message) {
        mLastNameET.setError(message);
        mLastNameET.requestFocus();
    }


    @Override
    public void showEmailInputError(String message) {
        mEmailET.setError(message);
        mEmailET.requestFocus();
    }


    @Override
    public void showPhoneNoInputError(String message) {
        mPhoneNoET.setError(message);
        mPhoneNoET.requestFocus();
    }


    @Override
    public void showAcademicStatusInputError(String message) {

        TextView errorText = (TextView) mSpinner.getSelectedView();
        errorText.setError("");
        errorText.setTextColor(Color.RED); // just to highlight that this is an error message, we display it in red.
        errorText.setText(R.string.spinnerErrorMsg); // changes the selected item text to this text.
        mSpinner.requestFocus();

    }


    @Override
    public void showPasswordInputError(String message) {
        mPasswordET.setError(message);
        mPasswordET.requestFocus();
    }


    @Override
    public void showPasswordConfirmationError(String message) {
        mConfirmPasswordET.setError(message);
        mConfirmPasswordET.requestFocus();
    }


    @Override
    public void showRegistrationResponseMsg(String message) {
        // requireActivity() will work fine here
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show();
    }


    @Override
    public void navigateToLogin() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.authentication_nav_host_fragment);
        navController.navigate(R.id.action_registerFragment_to_loginFragment);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    // ==================== TESTING CODE ===================================
    // setters for testing purposes

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNo = phoneNumber;
    }

    public void setAcademicStatus(String academicStatus) {
        mAcademicStatus = academicStatus;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public void setConfirmPassword(String confirmPassword) { mConfirmPassword = confirmPassword; }

    // =====================================================================


}
