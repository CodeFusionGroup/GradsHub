package com.example.gradshub.main.eventsSchedule;

import android.content.Context;
import android.content.res.AssetManager;
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
import com.example.gradshub.R;

import com.example.gradshub.main.MainActivity;
import com.example.gradshub.model.Schedule;
import com.example.gradshub.model.User;
import com.example.gradshub.network.NetworkRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class ScheduleListFragment extends Fragment {

    private ProgressBar progressBar;
    private View view;

    private User user;
    private ArrayList<Schedule> schedules = new ArrayList<>();

    private ScheduleListRecyclerViewAdapter adapter;

    HashMap<String, Integer> temp = new HashMap<>();
    // hash maps store events IDs as keys and votes as booleans values
    private static HashMap<String, Boolean> userPreviouslyVotedEvents = new HashMap<>();
    private static HashMap<String, Boolean> userCurrentlyVotedEvents = new HashMap<>();

    // listener that keeps track of which item has the user clicked on
    private OnScheduleListFragmentInteractionListener mListener;
    // listener that keeps track of which event is voted for by the user
    private ScheduleListRecyclerViewAdapter.OnScheduleItemVotedListener onScheduleItemVotedListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MainActivity mainActivity = (MainActivity) requireActivity();
        user = mainActivity.user;

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

        readScheduleFromFile();

        getEventVotesCount();

        getUserVotedEvents();

        onScheduleItemVotedListener = (item, value) -> userCurrentlyVotedEvents.put(item.getId(), value);

    }


    // this method gets schedule from website and writes the schedules to a file (Schedule-data.txt)
    // NOTE: method is not called anywhere yet
    private void requestSchedule() {

        try {

            Document document = Jsoup.connect("https://github.com/abhshkdz/ai-deadlines/blob/gh-pages/_data/conferences.yml").get();
            Elements scheduleData = document.getElementsByTag("tr");

            // Create a new file
            File file = new File("Schedule-data.txt");

            // File writer for the new file
            FileWriter fileWriter = new FileWriter("Schedule-data.txt");
            int count = 0;
            for( Element data : scheduleData) {
                count++;
                if(count < 3) {
                    continue;
                }
                fileWriter.write(data.text());
                //fileWriter.write(System.lineSeparator()); // lineSeparator() giving error
            }
            fileWriter.close();

            // Parse the file
            FileReader fileReader = new FileReader("Schedule-data.txt");
            Scanner scanner = new Scanner(fileReader);

            while(scanner.hasNext()) {
                String scanLine = scanner.nextLine();
                if(scanLine.length() == 0) {
                    continue;
                }

                System.out.println(scanLine);
            }
            scanner.close();


        } catch (IOException e) {
            System.out.println("Error connecting to url");
            e.printStackTrace();
        }

    }


    private void readScheduleFromFile() {

        schedules.clear(); // avoid duplicate elements in recycler view
        StringBuilder contents = new StringBuilder();
        InputStream is = null;
        BufferedReader reader = null;

        try {
            AssetManager am = requireActivity().getAssets();
            is = am.open("Schedule-data.txt");
            reader = new BufferedReader(new InputStreamReader(is));
            contents = new StringBuilder(reader.readLine());
            String line;

            while ( (line = reader.readLine() ) != null) {
                contents.append('\n').append(line);

            }

            // temp holds all the schedules of each conference
            String[] temp = contents.toString().split("\n\n");

            // from temp we take each item and form an object of type Schedule
            for (String s : temp) {

                String title = null, id = null, link = null, deadline = null, timezone = null, date = null, place = null;
                String[] scheduleItem = s.split("\n");

                for (String value : scheduleItem) {

                    // select only relevant parts of the schedule item
                    String substring = value.substring(value.indexOf(":") + 2); // for title, id and link
                    if (value.startsWith("- title")) {
                        title = substring;
                    } else if (value.startsWith("id")) {
                        id = substring;
                    } else if (value.startsWith("link")) {
                        link = substring;
                    } else if (value.startsWith("deadline")) {
                        deadline = value;
                    } else if (value.startsWith("timezone")) {
                        timezone = value;
                    } else if (value.startsWith("date")) {
                        date = value;
                    } else if (value.startsWith("place")) {
                        place = value;
                    }
                }
                schedules.add(new Schedule(id, title, link, deadline, timezone, date, place));
            }


        } catch (final Exception e) {
            e.printStackTrace();

        } finally {
            // releases system resources associated with this stream and reader
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }

        }

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
                        // means something went wrong when contacting server. Just display message indicating
                        // to user to try again
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverGetEventVotesCountResponse(JSONObject response) {

        try {

            temp.clear(); // clear to avoid duplicates
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

                        temp.put(event_id, total_votes);

                    }

                    for(Schedule item: schedules) {
                        String event_id = item.getId();

                        for(HashMap.Entry<String, Integer> entry: temp.entrySet()) {

                            String id = entry.getKey();
                            int event_total_votes = entry.getValue();
                            if(id.equals(event_id)) {
                                item.setVotesCount(event_total_votes);
                            }

                        }
                    }

                    break;

                // no voted events
                case "0":
                    Toast.makeText(requireActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }

            if (view instanceof RelativeLayout) {
                progressBar.setVisibility(View.GONE);
                Context context = view.getContext();
                RecyclerView recyclerView = view.findViewById(R.id.scheduleList);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                adapter = new ScheduleListRecyclerViewAdapter(schedules, mListener, onScheduleItemVotedListener);
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
                        //System.err.println(response);
                        serverGetUserVotedEventsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // means something went wrong when contacting server. Just display message indicating
                        // to user to try again
                        Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });

        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverGetUserVotedEventsResponse(JSONObject response) {

        userPreviouslyVotedEvents.clear(); // clear to avoid duplicates

        try {

            String statusCode = response.getString("success");

            // user has previously voted for some events
            if (statusCode.equals("1")) {

                JSONArray eventsJA = response.getJSONArray("message");

                for(int i = 0 ; i < eventsJA.length(); i++) {

                    JSONObject eventJO = (JSONObject)eventsJA.get(i);
                    String event_id = eventJO.getString("EVENT_ID");
                    Boolean user_vote = Boolean.parseBoolean(eventJO.getString("USER_EVENT_LIKE"));
                    userPreviouslyVotedEvents.put(event_id, user_vote);

                }
            }

            // user hasn't voted for any events yet
            else if (statusCode.equals("0")) {
                Toast.makeText(requireActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
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