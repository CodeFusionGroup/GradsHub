package com.example.gradshub.authentication;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Patterns;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.gradshub.R;
import com.example.gradshub.model.User;
import com.example.gradshub.network.NetworkRequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class RegisterFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private ProgressBar progressBar;
    private Spinner spinner;
    private EditText firstNameET, lastNameET, emailET, phoneNumberET, passwordET, confirmPasswordET;

    private String firstName, lastName, email, phoneNumber, academicStatus, password, confirmPassword;


    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        progressBar = view.findViewById(R.id.progress_circular);
        spinner = view.findViewById(R.id.spinner);
        initialiseSpinner(spinner);

        firstNameET = view.findViewById(R.id.firstNameET);
        lastNameET = view.findViewById(R.id.lastNameET);
        emailET = view.findViewById(R.id.emailET);
        phoneNumberET = view.findViewById(R.id.phoneNumberET);
        passwordET = view.findViewById(R.id.passwordET);
        confirmPasswordET = view.findViewById(R.id.confirmNewPasswordET);
        Button submitBtn = view.findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // must be passed inside onClick so that isValidInput() doesn't validate empty fields and that no empty fields
                // are used to register a user.
                firstName = firstNameET.getText().toString().trim();
                lastName = lastNameET.getText().toString().trim();
                email = emailET.getText().toString().trim();
                phoneNumber = phoneNumberET.getText().toString().trim();
                academicStatus = spinner.getSelectedItem().toString();
                password = passwordET.getText().toString().trim();
                confirmPassword = confirmPasswordET.getText().toString().trim();

                if ( isValidInput() ) {
                    registerUser( new User( firstName, lastName, email, phoneNumber, academicStatus, password ) );
                    progressBar.setVisibility(View.VISIBLE);
                }

            }
        });

    }


    // this part of the code allows us to create a spinner with a drop down list of items we want to display to the user to select.
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
    public void onNothingSelected(AdapterView<?> parent) {}


    public boolean isValidInput() {

        if (firstName.isEmpty()) {
            try {
                firstNameET.setText("");
                firstNameET.setError("Not a valid first name!");
                firstNameET.requestFocus();
            }
            catch (Exception e) {
                // This helps for testing purposes
            }
            return false;
        }

        if (lastName.isEmpty()) {
            try {
                lastNameET.setError("Not a valid last name!");
                lastNameET.requestFocus();
            }
            catch(Exception e) {
                // This helps for testing purposes
            }
            return false;
        }

        if (email.isEmpty()) {
            try {
                emailET.setError("Not a valid email address!");
                emailET.requestFocus();
            }
            catch (Exception e) {
                // This helps for testing purposes
            }
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            try {
                emailET.setError("check that your email address is entered correctly!");
                emailET.requestFocus();
            }
            catch(Exception e) {
                // This helps for testing purposes
            }
            return false;
        }

        if (phoneNumber.isEmpty()) {
            try {
                phoneNumberET.setError("Not a valid phone number!");
                phoneNumberET.requestFocus();
            }
            catch(Exception e) {
                // This helps for testing purposes
            }
            return false;
        }

        // check that the user selected a valid academic status from the list (Honours, Masters, PhD).
        String placeHolder = "Select your academic status here"; // first item is invalid (used as hint)
        if (academicStatus.equals(placeHolder)) {

            try {
                TextView errorText = (TextView) spinner.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED); // just to highlight that this is an error message, we display it in red.
                errorText.setText(R.string.spinnerErrorMsg); // changes the selected item text to this text.
                spinner.requestFocus();
            }
            catch(Exception e) {
                // This helps for testing purposes
            }
            return false;

        }

        if (password.isEmpty()) {
            try {
                passwordET.setError("Not a valid password!");
                passwordET.requestFocus();
            }
            catch(Exception e) {
                // This helps for testing purposes
            }
            return false;
        }

        if (confirmPassword.isEmpty()) {
            try {
                confirmPasswordET.setError("Please confirm your password!");
                confirmPasswordET.requestFocus();
            }
            catch(Exception e) {
                // This helps for testing purposes
            }
            return false;
        }

        // compare to check if the passwords match for the fields: (password & confirm password).
        if (!confirmPassword.equals(password)) {
            try {
                confirmPasswordET.setError("password doesn't match the above entered password!");
                confirmPasswordET.requestFocus();
            }
            catch(Exception e) {
                // This helps for testing purposes
            }
            return false;
        }

        return true;
    }


    private void registerUser(User user) {

        String url = "https://gradshub.herokuapp.com/api/User/register.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("f_name", user.getFirstName());
        params.put("l_name", user.getLastName());
        params.put("email", user.getEmail());
        params.put("phone_no", user.getPhoneNumber());
        params.put("acad_status", user.getAcademicStatus());
        params.put("password", user.getPassword());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverRegisterUserResponse(response);
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
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }


    private void serverRegisterUserResponse(JSONObject response) {

        try {

            String statusCode = response.getString("success");
            String message = response.getString("message");

            switch (statusCode) {

                // Registration successful!
                case "1":
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show();
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.authentication_nav_host_fragment);
                    navController.navigate(R.id.action_registerFragment_to_loginFragment);
                    break;

                // Email already exists!, Please use another email.
                case "-1":
                    // Ensures the fragment is added (testing)
                    if( isAdded() ) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show();
                    }
                    break;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // ==================== TESTING CODE ===================================
    // setters for testing purposes
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    // =====================================================================

}
