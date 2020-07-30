package com.example.gradshub.main.conferenceupdates;

import android.content.ContentValues;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gradshub.R;
import com.example.gradshub.main.availablegroups.AvailableGroupsListFragment;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.model.User;
import com.example.gradshub.network.AsyncHTTpPost;
import com.example.gradshub.network.ScheduleNetworkRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class ScheduleListFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_item, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        requestSchedule();
    }



//    private void requestSchedule() {
//
//        ContentValues params = new ContentValues();
//
//        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://github.com/abhshkdz/ai-deadlines/blob/gh-pages/_data/conferences.yml",params) {
//            @Override
//            protected void onPostExecute(String output) {
//                serverRequestScheduleResponse(output);
//            }
//
//        };
//        asyncHttpPost.execute();
//    }


    private void requestSchedule() {

        ContentValues params = new ContentValues();

        ScheduleNetworkRequest scheduleNetworkRequest = new ScheduleNetworkRequest("https://github.com/abhshkdz/ai-deadlines/blob/gh-pages/_data/conferences.yml",params) {
            @Override
            protected void onPostExecute(String output) {
                serverRequestScheduleResponse(output);
            }

        };
        scheduleNetworkRequest.execute();
    }


    private void serverRequestScheduleResponse(String output) {

        try {

            if(output.equals("")) {
                //progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
            }
            else {

                String o = output;
                JSONObject jo = new JSONObject(output);
                String success = jo.getString("success");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }




}