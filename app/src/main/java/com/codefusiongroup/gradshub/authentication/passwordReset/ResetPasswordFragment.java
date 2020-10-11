package com.codefusiongroup.gradshub.authentication.passwordReset;


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
import com.codefusiongroup.gradshub.databinding.FragmentResetPasswordBinding;
import com.codefusiongroup.gradshub.utils.validations.FormValidator;
import com.codefusiongroup.gradshub.utils.forms.ResetPasswordForm;


public class ResetPasswordFragment extends Fragment {

    private static final String TAG = "ResetPasswordFragment";
    private FragmentResetPasswordBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate view and obtain an instance of the binding class
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false);
        return  binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        // set the life cycle owner as the ResetPasswordFragment for data binding
        binding.setLifecycleOwner(this);

        // Obtain the ResetPasswordViewModel component
        ResetPasswordViewModel resetPasswordViewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);

        // Assign the ResetPasswordViewModel component to a property (i.e setResetPasswordViewModel) in the binding class
        binding.setResetPasswordViewModel(resetPasswordViewModel);

        // bind error properties associated with login form fields with the FormValidator
        binding.setFormValidator( FormValidator.getInstance() );

        // associate the binding object with the reset password form
        ResetPasswordForm.getInstance().setResetPasswordBinding(binding);

        // observe changes to live data objects in ResetPasswordViewModel
        observeViewModel(resetPasswordViewModel);

    }


    private void observeViewModel(ResetPasswordViewModel viewModel) {

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressCircular.setVisibility(View.VISIBLE);
            }
            else {
                binding.progressCircular.setVisibility(View.GONE);
            }
        });

        viewModel.getPasswordResetResponse().observe(getViewLifecycleOwner(), stringResource -> {
            if (stringResource != null) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.authentication_nav_host_fragment);
                navController.navigate(R.id.action_resetPasswordFragment_to_loginFragment);
                GradsHubApplication.showToast(stringResource.message);
                viewModel.onRequestResetSuccessful(); // set live data object to null
            }
            else {
                Log.d(TAG, "stringResource is null");
            }
        });

    }

}
