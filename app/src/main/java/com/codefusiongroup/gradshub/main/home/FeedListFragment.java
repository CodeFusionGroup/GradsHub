package com.codefusiongroup.gradshub.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.main.MainActivity;
import com.codefusiongroup.gradshub.main.eventsSchedule.ScheduleListFragment;
import com.codefusiongroup.gradshub.model.User;
import com.codefusiongroup.gradshub.network.NetworkRequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class FeedListFragment extends Fragment {

    private View view;
    private User user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MainActivity mainActivity = (MainActivity) requireActivity();
        user = mainActivity.user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feed_item_list, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        // calling insertUserVotedEvents() here allows the schedule list to be updated properly in terms of showing the overall
        // latest votes counts which the user will be able to see when they want to view the schedule later or so.
        // (obviously they can see the votes counts for the particular event update immediately DURING vote clicks since the
        // update in that instance does not require a network request)
        if( ScheduleListFragment.getUserCurrentlyVotedEvents() != null) {

            HashMap<String, Boolean> userCurrentlyVotedEvents = ScheduleListFragment.getUserCurrentlyVotedEvents();
            insertUserVotedEvents(userCurrentlyVotedEvents);
            ScheduleListFragment.getUserCurrentlyVotedEvents().clear(); // important to clear map after
        }

        // similar explanation as above for favoured events in this case
        if( ScheduleListFragment.getUserCurrentlyFavouredEvents() != null) {

            ArrayList<String> userCurrentlyFavouredEvents = ScheduleListFragment.getUserCurrentlyFavouredEvents();
            insertUserFavouredEvents(userCurrentlyFavouredEvents);
            ScheduleListFragment.getUserCurrentlyFavouredEvents().clear(); // important to clear list after
        }

    }


    public void insertUserVotedEvents(HashMap<String, Boolean> userCurrentlyVotedEvents) {

        StringBuilder eventsIDs = new StringBuilder();
        StringBuilder eventsVotes = new StringBuilder();

        int counter = 0;

        for(HashMap.Entry<String, Boolean> entry: userCurrentlyVotedEvents.entrySet()) {
            String event_id = entry.getKey();
            Boolean event_vote = entry.getValue();
            eventsIDs.append(event_id);
            eventsVotes.append(event_vote);

            if (counter != userCurrentlyVotedEvents.size()-1) {
                eventsIDs.append(",");
                eventsVotes.append(",");
                counter++;
            }
        }

        String url = "https://gradshub.herokuapp.com/api/Event/insertvotes.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("user_id", user.getUserID());
        params.put("event_ids", eventsIDs.toString());
        params.put("event_votes", eventsVotes.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverInsertVotedEventsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /*
                        NOTE: this means something went wrong when trying to contact the server.
                        Just display specific toast message indicating the type of action that was to be performed
                        in the background as a result of the user's actions on the app.
                        However this means the user might have to manually repeat that action every time there's a
                        failure which can be a time consuming task for the user depending on what they were doing.
                         */
                        //Toast.makeText(requireActivity(), "Error processing your voted events, try again later.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(requireActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverInsertVotedEventsResponse(JSONObject response) {

        try {

            String statusCode = response.getString("success");
            String message = response.getString("message");

            // votes have been inserted
            if(statusCode.equals("1")) {
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
            }

            // NOTE: no fail case based on the implemented logic for this task, so might have to remove this condition.
            else if(statusCode.equals("0")) {
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void insertUserFavouredEvents(ArrayList<String> userCurrentlyFavouredEvents) {

        StringBuilder eventsIDs = new StringBuilder();

        for(int i = 0; i < userCurrentlyFavouredEvents.size(); i++) {

            eventsIDs.append(userCurrentlyFavouredEvents.get(i));

            if (i != userCurrentlyFavouredEvents.size()-1) {
                eventsIDs.append(",");
            }
        }

        String url = "https://gradshub.herokuapp.com/api/Event/insertfavourite.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("user_id", user.getUserID());
        params.put("event_ids", eventsIDs.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverInsertFavouredEventsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /*
                        NOTE: this means something went wrong when trying to contact the server.
                        Just display specific toast message indicating the type of action that was to be performed
                        in the background as a result of the user's actions on the app.
                        However this means the user might have to manually repeat that action every time there's a
                        failure which can be a time consuming task for the user depending on what they were doing.
                         */
                        Toast.makeText(requireActivity(), "Error processing your favourite events, try again later.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverInsertFavouredEventsResponse(JSONObject response) {

        try {

            String statusCode = response.getString("success");
            String message = response.getString("message");

            // events have been favoured
            if(statusCode.equals("1")) {
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ) {

            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
