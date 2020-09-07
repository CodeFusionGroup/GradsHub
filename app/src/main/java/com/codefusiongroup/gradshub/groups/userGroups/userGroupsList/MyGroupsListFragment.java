package com.codefusiongroup.gradshub.groups.userGroups.userGroupsList;

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

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.ResearchGroup;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.NetworkRequestQueue;
import com.codefusiongroup.gradshub.groups.userGroups.userGroupProfile.MyGroupsProfileFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MyGroupsListFragment extends Fragment {


    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar progressBar;
    private View view;

    private List<ResearchGroup> researchGroups = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyGroupsListRecyclerViewAdapter adapter;
    private MyGroupsListFragment.OnMyGroupsListFragmentInteractionListener mListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_groups_item_list, container, false);

        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.myGroupsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new MyGroupsListRecyclerViewAdapter(researchGroups, mListener);
            recyclerView.setAdapter(adapter);
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        progressBar = view.findViewById(R.id.progress_circular);

        // inserting liked posts of user if they have liked any posts
        if( MyGroupsProfileFragment.getCurrentlyLikedPosts() != null) {

            ArrayList<String> userCurrentlyLikedPosts = MyGroupsProfileFragment.getCurrentlyLikedPosts();
            User user = MyGroupsProfileFragment.getUser();
            ResearchGroup researchGroup = MyGroupsProfileFragment.getGroup();
            insertGroupLikedPosts(userCurrentlyLikedPosts, user, researchGroup);
            MyGroupsProfileFragment.getCurrentlyLikedPosts().clear(); // important to clear list after

        }


        MainActivity mainActivity = (MainActivity) requireActivity();
        getUserGroups(mainActivity.user);
        progressBar.setVisibility(View.VISIBLE);


        // in the event of failed network requests, refresh page
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                getUserGroups(mainActivity.user);
            }
        });


    }


    private void insertGroupLikedPosts(ArrayList<String> userCurrentlyLikedPosts, User user, ResearchGroup researchGroup) {

        StringBuilder likedPostsIDs = new StringBuilder();

        for(int i = 0; i < userCurrentlyLikedPosts.size(); i++) {

            likedPostsIDs.append(userCurrentlyLikedPosts.get(i));

            if (i != userCurrentlyLikedPosts.size()-1) {
                likedPostsIDs.append(",");
            }
        }

        String url = "https://gradshub.herokuapp.com/api/GroupPost/insertlikes.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("user_id", user.getUserID());
        params.put("group_id", researchGroup.getGroupID());
        params.put("post_id", likedPostsIDs.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverInsertGroupLikedPostsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireActivity(), "failed to insert liked posts, please try again later.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverInsertGroupLikedPostsResponse(JSONObject response) {

        try {

            String statusCode = response.getString("success");
            String message = response.getString("message");

            // liked posts inserted
            if(statusCode.equals("1")) {
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void getUserGroups(User user) {

        String url = "https://gradshub.herokuapp.com/api/User/listgroups.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("user_email", user.getEmail());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverGetUserGroupsResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // means something went wrong when contacting server.
                        mSwipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "failed to retrieve your groups, please try again later.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);
    }


    private void serverGetUserGroupsResponse(JSONObject response) {

        try {

            researchGroups.clear(); // clear to avoid duplicates
            String statusCode = response.getString("success");

            switch(statusCode) {

                // user has joined groups, get the list of research groups this user belongs to.
                case "1":

                    JSONArray researchGroupsJA = response.getJSONArray("message");

                    for(int i = 0 ; i < researchGroupsJA.length(); i++) {

                        JSONObject researchGroupsJO = (JSONObject)researchGroupsJA.get(i);

                        String groupID = researchGroupsJO.getString("GROUP_ID");
                        String groupName = researchGroupsJO.getString("GROUP_NAME");
                        String groupAdmin = researchGroupsJO.getString("GROUP_ADMIN");
                        String groupVisibility = researchGroupsJO.getString("GROUP_VISIBILITY");
                        String groupInviteCode = researchGroupsJO.getString("GROUP_CODE");

                        ResearchGroup researchGroup = new ResearchGroup();
                        researchGroup.setGroupID(groupID);
                        researchGroup.setGroupName(groupName);
                        researchGroup.setGroupAdmin(groupAdmin);
                        researchGroup.setGroupVisibility(groupVisibility);
                        researchGroup.setGroupInviteCode(groupInviteCode);

                        researchGroups.add(researchGroup);
                    }

                    mSwipeRefreshLayout.setRefreshing(false);
                    if (view instanceof RelativeLayout) {
                        progressBar.setVisibility(View.GONE);
                        Context context = view.getContext();
                        recyclerView = view.findViewById(R.id.myGroupsList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        adapter = new MyGroupsListRecyclerViewAdapter(researchGroups, mListener);
                        recyclerView.setAdapter(adapter);
                    }

                    break;

                // if the user has not joined any groups
                case "0":
                    mSwipeRefreshLayout.setRefreshing(false);
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

        menuInflater.inflate(R.menu.my_groups_menu, menu);
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
        if (context instanceof MyGroupsListFragment.OnMyGroupsListFragmentInteractionListener) {
            mListener = (MyGroupsListFragment.OnMyGroupsListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMyGroupsListFragmentInteractionListener");
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
    public interface OnMyGroupsListFragmentInteractionListener {
        void onMyGroupsListFragmentInteraction(ResearchGroup item);
    }


}
