package com.codefusiongroup.gradshub.groups.searchGroups.exploreGroupsList;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.ResearchGroup;
import com.codefusiongroup.gradshub.common.network.ApiBaseResponse;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.groups.GroupsAPI;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


public class ExploreGroupsFragment extends Fragment {

    private static final String TAG = "ExploreGroupsFragment";

    private final String  SUCCESS_CODE = "1";
    private final String  SERVER_FAILURE_MSG = "failed to load groups, please swipe to refresh page";
    private final GroupsAPI groupsAPI = ApiProvider.getGroupsApiService();


    public ExploreGroupsFragment() {
        // empty constructor
    }


    private View mRootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;

    private RecyclerView mRecyclerView;
    private ExploreGroupsRecyclerViewAdapter mAdapter;
    private ExploreGroupsFragment.OnExploreGroupsFragmentInteractionListener mListener;
    private List<ResearchGroup> mAvailableGroups = new ArrayList<>();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof ExploreGroupsFragment.OnExploreGroupsFragmentInteractionListener) {
            mListener = (ExploreGroupsFragment.OnExploreGroupsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnExploreGroupsFragmentInteractionListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_explore_groups_item_list, container, false);
        return mRootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        mProgressBar = view.findViewById(R.id.progress_circular);

        MainActivity mainActivity = (MainActivity) requireActivity();
        getGroupsToExplore( mainActivity.user.getUserID() );

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            getGroupsToExplore(mainActivity.user.getUserID());
            mSwipeRefreshLayout.setRefreshing(true);
        });

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {

        menuInflater.inflate(R.menu.available_groups_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);

        MenuItem mSearchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) mSearchItem.getActionView();
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


    public interface OnExploreGroupsFragmentInteractionListener {
        void onExploreGroupsFragmentInteraction(ResearchGroup group);
    }


    // takes user ID cause we want to exclude those groups the user belongs to already
    public void getGroupsToExplore(String userID) {

        Map<String, String> params = new HashMap<>();
        params.put("user_id", userID);

        groupsAPI.getGroupsToExplore(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(SUCCESS_CODE) ) {

                        mAvailableGroups.clear();
                        JsonArray availableGroupsJA = jsonObject.getAsJsonArray("message");

                        for (JsonElement jsonElement: availableGroupsJA) {

                            JsonObject availableGroupJO = jsonElement.getAsJsonObject();

                            String groupID = availableGroupJO.get("GROUP_ID").getAsString();
                            String groupName = availableGroupJO.get("GROUP_NAME").getAsString();
                            String groupVisibility = availableGroupJO.get("GROUP_VISIBILITY").getAsString();

                            ResearchGroup group = new ResearchGroup();
                            group.setGroupID(groupID);
                            group.setGroupName(groupName);
                            group.setGroupVisibility(groupVisibility);

                            mAvailableGroups.add(group);
                        }

                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        if (mRootView instanceof ConstraintLayout) {
                            mProgressBar.setVisibility(View.GONE);
                            mRecyclerView = mRootView.findViewById(R.id.list);
                            Context context = mRootView.getContext();
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                            mAdapter = new ExploreGroupsRecyclerViewAdapter(mAvailableGroups, mListener);
                            mRecyclerView.setAdapter(mAdapter);
                        }

                    }
                    else {
                        // user belongs to all the groups
                        if (mRootView instanceof ConstraintLayout) {
                            mProgressBar.setVisibility(View.GONE);
                        }
                        ApiBaseResponse apiDefault = new Gson().fromJson(jsonObject, ApiBaseResponse.class);
                        GradsHubApplication.showToast(apiDefault.getMessage());
                    }

                }
                else {
                    Log.i(TAG, "response.isSuccessful = false");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                GradsHubApplication.showToast(SERVER_FAILURE_MSG);
                t.printStackTrace();
            }

        });

    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}