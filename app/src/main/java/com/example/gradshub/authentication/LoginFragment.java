package com.example.gradshub.authentication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.gradshub.R;
import com.example.gradshub.main.ui.MainActivity;
import com.example.gradshub.model.User;
import com.example.gradshub.network.AsyncHTTpPost;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private ProgressBar progressBar;
    private String email, password;
    private EditText emailET, passwordET;
    private User user = new User();


    // called to do initial creation of a fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    // called to have the fragment instantiate its user interface view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    // called after the fragment has instantiated its user interface view.
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        progressBar = view.findViewById(R.id.progress_circular);
        emailET = view.findViewById(R.id.emailET);
        passwordET = view.findViewById(R.id.passwordET);

        Button loginBtn = view.findViewById(R.id.loginBtn);
        Button registerBtn = view.findViewById(R.id.registerBtn);
        Button forgotPasswordBtn = view.findViewById(R.id.forgotPasswordBtn);

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        forgotPasswordBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        NavController navController;
        switch (v.getId()) {
            case R.id.loginBtn:
                email = emailET.getText().toString().trim();
                password = passwordET.getText().toString().trim();
                if (isValidInput()) {
                    requestLogin(email, password);
                    progressBar.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.registerBtn:
                navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_loginFragment_to_registerFragment);
                break;

            case R.id.forgotPasswordBtn:
                navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_loginFragment_to_resetPasswordFragment);
                break;
        }
    }


    private boolean isValidInput() {
        if (email.isEmpty()) {
            emailET.setError("Not a valid email address!");
            emailET.requestFocus();
            return false;
        }
        // if an email is entered, check that the entered email address has patterns similar to that of a typical email address.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("your email address is not entered correctly!");
            emailET.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwordET.setError("Not a valid password!");
            passwordET.requestFocus();
            return false;
        }

        return true;
    }


    private void requestLogin(String email, String password) {
        ContentValues params = new ContentValues();
        params.put("USER_EMAIL", email);
        params.put("USER_PASSWORD", password);

        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/login.php", params) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(String output) {
                serverLoginResponse(output);
            }

        };
        asyncHttpPost.execute();
    }


    private void serverLoginResponse(String output) {
        try {
            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");

            switch (success) {
                // login successful
                case "1":
                    JSONObject jsonObject = jo.getJSONObject("message");

                    // get user details.
                    String userId = jsonObject.getString("USER_ID");
                    String firstName = jsonObject.getString("USER_FNAME");
                    String lastName = jsonObject.getString("USER_LNAME");
                    String email = jsonObject.getString("USER_EMAIL");
                    String phoneNumber = jsonObject.getString("USER_PHONE_NO");
                    String academicStatus = jsonObject.getString("USER_ACAD_STATUS");

                    // initialise fields for the user.
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(email);
                    user.setPhoneNumber(phoneNumber);
                    user.setAcademicStatus(academicStatus);
                    user.setUserID(userId);

                    progressBar.setVisibility(View.GONE);
                    startMainActivity();
                    Toast.makeText(requireActivity(), "Successfully logged in!", Toast.LENGTH_SHORT).show();
                    break;

                // login unsuccessful
                case "0":
                case "-1":
                    Toast.makeText(requireActivity(), jo.getString("message"), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.putExtra("USER", user);
        startActivity(intent);
        // prevent user from navigating back to login screen once they have logged in.
        requireActivity().finish();
    }

}
