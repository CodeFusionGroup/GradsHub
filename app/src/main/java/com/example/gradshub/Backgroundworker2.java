package com.example.gradshub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Scorpions on 2019/05/10.
  An abstract version of the first background worker
 */


public abstract //TextView Tut1 = (TextView) findViewById(R.id.TUT1);
        class Backgroundworker2 extends AsyncTask<String,Void,String> {
    Context context;
    AlertDialog alertDialog;//to show login result
    Backgroundworker2 (Context ctx){
        context=ctx;
    }


    @Override
    protected String doInBackground(String... params) {
        //here we want to perfom our post operation then validate email and password
        String type= params[0];
        String userLogger = "http://lamp.ms.wits.ac.za/~s1682836/Login.php";


        if(type.equals("login")){
            try {
                String userEmail = params[1];
                String userPassword = params[2];
                URL url = new URL(userLogger);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter= new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String post_data= URLEncoder.encode("USER_EMAIL","UTF-8")+"="+URLEncoder.encode(userEmail,"UTF-8")+"&"
                        +URLEncoder.encode("USER_PASSWORD","UTF-8")+"="+URLEncoder.encode(userPassword,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                outputStream.close();
                InputStream inputStream =httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

                String result="";
                String line= "";
                while((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                //now we return the result we got
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        //TODO To be implemented later on for forgotten password
       /* else if(type == "forgot_password"){

            try{
                String student_id = params[1];
                String email = params[2];
                URL url = new URL(reset_pass);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter= new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String post_data= URLEncoder.encode("student_id","UTF-8")+"="+URLEncoder.encode(student_id,"UTF-8")+"&"
                        +URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                outputStream.close();
                InputStream inputStream =httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                while((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                //now we return the result we got
                return result;
            }

            catch (MalformedURLException e) {
                e.printStackTrace();
            }

            catch (IOException e) {
                e.printStackTrace();
            }

        }
        */

        return null;
    }

    @Override
    protected void onPreExecute() {
        alertDialog=new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Proccess Status");

    }

    @Override
    protected abstract void onPostExecute(String result);

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}