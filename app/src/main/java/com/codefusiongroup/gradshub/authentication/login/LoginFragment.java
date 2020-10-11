package com.codefusiongroup.gradshub.authentication.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.UserPreferences;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.databinding.FragmentLoginBinding;
import com.codefusiongroup.gradshub.utils.validations.FormValidator;
import com.codefusiongroup.gradshub.utils.forms.LoginForm;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private static String TAG = "LoginFragment";

    private FragmentLoginBinding binding;
    private UserPreferences mUserPreferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserPreferences = UserPreferences.getInstance();
        User user = mUserPreferences.getUserState( requireActivity() );
        if ( user.isLoggedIn() ) {
            startMainActivity(user);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate view and obtain an instance of the binding class
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return  binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        binding.forgotPasswordBtn.setOnClickListener(this);
        binding.registerBtn.setOnClickListener(this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set the life cycle owner as the LoginFragment for data binding
        binding.setLifecycleOwner(this);

        // Obtain the LoginViewModel component
        LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Assign the LoginViewModel component to a property (i.e setLoginViewModel) in the binding class
        binding.setLoginViewModel(loginViewModel);

        // bind error properties associated with login form fields with the FormValidator
        binding.setFormValidator( FormValidator.getInstance() );

        // associate the binding object with the login form
        LoginForm.getInstance().setLoginBinding(binding);

        // observe changes to live data objects in LoginViewModel
        observeViewModel(loginViewModel);

    }


    private void observeViewModel(LoginViewModel viewModel) {

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressCircular.setVisibility(View.VISIBLE);
            }
            else {
                binding.progressCircular.setVisibility(View.GONE);
            }
        });

        viewModel.getLoginResponse().observe(getViewLifecycleOwner(), userResource -> {

            if (userResource != null) {

                switch (userResource.status) {

                    case API_DATA_SUCCESS:
                        if (userResource.data != null) {
                            mUserPreferences.saveUserState( userResource.data, requireActivity() );
                            startMainActivity(userResource.data);
                            GradsHubApplication.showToast(userResource.message);
                            viewModel.onLoginSuccessful(); // set live data object to null
                        }
                        else {
                            Log.d(TAG, "userResource.data is null");
                        }
                        break;

                    case ERROR:
                    case API_NON_DATA_SUCCESS:
                        GradsHubApplication.showToast(userResource.message);
                        break;
                }

            }
            else {
                Log.d(TAG, "userResource is null");
            }

        });

    }


    @Override
    public void onClick(View v) {

        NavController navController = Navigation.findNavController(requireActivity(), R.id.authentication_nav_host_fragment);
        switch ( v.getId() ) {
            case R.id.registerBtn:
                navController.navigate(R.id.action_loginFragment_to_registerFragment);
                break;

            case R.id.forgotPasswordBtn:
                navController.navigate(R.id.action_loginFragment_to_resetPasswordFragment);
                break;
        }

    }


    private void startMainActivity(User user) {
        Intent intent = new Intent( requireActivity(), MainActivity.class );
        intent.putExtra("user", user);
        requireActivity().startActivity(intent);
    }

}
