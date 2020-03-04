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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button lecLoginBtn = findViewById(R.id.loginBtn);
        lecLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                EditText emailET = findViewById(R.id.emailET);
                EditText passwordET = findViewById(R.id.passwordET);

                String userEmail = emailET.getText().toString();
                String password = passwordET.getText().toString();

                ContentValues params = new ContentValues();

                params.put("USER_EMAIL", userEmail);
                params.put("USER_PASSWORD", password);

                AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost(
                        "https://gradshub.herokuapp.com/login.php", params) {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    protected void onPostExecute(String output) {

                        if (validate() == false) {
                            Toast.makeText(Login.this, "One or more fields are missing!", Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(Login.this, "Testing ", Toast.LENGTH_SHORT).show();
                            loginUser(output);
                        }
                    }

                };
                asyncHttpPost.execute();
            }
        });
    }

    private boolean validate() {

        EditText emailET = findViewById(R.id.emailET);
        EditText passwordET = findViewById(R.id.passwordET);

        String personNo = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        if (personNo.isEmpty()) {
            emailET.setError("Field can't be empty!");
            emailET.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwordET.setError("Field can't be empty!");
            passwordET.requestFocus();
            return false;
        }

        return true;
    }

    private void loginUser(String output) {

        try {

            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message =jo.getString("message");

            if (success.equals("1")) {
                Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                feedActivity();
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
