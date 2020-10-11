package com.codefusiongroup.gradshub.authentication.registration;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.utils.validations.FormValidator;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.UserPreferences;
import com.codefusiongroup.gradshub.databinding.FragmentRegisterBinding;
import com.codefusiongroup.gradshub.utils.forms.RegisterForm;
import com.google.firebase.iid.FirebaseInstanceId;


public class RegisterFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static String TAG = "RegisterFragment";
    private FragmentRegisterBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // inflate view and obtain an instance of the binding class
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return  binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        binding.generateTokenBtn.setOnClickListener(v -> getUserToken());

        initialiseSpinner(binding.spinner);

        // set the life cycle owner as the RegisterFragment for data binding
        binding.setLifecycleOwner(this);

        // Obtain the RegisterViewModel component
        RegisterViewModel registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Assign the LoginViewModel component to a property (i.e setRegisterViewModel) in the binding class
        binding.setRegisterViewModel(registerViewModel);

        // bind error properties associated with registration form fields with the FormValidator
        binding.setFormValidator( FormValidator.getInstance() );

        // associate the binding object with the registration form
        RegisterForm.getInstance().setRegisterBinding(binding);

        // observe changes to live data objects in RegisterViewModel
        observeViewModel(registerViewModel);

        // ensure token is generated for user when they register
        getUserToken();

    }


    private void observeViewModel(RegisterViewModel viewModel) {

        viewModel.getTokenStatus().observe(getViewLifecycleOwner(), tokenGenerated -> {
            if (!tokenGenerated) {
                GradsHubApplication.showToast("Registration token not generated, click refresh token below or try again later.");
                binding.generateTokenBtn.setVisibility(View.VISIBLE);
            }
            else {
                binding.generateTokenBtn.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressCircular.setVisibility(View.VISIBLE);
            }
            else {
                binding.progressCircular.setVisibility(View.GONE);
            }
        });

        viewModel.getRegisterResponse().observe(getViewLifecycleOwner(), stringResource -> {
            if (stringResource != null) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.authentication_nav_host_fragment);
                navController.navigate(R.id.action_registerFragment_to_loginFragment);
                GradsHubApplication.showToast(stringResource.message);
            }
            else {
                Log.d(TAG, "stringResource is null");
            }
        });

    }


    private void getUserToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCompleteListener(task -> {

                if ( task.isSuccessful() ) {
                    Log.d(TAG, "task.isSuccessful() = true");
                    if (task.getResult() != null) {
                        binding.generateTokenBtn.setVisibility(View.GONE);
                        String token = task.getResult().getToken();
                        UserPreferences.getInstance().saveTokenState( token, requireActivity() );
                        GradsHubApplication.showToast("Token successfully generated, enter required fields to complete registration.");
                    }
                    else {
                        GradsHubApplication.showToast("Failed to generate registration token, please refresh page or try again later.");
                        Log.d(TAG, "task.getResult() is null");
                    }
                }
                else {
                    GradsHubApplication.showToast("Failed to generate registration token, please refresh page or try again later.");
                    Log.d(TAG, "getInstanceId() --> task.isSuccessful() = false", task.getException());
                }

            })
            .addOnFailureListener(e -> {
                GradsHubApplication.showToast("Failed to generate registration token, please refresh page or try again later.");
                Log.d( TAG, "FCM getInstanceId() --> onFailure() executed", e );
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
        // no implementation
    }


    // ==================== TESTING CODE ===================================
/*
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
*/

}
