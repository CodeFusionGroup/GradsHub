package com.codefusiongroup.gradshub.messaging.searchableUsers;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.NetworkRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UsersListFragment extends Fragment {


    private View mView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerView;
    private UsersListRecyclerViewAdapter mAdapter;
    private UsersListFragment.OnUsersListFragmentInteractionListener mListener;

    private User mUser;
    private List<User> mUsersList = new ArrayList<>();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UsersListFragment.OnUsersListFragmentInteractionListener) {
            mListener = (UsersListFragment.OnUsersListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUsersListFragmentInteractionListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MainActivity mMainActivity = (MainActivity) requireActivity();
        mUser = mMainActivity.user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_users_list, container, false);

        if (mView instanceof ConstraintLayout) {
            Context context = mView.getContext();
            mRecyclerView = mView.findViewById(R.id.usersList);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new UsersListRecyclerViewAdapter(mUsersList, mListener);
            mRecyclerView.setAdapter(mAdapter);
        }

        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        mProgressBar = view.findViewById(R.id.progress_circular);

        getCommonUsers();

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            getCommonUsers();
            mSwipeRefreshLayout.setRefreshing(true);
        });

    }


    private void getCommonUsers() {

        String url = "https://gradshub.herokuapp.com/api/User/retrieveComMembers.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("user_id", mUser.getUserID());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (mView instanceof ConstraintLayout){
                            mProgressBar.setVisibility(View.GONE);
                        }

                        serverGetCommonUsersResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // means something went wrong when contacting server.
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (mView instanceof ConstraintLayout){
                            mProgressBar.setVisibility(View.GONE);
                        }
                        GradsHubApplication.showToast("failed to load users, please swipe to refresh page.");
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverGetCommonUsersResponse(JSONObject response) {

        try {

            mUsersList.clear(); // clear to avoid duplicates in recycler view
            String statusCode = response.getString("success");

            // no users belong to similar groups as the current user
            if (statusCode.equals("0")) {
                GradsHubApplication.showToast(response.getString("message"));
            }

            // there exists users who belong to same groups as current user
            else {

                JSONArray usersJA = response.getJSONArray("message");

                for(int i = 0 ; i < usersJA.length(); i++) {

                    JSONObject userJO = (JSONObject)usersJA.get(i);

                    String userID = userJO.getString("USER_ID");
                    String firstName = userJO.getString("USER_FNAME");
                    String lastName = userJO.getString("USER_LNAME");
                    String email = userJO.getString("USER_EMAIL");
                    String phoneNo = userJO.getString("USER_PHONE_NO");
                    String acadStatus = userJO.getString("USER_ACAD_STATUS");

                    User user = new User();
                    user.setUserID(userID);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setAcademicStatus(acadStatus);
                    user.setEmail(email);
                    user.setPhoneNumber(phoneNo);

                    mUsersList.add(user);

                }

                if (mView instanceof ConstraintLayout) {
                    Context context = mView.getContext();
                    mRecyclerView = mView.findViewById(R.id.usersList);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    mAdapter = new UsersListRecyclerViewAdapter(mUsersList, mListener);
                    mRecyclerView.setAdapter(mAdapter);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {

        // we sue the same resource file for now since the menus are the same
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
                mAdapter.getFilter().filter(newText);
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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnUsersListFragmentInteractionListener {
        void onUsersListFragmentInteraction(User user);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}