package com.example.gradshub;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Registration extends AppCompatActivity {
    //Naming the variables we used in this class, to avoid redefining(Making them global within this class).
    private EditText nameET, surnameET, emailET, phoneNoET, passwordET;
    private Spinner spinner;
    private String userName, userSurname, phoneNum, userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Button confButton = findViewById(R.id.confirmBtn);
        confButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                nameET = findViewById(R.id.nameEt);
                surnameET = findViewById(R.id.surnameEt);
                phoneNoET = findViewById(R.id.phoneNoEt);
                emailET = findViewById(R.id.emailEt);
                passwordET = findViewById(R.id.passEt);
                spinner = findViewById(R.id.spinner);

                userName = nameET.getText().toString();
                userSurname = surnameET.getText().toString();
                phoneNum = phoneNoET.getText().toString();
                userEmail = emailET.getText().toString();
                String password = passwordET.getText().toString();
                //confirm password is defined and used in validate method only.
                String levelOfStudy = spinner.getSelectedItem().toString();
                ContentValues params = new ContentValues();



                params.put("USER_NAME", userName);
                params.put("USER_SURNAME", userSurname);
                params.put("PHONE_NUMBER", phoneNum);
                params.put("USER_EMAIL", userEmail);
                params.put("USER_PASSWORD", password);
                params.put("LEVEL_OF_STUDY", levelOfStudy);


                //Todo: Match the parameters with the PHP file, and change the php url
                @SuppressLint("StaticFieldLeak") AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                        "https://gradshub.herokuapp.com/login.php", params) {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    protected void onPostExecute(String output) {

                        if (validate() == false) {
                            Toast.makeText(Registration.this, "One or more fields are missing!", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(Login.this, "Testing ", Toast.LENGTH_SHORT).show();
                            registerUser(output);
                        }
                    }

                };
                asyncHttpPost.execute();
            }
        });

    }

    private boolean validate() {

        EditText passwordET = findViewById(R.id.passwordET);
        EditText confPass = findViewById(R.id.confirmPassEt);
        String password = passwordET.getText().toString().trim();

        if (userName.trim().isEmpty() || userSurname.trim().isEmpty() || phoneNum.trim().isEmpty() || userEmail.trim().isEmpty()) {
            emailET.setError("Field can't be empty!");
            emailET.requestFocus();
            return false;
        }
        //ToDo:Validate the spinner, check if the year of study is selected.

        if (password.isEmpty()) {
            passwordET.setError("Field can't be empty!");
            passwordET.requestFocus();
            return false;
        }
        //validating password and password confirmation
        if(!password.equals(confPass)){
            confPass.setError("Please ensure you have entered correct password!");
            confPass.requestFocus();
            return false;
        }


        return true;
    }

    private void registerUser(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message =jo.getString("message");

            if (success.equals("1")) {
                Toast.makeText(Registration.this, message, Toast.LENGTH_SHORT).show();
                feedActivity();
            }
            else if(success.equals("0")) {
                Toast.makeText(Registration.this,message,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void feedActivity() {
        Intent intent = new Intent(this, Feed.class);
        startActivity(intent);
    }
}