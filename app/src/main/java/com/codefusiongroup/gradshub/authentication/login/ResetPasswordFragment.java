package com.codefusiongroup.gradshub.authentication.login;


import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationAPI;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


public class ResetPasswordFragment extends Fragment {

    private static final String TAG = "ResetPasswordFragment";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        EditText emailET = view.findViewById(R.id.emailET);
        Button resetPasswordBtn = view.findViewById(R.id.sendRequestBtn);

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailET.getText().toString().trim();

                if ( isValidInput(email, emailET) ) {
                    requestPasswordReset(email);
                }

            }
        });

    }


    private boolean isValidInput(String email, EditText emailET) {

        if ( email.isEmpty() ) {
            emailET.setError("Field can't be empty.");
            emailET.requestFocus();
            return false;
        }

        if ( !Patterns.EMAIL_ADDRESS.matcher(email).matches()  ) {
            emailET.setError("Please check that your email address is entered correctly.");
            emailET.requestFocus();
            return false;
        }

        return true;

    }


    private void requestPasswordReset(String email) {

        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        AuthenticationAPI authenticationAPI = ApiProvider.getAuthApiService();
        authenticationAPI.requestPasswordReset(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {
                        GradsHubApplication.showToast("An email has been sent to you to reset your password");

                        // navigate back to LoginFragment
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.authentication_nav_host_fragment);
                        navController.navigate(R.id.action_resetPasswordFragment_to_loginFragment);
                    }
                    else {
                        GradsHubApplication.showToast("incorrect email address.");
                    }

                }
                else {
                    Log.i(TAG, " requestPasswordReset() --> response.isSuccessful = false");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                GradsHubApplication.showToast(ApiResponseConstants.SERVER_FAILURE_MSG);
                t.printStackTrace();
            }

        });

    }

}
