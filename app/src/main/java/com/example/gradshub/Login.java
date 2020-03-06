package com.example.gradshub;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    User user = new User();
    //
    String userEmail,userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        EditText emailET = findViewById(R.id.emailET);
        EditText passwordET = findViewById(R.id.passwordET);

        userEmail = emailET.getText().toString().trim();
        userPassword = passwordET.getText().toString().trim();


        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                requestLogin(userEmail, userPassword);
            }
        });
    }


    public void requestLogin(String userEmail, String userPassword) {
        ContentValues params = new ContentValues();

        params.put("USER_EMAIL", userEmail);
        params.put("USER_PASSWORD", userPassword);

        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/login.php", params) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(String output) {

                if (validateLoginInput() == false) {
                    Toast.makeText(Login.this, "One or more fields are missing!", Toast.LENGTH_SHORT).show();
                } else {
                    requestResponse(output);
                }
            }

        };
        asyncHttpPost.execute();
    }


    public boolean validateLoginInput() {

        EditText emailET = findViewById(R.id.emailET);
        EditText passwordET = findViewById(R.id.passwordET);

        userEmail = emailET.getText().toString().trim();
        userPassword = passwordET.getText().toString().trim();

        if (userEmail.isEmpty()) {
            emailET.setError("Field can't be empty!");
            emailET.requestFocus();
            return false;
        }

        if (userPassword.isEmpty()) {
            passwordET.setError("Field can't be empty!");
            passwordET.requestFocus();
            return false;
        }

        return true;
    }


    public void requestResponse(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message =jo.getString("message");

            if (success.equals("1")) {
                Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                feedActivity();
                user.setEmail(userEmail);
                user.setPassword(userPassword);
            }
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

}
