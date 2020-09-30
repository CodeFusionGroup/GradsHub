package com.codefusiongroup.gradshub.friends;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.ResearchGroup;
import com.codefusiongroup.gradshub.common.models.User;
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


public class FriendsFragment extends Fragment {

    private static final String TAG = "FriendsFragment";

    private final String  SUCCESS_CODE = "1";
    private final String  SERVER_FAILURE_MSG = "Failed to load friends, please try again later or refresh page.";
    private final FriendsAPI friendsAPI = ApiProvider.getFriendsApiService();

    private View mRootView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mProgressBar;

    private RecyclerView mRecyclerView;
    private FriendsListRecyclerViewAdapter mAdapter;
    private FriendsFragment.OnFriendsListFragmentInteractionListener mListener;
    private List<User> mFriendsList = new ArrayList<>();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FriendsFragment.OnFriendsListFragmentInteractionListener) {
            mListener = (FriendsFragment.OnFriendsListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFriendsListFragmentInteractionListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_friends_item_list, container, false);
        return mRootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        mProgressBar = view.findViewById(R.id.progress_circular);

        MainActivity mainActivity = (MainActivity) requireActivity();
        getUserFriends( mainActivity.user.getUserID() );

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            getUserFriends( mainActivity.user.getUserID() );
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


    public interface OnFriendsListFragmentInteractionListener {
        void onFriendsListFragmentInteraction(User user);
    }


    public void getUserFriends(String userID) {

        Map<String, String> params = new HashMap<>();
        params.put("user_id", userID);

        friendsAPI.getUserFriends(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                mProgressBar.setVisibility(View.GONE);
                if ( mSwipeRefreshLayout.isRefreshing() ) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if ( response.isSuccessful() ) {
                    Log.d(TAG, "getUserFriends() --> response.isSuccessful() = true");

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(SUCCESS_CODE) ) {

                        mFriendsList.clear();
                        JsonArray userFriendsJA = jsonObject.getAsJsonArray("message");

                        for (JsonElement jsonElement: userFriendsJA) {

                            JsonObject userFriendJO = jsonElement.getAsJsonObject();

                            String userID = userFriendJO.get("USER_ID").getAsString();
                            String firstName = userFriendJO.get("USER_FNAME").getAsString();
                            String lastName = userFriendJO.get("USER_LNAME").getAsString();

                            User user = new User();
                            user.setUserID(userID);
                            user.setFirstName(firstName);
                            user.setLastName(lastName);
                            user.setFriendStatus(true);

                            mFriendsList.add(user);
                        }

                        if (mRootView instanceof ConstraintLayout) {
                            mRecyclerView = mRootView.findViewById(R.id.list);
                            Context context = mRootView.getContext();
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                            mAdapter = new FriendsListRecyclerViewAdapter(mFriendsList, mListener);
                            mRecyclerView.setAdapter(mAdapter);
                        }

                    }
                    else {
                        // user has no friends
                        ApiBaseResponse apiDefault = new Gson().fromJson(jsonObject, ApiBaseResponse.class);
                        GradsHubApplication.showToast( apiDefault.getMessage() );
                    }

                }
                else {
                    GradsHubApplication.showToast( SERVER_FAILURE_MSG );
                    Log.d(TAG, "getUserFriends() --> response.isSuccessful() = false");
                    Log.d(TAG, "error code: " +response.code() );
                    Log.d(TAG, "error message: " +response.message() );
                }

            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if ( mSwipeRefreshLayout.isRefreshing() ) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                mProgressBar.setVisibility(View.GONE);
                GradsHubApplication.showToast( SERVER_FAILURE_MSG );
                Log.d(TAG, "getUserFriends() --> onFailure executed, error: ", t);
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