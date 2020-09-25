package com.codefusiongroup.gradshub.messaging.searchableUsers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.friends.FriendsAPI;
import com.google.gson.JsonObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;


public class UserProfileFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "UserProfileFragment";

    private ProgressBar mProgressBar;
    private Button mFriendStatusBtn;
    private Button mBlockUserStatusBtn;

    private User mSelectedUser;
    private MainActivity mainActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            mSelectedUser = bundle.getParcelable("selected_user");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mProgressBar = view.findViewById(R.id.progress_circular);
        Button startChatBtn = view.findViewById(R.id.startChatBtn);
        mFriendStatusBtn = view.findViewById(R.id.friendStatusBtn);
        mBlockUserStatusBtn = view.findViewById(R.id.blockStatusBtn);

        startChatBtn.setOnClickListener(this);
        mFriendStatusBtn.setOnClickListener(this);
        mBlockUserStatusBtn.setOnClickListener(this);

        TextView userNameTV = view.findViewById(R.id.userProfileNameTV);
        userNameTV.setText( mSelectedUser.getFullName() );

        if ( mSelectedUser.isBlocked() ) {
            mBlockUserStatusBtn.setText(R.string.unblock_button_text);
            // shouldn't be able to add this user as a friend or chat with them since they have been blocked
            mFriendStatusBtn.setVisibility(View.GONE);
            startChatBtn.setVisibility(View.GONE);
        }
        else {
            mBlockUserStatusBtn.setText(R.string.block_button_text);
        }

        if ( mSelectedUser.isAFriend() ) {
            mFriendStatusBtn.setText(R.string.unfriend_button_text);
            // shouldn't be able to block this user but instead remove them from the friends list
            mBlockUserStatusBtn.setVisibility(View.GONE);
        }
        else {
            mFriendStatusBtn.setText(R.string.friend_button_text);
        }

    }


    @Override
    public void onClick(View v) {

        NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);

        switch ( v.getId() ) {

            case R.id.startChatBtn:

                // navigate to chat messages to start a conversation
                Bundle userBundle = new Bundle();
                userBundle.putParcelable("selected_user", mSelectedUser);
                Bundle bundle = new Bundle();
                bundle.putBundle("user_bundle", userBundle);
                navController.navigate(R.id.action_userProfileFragment_to_chatMessagesFragment, bundle);

                break;

            case R.id.friendStatusBtn:

                if ( mSelectedUser.isAFriend() ) {
                    Log.i(TAG, "executed: removeUserFromFriendsList() since this user was already a friend");
                    removeUserFromFriendsList( mainActivity.user.getUserID(), mSelectedUser.getUserID() );
                }
                else {
                    Log.i(TAG, "executed: addUserToFriendsList() since this user was not a friend");
                    addUserToFriendsList( mainActivity.user.getUserID(), mSelectedUser.getUserID() );
                }

                mProgressBar.setVisibility(View.VISIBLE);

                break;

            case R.id.blockStatusBtn:

                if ( mSelectedUser.isBlocked() ) {
                    Log.i(TAG, "executed: unblockUser() since this user was blocked already");
                    unblockUser( mainActivity.user.getUserID(), mSelectedUser.getUserID() );
                }
                else {
                    Log.i(TAG, "executed: blockUser() since this user was not blocked already.");
                    blockUser( mainActivity.user.getUserID(), mSelectedUser.getUserID() );
                }

                mProgressBar.setVisibility(View.VISIBLE);

                break;
        }

    }


    private void addUserToFriendsList(String currentUserID, String selectedUserID) {

        HashMap<String, String> params = new HashMap<>();
        params.put( "user_id", currentUserID );
        params.put( "friend_id", selectedUserID );

        FriendsAPI friendsAPI = ApiProvider.getFriendsApiService();
        friendsAPI.addUserToFriendsList(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                mProgressBar.setVisibility(View.GONE);

                if ( response.isSuccessful() ) {
                    Log.i(TAG, " addUserToFriendsList() --> response.isSuccessful() = true");
                    JsonObject jsonObject = response.body();
                    GradsHubApplication.showToast( jsonObject.get("message").getAsString() );
                    //change text of button when request is successful
                    mFriendStatusBtn.setText(R.string.unfriend_button_text);
                }

                else {
                    // internal server failure or something. Response is received but not necessarily a successful one.
                    GradsHubApplication.showToast(ApiResponseConstants.SERVER_FAILURE_MSG);
                    Log.i(TAG, " addUserToFriendsList() --> response.isSuccessful() = false");
                    Log.i(TAG, "error code: " +response.code() );
                    Log.i(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                GradsHubApplication.showToast(ApiResponseConstants.SERVER_FAILURE_MSG);
                Log.i(TAG, "addUserToFriendsList() --> onFailure executed, error: ", t);
                t.printStackTrace();
            }

        });

    }


    private void removeUserFromFriendsList( String currentUserID, String selectedUserID ) {

        HashMap<String, String> params = new HashMap<>();
        params.put( "user_id", currentUserID );
        params.put( "friend_id", selectedUserID );

        FriendsAPI friendsAPI = ApiProvider.getFriendsApiService();
        friendsAPI.removeUserFromFriendsList(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                mProgressBar.setVisibility(View.GONE);

                if ( response.isSuccessful() ) {
                    Log.i(TAG, " removeUserFromFriendsList() --> response.isSuccessful() = true");
                    JsonObject jsonObject = response.body();
                    GradsHubApplication.showToast( jsonObject.get("message").getAsString() );
                    //change text of button when request is successful
                    mFriendStatusBtn.setText(R.string.friend_button_text);
                }

                else {
                    // internal server failure or something. Response is received but not necessarily a successful one.
                    GradsHubApplication.showToast( ApiResponseConstants.SERVER_FAILURE_MSG );
                    Log.i(TAG, " removeUserFromFriendsList() --> response.isSuccessful() = false");
                    Log.i(TAG, "error code: " +response.code() );
                    Log.i(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                GradsHubApplication.showToast(ApiResponseConstants.SERVER_FAILURE_MSG);
                Log.i(TAG, "removeUserFromFriendsList() --> onFailure executed, error: ", t);
                t.printStackTrace();
            }

        });

    }


    private void blockUser( String currentUserID, String selectedUserID ) {

        HashMap<String, String> params = new HashMap<>();
        params.put( "user_id", currentUserID );
        params.put( "blocked_user_id", selectedUserID );

        FriendsAPI friendsAPI = ApiProvider.getFriendsApiService();
        friendsAPI.blockUser(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                mProgressBar.setVisibility(View.GONE);

                if ( response.isSuccessful() ) {
                    Log.i(TAG, " blockUser() --> response.isSuccessful() = true");
                    JsonObject jsonObject = response.body();
                    GradsHubApplication.showToast( jsonObject.get("message").getAsString() );
                    //change text of button when request is successful
                    mBlockUserStatusBtn.setText(R.string.unblock_button_text);
                }

                else {
                    // internal server failure or something. Response is received but not necessarily a successful one.
                    GradsHubApplication.showToast( ApiResponseConstants.SERVER_FAILURE_MSG );
                    Log.i(TAG, " blockUser() --> response.isSuccessful() = false");
                    Log.i(TAG, "error code: " +response.code() );
                    Log.i(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                GradsHubApplication.showToast(ApiResponseConstants.SERVER_FAILURE_MSG);
                Log.i(TAG, "blockUser() --> onFailure executed, error: ", t);
                t.printStackTrace();
            }

        });

    }


    private void unblockUser( String currentUserID, String selectedUserID ) {

        HashMap<String, String> params = new HashMap<>();
        params.put( "user_id", currentUserID );
        params.put( "blocked_user_id", selectedUserID );

        FriendsAPI friendsAPI = ApiProvider.getFriendsApiService();
        friendsAPI.unblockUser(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                mProgressBar.setVisibility(View.GONE);

                if ( response.isSuccessful() ) {
                    Log.i(TAG, " unblockUser() --> response.isSuccessful() = true");
                    JsonObject jsonObject = response.body();
                    GradsHubApplication.showToast( jsonObject.get("message").getAsString() );
                    //change text of button when request is successful
                    mBlockUserStatusBtn.setText(R.string.block_button_text);
                }

                else {
                    // internal server failure or something. Response is received but not necessarily a successful one.
                    GradsHubApplication.showToast( ApiResponseConstants.SERVER_FAILURE_MSG );
                    Log.i(TAG, " unblockUser() --> response.isSuccessful() = false");
                    Log.i(TAG, "error code: " +response.code() );
                    Log.i(TAG, "error message: " +response.message() );

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                GradsHubApplication.showToast(ApiResponseConstants.SERVER_FAILURE_MSG);
                Log.i(TAG, "unblockUser() --> onFailure executed, error: ", t);
                t.printStackTrace();
            }

        });

    }

}