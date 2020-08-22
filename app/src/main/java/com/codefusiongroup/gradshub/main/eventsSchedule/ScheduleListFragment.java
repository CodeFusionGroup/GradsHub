package com.codefusiongroup.gradshub.main.eventsSchedule;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codefusiongroup.gradshub.R;

import com.codefusiongroup.gradshub.main.MainActivity;
import com.codefusiongroup.gradshub.model.Schedule;
import com.codefusiongroup.gradshub.model.User;
import com.codefusiongroup.gradshub.network.NetworkRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ScheduleListFragment extends Fragment {

    private ProgressBar progressBar;
    private View view;

    private User user;
    private ScheduleListRecyclerViewAdapter adapter;

    private ArrayList<Schedule> schedules = new ArrayList<>();
    // hash maps store events IDs as keys and votes as booleans values
    private static HashMap<String, Boolean> userPreviouslyVotedEvents = new HashMap<>();
    private static HashMap<String, Boolean> userCurrentlyVotedEvents = new HashMap<>();

    private HashMap<String, Integer> eventsVotesCounts = new HashMap<>();

    // lists store events IDs that have been favoured by user
    private static ArrayList<String> userPreviouslyFavouredEvents = new ArrayList<>();
    private static ArrayList<String> userCurrentlyFavouredEvents = new ArrayList<>();

    // listener that keeps track of which item has the user clicked on
    private OnScheduleListFragmentInteractionListener mListener;
    // listener that keeps track of which event is voted for by the user
    private ScheduleListRecyclerViewAdapter.OnScheduleItemVotedListener onScheduleItemVotedListener;
    // listener that keeps track of which event is favoured by the user
    private ScheduleListRecyclerViewAdapter.OnScheduleItemFavouredListener onScheduleItemFavouredListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MainActivity mainActivity = (MainActivity) requireActivity();
        user = mainActivity.user;

        //getUserFavouredEvents();

        if (mainActivity.getScheduleList() != null) {
            schedules = mainActivity.getScheduleList();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule_item_list, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        progressBar = view.findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);

        getUserFavouredEvents();

        getEventVotesCount();

        getUserVotedEvents();

        onScheduleItemVotedListener = (item, value) -> userCurrentlyVotedEvents.put(item.getId(), value);

        onScheduleItemFavouredListener = (item) -> userCurrentlyFavouredEvents.add(item.getId());

    }


    private void getEventVotesCount() {

        String url = "https://gradshub.herokuapp.com/api/Event/retrieveall.php";
        HashMap<String, String> params = new HashMap<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverGetEventVotesCountResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /*
                        NOTE: regarding the display of a toast message here, its also a similar explanation given below
                        on the corresponding server response method for this method (i.e serverGetEventVotesCountResponse())
                         */
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverGetEventVotesCountResponse(JSONObject response) {

        try {

            eventsVotesCounts.clear(); // clear to avoid duplicates
            String success = response.getString("success");

            switch (success) {

                // there are voted events
                case "1":

                    JSONArray eventsJA = response.getJSONArray("message");

                    for(int i = 0 ; i < eventsJA.length(); i++) {

                        JSONObject eventJO = (JSONObject)eventsJA.get(i);

                        String event_id = eventJO.getString("EVENT_ID");
                        Integer votes_true = Integer.parseInt(eventJO.getString("VOTES_TRUE"));
                        Integer votes_false = Integer.parseInt(eventJO.getString("VOTES_FALSE"));
                        Integer total_votes = votes_true - votes_false;

                        eventsVotesCounts.put(event_id, total_votes);

                    }

                    for(Schedule item: schedules) {
                        String event_id = item.getId();

                        for(HashMap.Entry<String, Integer> entry: eventsVotesCounts.entrySet()) {

                            String id = entry.getKey();
                            int event_total_votes = entry.getValue();
                            if(id.equals(event_id)) {
                                item.setVotesCount(event_total_votes);
                            }

                        }
                    }

                    break;

                /*
                NOTE: Since these network calls happen asynchronously and there are multiple methods in this fragment
                that make network requests, it is not always clear as to which method will finish its network request
                first thus it becomes difficult as to which Toast message will be shown to the user especially in the
                case of the DB not having any records of the requested data since then these methods display different
                Toast messages in that instance which can be confusing to the user. It is therefore important to consider
                which toast messages are important to show to the user when a network request has been made.
                So for methods like this one there's no need to display toast msg for network calls that get data
                but don't necessarily have to inform the user of the outcome of that response from server.
                */
                case "0":
                    // no toast message shown.
                    break;
            }

            // NOTE: this will execute regardless whether there are voted events or not because it shows the events items.
            // only after event votes counts have been calculated for each voted event do we populate the
            // recycler view adapter. This is to ensure that all the necessary data is collected first then an accurate
            // information is shown to the user
            if (view instanceof RelativeLayout) {
                progressBar.setVisibility(View.GONE);
                Context context = view.getContext();
                RecyclerView recyclerView = view.findViewById(R.id.scheduleList);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                adapter = new ScheduleListRecyclerViewAdapter(schedules, mListener, onScheduleItemVotedListener, onScheduleItemFavouredListener);
                recyclerView.setAdapter(adapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void getUserVotedEvents() {

        String url = "https://gradshub.herokuapp.com/api/User/retrievevotes.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("user_id", user.getUserID());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverGetUserVotedEventsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /*
                        NOTE: regarding the display of a toast message here, its also a similar explanation given below
                        on the corresponding server response method for this method (i.e serverGetUserVotedEventsResponse())
                         */
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverGetUserVotedEventsResponse(JSONObject response) {

        try {

            userPreviouslyVotedEvents.clear(); // clear to avoid duplicates
            String statusCode = response.getString("success");

            // user has previously voted for some events
            if (statusCode.equals("1")) {

                JSONArray eventsJA = response.getJSONArray("message");

                for(int i = 0 ; i < eventsJA.length(); i++) {

                    JSONObject eventJO = (JSONObject)eventsJA.get(i);
                    String event_id = eventJO.getString("EVENT_ID");
                    Boolean user_vote = Boolean.parseBoolean(eventJO.getString("EVENT_VOTE"));
                    userPreviouslyVotedEvents.put(event_id, user_vote);

                }
            }

            /*
            NOTE: Since these network calls happen asynchronously and there are multiple methods in this fragment
            that make network requests, it is not always clear as to which method will finish its network request
            first thus it becomes difficult as to which Toast message will be shown to the user especially in the
            case of the DB not having any records of the requested data since then these methods display different
            Toast messages in that instance which can be confusing to the user. It is therefore important to consider
            which toast messages are important to show to the user when a network request has been made.
            So for methods like this one there's no need to display toast msg for network calls that get data
            but don't necessarily have to inform the user of the outcome of that response from server.
            */
            else if (statusCode.equals("0")) {
                // no toast message shown.
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void getUserFavouredEvents() {

        String url = "https://gradshub.herokuapp.com/api/User/retrievefavourites.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("user_id", user.getUserID());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverGetUserFavouredEventsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        /*
                        NOTE: regarding the display of a toast message here, its also a similar explanation given below
                        on the corresponding server response method for this method (i.e serverGetUserFavouredEventsResponse())
                         */
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance(requireActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }


    private void serverGetUserFavouredEventsResponse(JSONObject response) {

        try {

            userPreviouslyFavouredEvents.clear(); // clear in case activity is recreated due to configuration changes
            String success = response.getString("success");

            switch (success) {

                // there are favoured events by user
                case "1":

                    JSONArray favouredEventsJA = response.getJSONArray("message");
                    for (int i = 0; i < favouredEventsJA.length(); i++) {

                        JSONObject favouredEventJO = (JSONObject) favouredEventsJA.get(i);
                        String event_id = favouredEventJO.getString("EVENT_ID");
                        userPreviouslyFavouredEvents.add(event_id);
                    }

                    break;

                /*
                NOTE: Since these network calls happen asynchronously and there are multiple methods in this fragment
                that make network requests, it is not always clear as to which method will finish its network request
                first thus it becomes difficult as to which Toast message will be shown to the user especially in the
                case of the DB not having any records of the requested data since then these methods display different
                Toast messages in that instance which can be confusing to the user. It is therefore important to consider
                which toast messages are important to show to the user when a network request has been made.
                So for methods like this one there's no need to display toast msg for network calls that get data
                but don't necessarily have to inform the user of the outcome of that response from server.
                */
                case "0":
                    // no toast message shown.
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static HashMap<String, Boolean> getUserCurrentlyVotedEvents() {

        if (userCurrentlyVotedEvents.size() == 0) {
            return null;
        }

        return userCurrentlyVotedEvents;
    }


    public static HashMap<String, Boolean> getUserPreviouslyVotedEvents() {

        if (userPreviouslyVotedEvents.size() == 0) {
            return null;
        }

        return userPreviouslyVotedEvents;
    }


    public static ArrayList<String> getUserCurrentlyFavouredEvents() {

        if (userCurrentlyFavouredEvents.size() == 0) {
            return null;
        }

        return userCurrentlyFavouredEvents;
    }


    public static ArrayList<String> getUserPreviouslyFavouredEvents() {

        if (userPreviouslyFavouredEvents.size() == 0) {
            return null;
        }

        return userPreviouslyFavouredEvents;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.available_groups_menu, menu); // for now we will use the same resource file
        super.onCreateOptionsMenu(menu, menuInflater);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:

                return true;

            default:
                return super.onOptionsItemSelected(item); // hamburger icon of side menu bar when pressed
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnScheduleListFragmentInteractionListener) {
            mListener = (OnScheduleListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnScheduleListFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnScheduleListFragmentInteractionListener {
        void onScheduleListFragmentInteraction(Schedule item);
    }


}
