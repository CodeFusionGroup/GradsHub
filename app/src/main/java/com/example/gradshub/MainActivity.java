package com.example.gradshub;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText userEmail, userPassword;
    private TextView forgotPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userEmail = (EditText)findViewById(R.id.editEmail);
        userPassword = (EditText)findViewById(R.id.editPassword);
        forgotPass = (TextView) findViewById(R.id.forgot_pass);
    }


    public void onLogin(View view) {
        String email= userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String type = "login";

        Backgroundworker2 backgroundworker= new Backgroundworker2(this) {
            @Override
            protected void onPostExecute(String result) {
                Context context = getApplicationContext();
                String outcome = new String();
                if(result.contains("incorect emai") || result.contains("try again")){
                        outcome = "Log in failed please check credentials!";
                }
                else if(result.contains("logged in")){
                    outcome = "Successfully Logged in";
                    startFeed();
                }
                CharSequence text = outcome;
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        };
        backgroundworker.execute(type,email,password);

    }

    public void startFeed(){
        startActivity(new Intent(this,Feed.class));
    }

}
