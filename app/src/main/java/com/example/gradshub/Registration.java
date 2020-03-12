package com.example.gradshub;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    User user = new User();
    String firstName, lastName, email, phoneNumber, academicStatus, password;
    EditText[] editTexts = new EditText[6];
    Spinner[] spinners = new Spinner[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Spinner spinner = findViewById(R.id.spinner);
        spinners[0] = spinner;
        initialiseSpinner(spinner);

        Button confirmBtn = findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                EditText fnameET = findViewById(R.id.fnameET);
                EditText lnameET = findViewById(R.id.lnameET);
                EditText emailET = findViewById(R.id.emailET);
                EditText phoneNumberET = findViewById(R.id.phoneNumberET);
                EditText passwordET = findViewById(R.id.passwordET);
                EditText confirmPasswordET = findViewById(R.id.confirmPasswordET);

                editTexts[0] = emailET;
                editTexts[1] = passwordET;
                editTexts[2] = confirmPasswordET;
                editTexts[3] = fnameET;
                editTexts[4] = lnameET;
                editTexts[5] = phoneNumberET;

                firstName = fnameET.getText().toString().trim();
                lastName = lnameET.getText().toString().trim();
                email = emailET.getText().toString().trim();
                phoneNumber = phoneNumberET.getText().toString().trim();
                academicStatus = spinner.getSelectedItem().toString();
                password = passwordET.getText().toString().trim();

                // if there are fields missing don't register the user.
                if(!User.validateInputData(editTexts, spinners)) {
                    // we do nothing here except set errors where fields are not filled by user.

                } else { // if all fields are correct then register the user

                    // in the case of all fields filled in correctly but the user's provided email already exits, then the user
                    // is not registered since the DB first checks for this condition before inserting.
                    // so instead a toast msg is displayed indicating that the provided email is already taken, hence provide another.
                    registerUser(firstName, lastName, email, phoneNumber, academicStatus, password);
                }

            }
        });

    }


    public void registerUser(String firstName, String lastName, String email, String phoneNumber, String academicStatus, String password) {

        ContentValues params = new ContentValues();
        // we don't make a user object until we are certain that the details are valid from the DB's perspective, only then
        // do we initialise the user object with the correct details in registrationOutcome() before going to the login activity.
        params.put("USER_FNAME", firstName);
        params.put("USER_LNAME", lastName);
        params.put("USER_EMAIL", email);
        params.put("USER_PHONE_NO", phoneNumber);
        params.put("USER_ACAD_STATUS", academicStatus);
        params.put("USER_PASSWORD", password);

        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/register.php",params) {
            @Override
            protected void onPostExecute(String output) {
                registrationOutcome(output);
            }

        };
        asyncHttpPost.execute();

    }


    public void registrationOutcome(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message = jo.getString("message");

            // Toast msg: Registration successful!
            if(success.equals("1")) {
                Toast.makeText(Registration.this,message,Toast.LENGTH_LONG).show();
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setEmail(email);
                user.setPhoneNumber(phoneNumber);
                user.setAcademicStatus(academicStatus);
                user.setPassword(password);
                loginActivity();
            }

            // can't have the same email address for different users, so we check for this on the DB before inserting the registration details of user
            // Toast msg: Email already exists!, Please use another email.
            else if(success.equals("-1")) {
                Toast.makeText(Registration.this,message,Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // this part of the code allows us to create a spinner with a drop down list of items we want to display to the user to select.
    public void initialiseSpinner(Spinner spinner) {

        // style and populate spinner
        ArrayAdapter<CharSequence> academicStatusAdapter = ArrayAdapter.createFromResource(this,R.array.ACADEMIC_STATUS,android.R.layout.simple_spinner_item);
        // drop down layout style
        academicStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching adapter (academicStatusAdapter) to spinner
        spinner.setAdapter(academicStatusAdapter);
        spinner.setOnItemSelectedListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String text = parent.getItemAtPosition(position).toString();
        TextView tv = (TextView) view;

        if (position == 0) {
            // the first item (which by default is the displayed item) is set to gray to indicate its not
            // part of the selection from the list.
            tv.setTextColor(Color.rgb(112,128,144));
        }

        else {
            // the font colour for valid items once selected, is set to black, otherwise set to gray
            // (for invalid item) when displayed after being selected.
            tv.setTextColor(Color.BLACK);
        }

        // if the user has selected a valid item from the drop down list, then display a short confirm message of the selected item.
        if (position > 0) {
            Toast.makeText(parent.getContext(), "selected " + text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // once the user is registered we want them to login (this is necessary to check if the user can access their accounts
    // using the details they have provided instead of granting immediate access through registration activity).
    public void loginActivity() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

}

