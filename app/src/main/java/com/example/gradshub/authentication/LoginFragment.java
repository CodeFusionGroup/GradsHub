package com.example.gradshub.authentication;

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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.gradshub.R;
import com.example.gradshub.main.MainActivity;
import com.example.gradshub.model.User;
import com.example.gradshub.network.NetworkRequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private ProgressBar progressBar;
    private EditText emailET, passwordET;

    private String email, password;
    private User user;


    // called to do initial creation of a fragment.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    // called to have the fragment instantiate its user interface view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        NavController navController = Navigation.findNavController(requireActivity(), R.id.authentication_nav_host_fragment);;
        switch ( v.getId() ) {

            case R.id.loginBtn:
                email = emailET.getText().toString().trim();
                password = passwordET.getText().toString().trim();
                if ( isValidInput() ) {
                    requestLogin(email, password);
                    progressBar.setVisibility(View.VISIBLE);
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


    private boolean isValidInput() {

        if (email.isEmpty()) {
            emailET.setError("Not a valid email address!");
            emailET.requestFocus();
            return false;
        }

        // if an email is entered, check that the entered email address has patterns similar to that of a
        // typical email address.
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

        String url = "https://gradshub.herokuapp.com/api/User/login.php";
        HashMap<String, String>  params = new HashMap<>();
        // the POST parameters:
        params.put("email", email);
        params.put("password", password);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverLoginResponse( response );
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // means something went wrong when contacting server. Just display message indicating
                        // to user to try again
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                } );
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


    private void serverLoginResponse(JSONObject response) {

        try {

            String statusCode = response.getString("success");

            switch (statusCode) {

                // login successful
                case "1":
                    JSONObject userJO = response.getJSONObject("message");

                    // get user details.
                    String userId = userJO.getString("USER_ID");
                    String firstName = userJO.getString("USER_FNAME");
                    String lastName = userJO.getString("USER_LNAME");
                    String email = userJO.getString("USER_EMAIL");
                    String phoneNumber = userJO.getString("USER_PHONE_NO");
                    String academicStatus = userJO.getString("USER_ACAD_STATUS");

                    // initialise user with fields.
                    // we use the default constructor since user's password is not requested and
                    // userID is one given by the DB when a new User record is created
                    user = new User();
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(email);
                    user.setPhoneNumber(phoneNumber);
                    user.setAcademicStatus(academicStatus);
                    user.setUserID(userId);

                    // Ensures the fragment is added (testing)
                    if( isAdded() ) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "Successfully logged in.", Toast.LENGTH_SHORT).show();
                        startMainActivity();
                    }
                    break;

                // login unsuccessful (incorrect login credentials)
                case "0":
                case "-1":
                    // Ensures the fragment is added (testing)
                    if( isAdded() ) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
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
    }

}
