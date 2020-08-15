package com.example.gradshub.main.availablegroups;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.gradshub.R;
import com.example.gradshub.main.MainActivity;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.model.User;
import com.example.gradshub.network.NetworkRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AvailableGroupsListFragment extends Fragment {

    private ProgressBar progressBar;
    private View view;

    private RecyclerView recyclerView;
    private AvailableGroupsListRecyclerViewAdapter adapter;
    private OnAvailableGroupsListFragmentInteractionListener mListener;

    private List<ResearchGroup> researchGroups = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_available_groups_item_list, container, false);

        // ensures that the recycler view list of items are immediately visible when navigating back from
        // AvailableGroupProfileFragment to AvailableGroupsListFragment
        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.availableGroupsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new AvailableGroupsListRecyclerViewAdapter(researchGroups, mListener);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        progressBar = view.findViewById(R.id.progress_circular);
        MainActivity mainActivity = (MainActivity) requireActivity();
        getGroupsToExplore(mainActivity.user);
        progressBar.setVisibility(View.VISIBLE);

    }


    public void getGroupsToExplore(User user) {

        String url = "https://gradshub.herokuapp.com/api/User/availablegroups.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("user_id", user.getUserID());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverGetGroupsToExploreResponse(response);
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



    private void serverGetGroupsToExploreResponse(JSONObject response) {

        try {

            researchGroups.clear(); // clear the list so that we don't have duplicate entries in our recycle view
            String statusCode = response.getString("success");

            switch (statusCode) {

                // there exist groups the user has not joined
                case "1":

                    JSONArray availableGroupsJA = response.getJSONArray("message");

                    for(int i = 0 ; i < availableGroupsJA.length(); i++) {

                        JSONObject availableGroupJO = (JSONObject)availableGroupsJA.get(i);

                        String groupID = availableGroupJO.getString("GROUP_ID");
                        String groupName = availableGroupJO.getString("GROUP_NAME");
                        String groupVisibility = availableGroupJO.getString("GROUP_VISIBILITY");

                        ResearchGroup researchGroup = new ResearchGroup();
                        researchGroup.setGroupID(groupID);
                        researchGroup.setGroupName(groupName);
                        researchGroup.setGroupVisibility(groupVisibility);

                        researchGroups.add(researchGroup);
                    }

                    // now initialize the recycler view adapter with the available groups
                    if (view instanceof RelativeLayout) {
                        progressBar.setVisibility(View.GONE);
                        Context context = view.getContext();
                        recyclerView = view.findViewById(R.id.availableGroupsList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        adapter = new AvailableGroupsListRecyclerViewAdapter(researchGroups, mListener);
                        recyclerView.setAdapter(adapter);
                    }

                    break;

                // no available groups (current user belongs to all groups)
                case "0":
                    if (view instanceof RelativeLayout) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Toast.makeText(requireActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.available_groups_menu, menu);
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
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAvailableGroupsListFragmentInteractionListener) {
            mListener = (OnAvailableGroupsListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAvailableGroupsListFragmentInteractionListener");
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
    public interface OnAvailableGroupsListFragmentInteractionListener {
        void onAvailableGroupsListFragmentInteraction(ResearchGroup item);
    }

}
