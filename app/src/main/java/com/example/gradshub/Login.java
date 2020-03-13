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

public class Login extends AppCompatActivity {

    User user = new User();
    String email, password;
    EditText[] editTexts = new EditText[2];
    Spinner[] spinners = new Spinner[1];// we don't really need this for login but because validateInputData()
                                        // method is generic we need to pass in "spinners" so that it validates for registration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText emailET = findViewById(R.id.emailET);
                EditText passwordET = findViewById(R.id.passEt);

                editTexts[0] = emailET;
                editTexts[1] = passwordET;

                email = emailET.getText().toString().trim();
                password = passwordET.getText().toString().trim();

                if (!User.validateInputData(editTexts, spinners)) {
                    // we do nothing, except set errors where fields are not filled in (done by validateInputData() already)

                } else { // if all the fields are filled then call this method and respond appropriately using toast messages.
                    requestLogin(email, password);
                }

            }
        });

        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationActivity(v);
            }
        });

    }


    public void requestLogin(String email, String password) {
        ContentValues params = new ContentValues();

        params.put("USER_EMAIL", email);
        params.put("USER_PASSWORD", password);

        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/login.php", params) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(String output) {
                loginRequestOutcome(output);
            }

        };
        asyncHttpPost.execute();
    }


    public void loginRequestOutcome(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message =jo.getString("message");

            //Toast msg: Successfully logged in!
            if (success.equals("1")) {
                Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                //if the user entered correct login credentials, we initialise the user with the login credentials.
                user.setEmail(email);
                user.setPassword(password);
                feedActivity();
            }

            //Toast msg: Incorrect email, try again!.
            else if(success.equals("-1")) {
                Toast.makeText(Login.this,message,Toast.LENGTH_SHORT).show();
            }

            //Toast msg: Incorrect password. Please try again!
            else if(success.equals("0")) {
                Toast.makeText(Login.this,message,Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void feedActivity() {
        Intent intent = new Intent(this, Feed.class);
        startActivity(intent);
    }


    public void registrationActivity(View v) {
        Intent intent = new Intent(this, Registration.class);
        startActivity(intent);
    }


}
