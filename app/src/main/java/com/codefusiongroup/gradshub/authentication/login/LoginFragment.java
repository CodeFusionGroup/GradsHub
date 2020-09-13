package com.codefusiongroup.gradshub.authentication.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.BaseView;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.UserPreferences;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.messaging.chatMessages.ChatMessagesPresenter;


public class LoginFragment extends Fragment implements BaseView<LoginPresenter>, LoginContract.ILoginView, View.OnClickListener {


    private static String TAG = "LoginFragment"; // for debugging

    private AppCompatActivity mActivity; // used as context in intent to start MainActivity and Preferences
    private ProgressBar mProgressBar;
    private EditText mEmailET, mPasswordET;

    private User mUser;
    private LoginPresenter mPresenter;
    private UserPreferences mUserPreferences;
    private View mView;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity)
            mActivity = (AppCompatActivity) context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPresenter( new LoginPresenter() );
        mUserPreferences = UserPreferences.getInstance();
        if (mUserPreferences.isLoggedIn( requireActivity() ) ) {
            mUser = mUserPreferences.getUserDetails( requireActivity() );
            startMainActivity();
        }

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login, container, false);
        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        initViewComponents(view);
        mPresenter.subscribe(LoginFragment.this);
        Log.i(TAG, "login presenter has subscribed to view --> LoginFragment");
    }


    public void initViewComponents(View view) {

        mProgressBar = view.findViewById(R.id.progress_circular);
        mEmailET = view.findViewById(R.id.emailET);
        mPasswordET = view.findViewById(R.id.passwordET);

        Button loginBtn = view.findViewById(R.id.loginBtn);
        Button registerBtn = view.findViewById(R.id.registerBtn);
        Button forgotPasswordBtn = view.findViewById(R.id.forgotPasswordBtn);

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        forgotPasswordBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        NavController navController = Navigation.findNavController(requireActivity(), R.id.authentication_nav_host_fragment);

        switch ( v.getId() ) {

            case R.id.loginBtn:

                String email = mEmailET.getText().toString().trim();
                String password = mPasswordET.getText().toString().trim();

                if ( mPresenter.validateLoginInput(email, password) ) {
                    mPresenter.loginUser(email, password);
                }

                break;

            case R.id.registerBtn:
                navController.navigate(R.id.action_loginFragment_to_registerFragment);
                break;

            case R.id.forgotPasswordBtn:
                navController.navigate(R.id.action_loginFragment_to_resetPasswordFragment);
                break;
        }

    }


    @Override
    public void setPresenter(LoginPresenter presenter) {
        mPresenter = presenter;
    }


    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void hideProgressBar() {
        //TODO: fix progress bar not disappearing if login credentials are incorrect
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void showEmailInputError(String message) {
        mEmailET.setError(message);
        mEmailET.requestFocus();
    }


    @Override
    public void showPasswordInputError(String message) {
        mPasswordET.setError(message);
        mPasswordET.requestFocus();
    }


    @Override
    public void showLoginResponseMsg(String message) {
        GradsHubApplication.showToast(message); }


    @Override
    public void initialiseUser(User user) {
        Log.i(TAG, "initialiseUser() executed");
        mUser = user;
        mUserPreferences.saveUserDetails( user, mActivity );
        mUserPreferences.setLogInState( mActivity );
    }


    @Override
    public void startMainActivity() {
        Log.i(TAG, "startMainActivity() executed");
        Intent intent = new Intent( mActivity, MainActivity.class );
        intent.putExtra("USER", mUser);
        mActivity.startActivity(intent);
    }


    @Override
    public void onDetach() {

        super.onDetach();
        //mPresenter.unsubscribe();
        //Log.i(TAG, "login presenter has unsubscribed from view --> LoginFragment");
    }

}
