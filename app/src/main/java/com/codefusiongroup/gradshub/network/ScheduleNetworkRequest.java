package com.codefusiongroup.gradshub.network;

import android.content.ContentValues;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


public abstract class ScheduleNetworkRequest extends AsyncTask<String, String, String> {

    String address;
    ContentValues parameters;

    public ScheduleNetworkRequest(String address, ContentValues parameters) {
        this.address = address;
        this.parameters = parameters;
    }

    @Override
    protected String doInBackground(String... params) {

        try {

            URL url = new URL(address);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            connection.setDoOutput(true);

//            if (parameters.size() > 0) {
//                connection.setDoInput(true);
//                Uri.Builder builder = new Uri.Builder();
//                for (String s : parameters.keySet()) {
//                    builder.appendQueryParameter(s, parameters.getAsString(s));
//                }
//
//                String query = builder.build().getEncodedQuery();
//                OutputStream os = connection.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//                writer.write(query);
//                writer.flush();
//                writer.close();
//                os.close();
//            }
            //=======================================================
            StringBuilder sb = new StringBuilder();
            //url = new URL("http://github.com/abhshkdz/ai-deadlines/blob/gh-pages/_data/conferences.yml");

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine;
            while( ( inputLine = in.readLine() ) != null ) {
                sb.append(inputLine);
            }

            in.close();

            return sb.toString();

            //=========================================================
//            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String response = br.readLine();
//            br.close();
//            return response;

        } catch (Exception e) {

            e.printStackTrace();
            return "";
        }

    }

    @Override
    protected abstract void onPostExecute(String output);


    /*

private static class MyTask extends AsyncTask<Object, Void, String> {

    Main2Activity activity;

    @Override
    protected String doInBackground(Object... params) {
        activity = (Main2Activity)params[0];
        try {
            StringBuilder sb = new StringBuilder();
            URL url = new URL("http://github.com/abhshkdz/ai-deadlines/blob/gh-pages/_data/conferences.yml");

            BufferedReader in;
            in = new BufferedReader(
                    new InputStreamReader(
                            url.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine);

            in.close();

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String str) {
        //Do something with result string
        //WebView webView = activity.findViewById(R.id.web_view);
        //webView.loadData(str, "text/html; charset=UTF-8", null);
    }

}


     */

}