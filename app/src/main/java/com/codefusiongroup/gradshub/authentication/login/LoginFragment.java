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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.User;


public class LoginFragment extends Fragment implements LoginContract.ILoginView, View.OnClickListener {


    private static String TAG = "LoginFragment"; // for debugging

    private AppCompatActivity mActivity; // used as context in intent to start MainActivity
    private ProgressBar mProgressBar;
    private EditText mEmailET, mPasswordET;

    private User mUser;
    private LoginPresenter mLoginPresenter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity)
            mActivity = (AppCompatActivity) context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginPresenter = new LoginPresenter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        initViewComponents(view);
        mLoginPresenter.subscribe(LoginFragment.this);
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

                if ( mLoginPresenter.validateLoginInput(email, password) ) {
                    mLoginPresenter.loginUser(email, password);
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
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void hideProgressBar() {
        //TODO: fix progress bar not disappearing
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
    public void showLoginResponseMsg(String message) { GradsHubApplication.showToast(message); }


    @Override
    public void initialiseUser(User user) { mUser = user; }


    @Override
    public void startMainActivity() {
        Intent intent = new Intent( mActivity, MainActivity.class );
        intent.putExtra("USER", mUser);
        mActivity.startActivity(intent);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


}
