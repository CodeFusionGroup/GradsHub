package com.codefusiongroup.gradshub.messaging.openChats;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.Chat;
import com.codefusiongroup.gradshub.common.BaseView;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.ApiBaseResponse;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.messaging.MessagingAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


public class OpenChatsFragment extends Fragment { //implements BaseView<OpenChatsPresenter>, OpenChatsContract.IOpenChatsView {


    private static final String TAG = "OpenChatsFragment";

    private View mView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerView;
    private OpenChatsRecyclerViewAdapter mAdapter;
    private OpenChatsFragment.OnOpenChatsFragmentInteractionListener mListener;

    private ArrayList<Chat> mOpenChatsList = new ArrayList<>();
    private ArrayList<String> mChatsToBeRemoved = new ArrayList<>();

    private User mUser;
    private boolean shouldRemoveChats = false;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OpenChatsFragment.OnOpenChatsFragmentInteractionListener) {
            mListener = (OpenChatsFragment.OnOpenChatsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOpenChatsFragmentInteractionListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MainActivity mainActivity = (MainActivity) requireActivity();
        mUser = mainActivity.user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_open_chats_list, container, false);

        if (mView instanceof RelativeLayout) {
            Context context = mView.getContext();
            mRecyclerView = mView.findViewById(R.id.chatsList);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new OpenChatsRecyclerViewAdapter(mOpenChatsList, mListener);
            new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(mRecyclerView);
            mRecyclerView.setAdapter(mAdapter);
        }

        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        mProgressBar = view.findViewById(R.id.progress_circular);

        fetchUserOpenChats(mUser.getUserID());

        // if network request to fetch user's open chats fails, the user can refresh the page to
        // initiate another request
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            fetchUserOpenChats( mUser.getUserID() );
            mSwipeRefreshLayout.setRefreshing(true);
        });


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
            navController.navigate(R.id.action_chatsListFragment_to_friendsListFragment);
        });


        // if user requested to remove chats but pressed back button then boolean shouldRemoveChats
        // will be reset to false since fragment is recreated unlike when navigating to messages then
        // coming back to open chats
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener( (v, keyCode, event) -> {
            if ( keyCode == KeyEvent.KEYCODE_BACK ) {
                if ( mChatsToBeRemoved.size() > 0 ) {
                    requestToRemoveChats(mUser.getUserID(), mChatsToBeRemoved);
                    mChatsToBeRemoved.clear();
                }
            }
            return false;
        });


        // if condition is true it means there is at least one chat to be removed even though undo
        // was pressed for some chats but will always consider the final value of this boolean.
        if (shouldRemoveChats) {
            Log.d(TAG, "shouldRemoveChats final value is true");
            requestToRemoveChats(mUser.getUserID(), mChatsToBeRemoved);
            mChatsToBeRemoved.clear();
        }

    }


    ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            onItemRemoved(viewHolder, mRecyclerView);
        }
    };


    private void onItemRemoved(final RecyclerView.ViewHolder viewHolder, final RecyclerView recyclerView) {
        final int adapterPos = viewHolder.getAdapterPosition();
        Log.d(TAG, "adapterPos: "+adapterPos);
        final Chat chat = mOpenChatsList.get(adapterPos);
        String chatID = chat.getChatID();
        Log.d(TAG, "chat to be removed--> correspondentName: "+chat.getCorrespondentName()+", message: "+chat.getLatestMessage()+", timestamp: "+chat.getMessageTimeStamp());
        mChatsToBeRemoved.add(chatID);
        Log.d(TAG, "after swipe(remove) mChatsToBeRemoved size is: "+mChatsToBeRemoved.size());
        Snackbar snackbar = Snackbar.make(recyclerView, "CHAT REMOVED", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        shouldRemoveChats = false;
                        mOpenChatsList.add(adapterPos, chat);
                        mAdapter.notifyItemInserted(adapterPos);
                        recyclerView.scrollToPosition(adapterPos);
                        mChatsToBeRemoved.remove(chatID);
                        Log.d(TAG, "after undo mChatsToBeRemoved size is: "+mChatsToBeRemoved.size());
                        Log.d(TAG, "after undo mOpenChatsList size is: "+mOpenChatsList.size());
                    }
                });

        shouldRemoveChats = true;
        snackbar.show();
        mOpenChatsList.remove(adapterPos);
        Log.d(TAG, "after swipe(remove) mOpenChatsList size is: "+mOpenChatsList.size());
        mAdapter.notifyItemRemoved(adapterPos);

    }


    public void fetchUserOpenChats(String userID) {

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userID);

        MessagingAPI messagingAPI = ApiProvider.getMessageApiService();
        messagingAPI.fetchOpenChats(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                hideProgressBar();
                if ( mSwipeRefreshLayout.isRefreshing() ) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                if ( response.isSuccessful() ) {
                    Log.d(TAG, "fetchUserOpenChats() --> response.isSuccessful() = true");

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        List<Chat> openChatsList = new ArrayList<>();
                        JsonArray openChatsJA = jsonObject.getAsJsonArray("message");

                        for (JsonElement jsonElement: openChatsJA) {
                            JsonObject openChatJO = jsonElement.getAsJsonObject();
                            Chat  chat = new Gson().fromJson(openChatJO, Chat.class);
                            openChatsList.add(chat);
                        }

                        // mAdapter must point to the same object mOpenChatsList otherwise recycler view won't update
                        mOpenChatsList.clear();
                        mOpenChatsList.addAll(openChatsList);
                        mAdapter.notifyDataSetChanged();
                    }
                    else {
                        GradsHubApplication.showToast("no open chats exits yet.");
                    }

                }

                else {
                    GradsHubApplication.showToast("Failed to load open chats, please swipe to refresh page or try again later.");
                    Log.d(TAG, "fetchUserOpenChats() --> response.isSuccessful() = false");
                    Log.d(TAG, "error code: " +response.code() );
                    Log.d(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hideProgressBar();
                GradsHubApplication.showToast("Failed to load open chats, please swipe to refresh page or try again later.");
                Log.d(TAG, "fetchUserOpenChats() --> onFailure executed, error: ", t);
                t.printStackTrace();
            }

        });

    }


    private void requestToRemoveChats(String userID, List<String> chatsToBeRemoved) {

        StringBuilder chatsIDs = new StringBuilder();
        for(int i = 0; i < chatsToBeRemoved.size(); i++) {
            chatsIDs.append(chatsToBeRemoved.get(i));

            if (i != chatsToBeRemoved.size()-1) {
                chatsIDs.append(",");
            }
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userID);
        params.put("chat_ids", chatsIDs.toString());


        final MessagingAPI messagingAPI = ApiProvider.getMessageApiService();
        messagingAPI.removeChats(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                // reset boolean value on response from server (type of response doesn't matter)
                shouldRemoveChats = false;
                if ( response.isSuccessful() ) {
                    Log.d(TAG, "requestToRemoveChats() --> response.isSuccessful() = true");
                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {
                        // omit this toast message if the operation is successful
                        //GradsHubApplication.showToast(jsonObject.get("message").getAsString());
                        Log.d(TAG, "requestToRemoveChats() --> api response: "+jsonObject.get("message").getAsString());
                    }
                    else {
                        // Api error
                        ApiBaseResponse apiDefault = new Gson().fromJson(jsonObject, ApiBaseResponse.class);
                        GradsHubApplication.showToast( apiDefault.getMessage() );
                    }
                }
                else {
                    GradsHubApplication.showToast("Failed to remove selected chat(s), please try again later.");
                    Log.d(TAG, "requestToRemoveChats() --> response.isSuccessful() = false");
                    Log.d(TAG, "error code: " +response.code() );
                    Log.d(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // reset boolean value on response from server (type of response doesn't matter)
                shouldRemoveChats = false;
                GradsHubApplication.showToast("Failed to remove selected chat(s), please try again later.");
                Log.d(TAG, "requestToRemoveChats() --> onFailure executed, error: ", t);
            }

        });

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
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }


    public interface OnOpenChatsFragmentInteractionListener {
        void onOpenChatsFragmentInteraction(Chat item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}