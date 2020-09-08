package com.codefusiongroup.gradshub.messaging.chats;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.Chat;
import com.codefusiongroup.gradshub.common.models.ChatMessage;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.NetworkRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChatsListFragment extends Fragment {


    private View mView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerView;
    private ChatsListRecyclerViewAdapter mAdapter;
    private ChatsListFragment.OnChatsListFragmentInteractionListener mListener;

    private ArrayList<Chat> mChatsList = new ArrayList<>();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ChatsListFragment.OnChatsListFragmentInteractionListener) {
            mListener = (ChatsListFragment.OnChatsListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnChatsListFragmentInteractionListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_chats_list, container, false);

        if (mView instanceof RelativeLayout) {
            Context context = mView.getContext();
            mRecyclerView = mView.findViewById(R.id.chatsList);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new ChatsListRecyclerViewAdapter(mChatsList, mListener);
            mRecyclerView.setAdapter(mAdapter);
        }

        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        mProgressBar = view.findViewById(R.id.progress_circular);

        // if there are chat messages to be sent from the ChatMessagesFragment, we store them in the DB.
        // TODO: user firebase real time DB
        if(ChatMessagesFragment.getChatMessagesList().size() > 0) {

            List<ChatMessage> chatMessages = ChatMessagesFragment.getChatMessagesList();
            //TODO: php file for testing
            //insertChatMessage(chatMessages);
            ChatMessagesFragment.getChatMessagesList().clear(); // important to clear list
        }


        MainActivity mainActivity = (MainActivity) requireActivity();
        //TODO: php file for testing
        //getOpenChats(mainActivity.user.getUserID());

        // in the event of failed network requests for getLatestMessages();, refresh page to make another request
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            //getOpenChats(mainActivity.user.getUserID());
            mSwipeRefreshLayout.setRefreshing(true);
        });


    }


    private void insertChatMessage(List<ChatMessage> chatMessages) {

        StringBuilder chatMessagesStr = new StringBuilder();
        //int counter = 0;
        for(ChatMessage message: chatMessages) {

            chatMessagesStr.append(message.getMessageCreatorID());
            chatMessagesStr.append(",");
            chatMessagesStr.append(message.getMessageTime());
            chatMessagesStr.append(",");
            chatMessagesStr.append(message.getMessageBody());
            chatMessagesStr.append(",");
            chatMessagesStr.append(message.getRecipientUserID());

//            if (counter != chatMessages.size()-1) {
//                chatMessagesStr.append(",");
//            }
//            counter++;
        }

        String url = "https://gradshub.herokuapp.com/api/Chats/insertChatMessages.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("chat_message", chatMessagesStr.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverInsertChatMessageResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        GradsHubApplication.showToast("failed to send message, please try again later.");
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverInsertChatMessageResponse(JSONObject response) {

        try {

            String statusCode = response.getString("success");
            String message = response.getString("message");

            // message sent
            if(statusCode.equals("1")) {
                GradsHubApplication.showToast(message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void getOpenChats(String userID) {

        String url = "https://gradshub.herokuapp.com/api/Chats/retrieveOpenChats.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("user_id", userID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        if (mView instanceof RelativeLayout) {
                            mProgressBar.setVisibility(View.GONE);
                        }

                        serverGetOpenChatsResponse(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                        if (mView instanceof RelativeLayout) {
                            mProgressBar.setVisibility(View.GONE);
                        }

                        GradsHubApplication.showToast("failed to load open chats, please swipe to refresh page.");
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverGetOpenChatsResponse(JSONObject response) {


        try {

            mChatsList.clear(); // clear to avoid duplicates in recycler view
            String statusCode = response.getString("success");

            // no open chats exist
            if (statusCode.equals("0")) {
                GradsHubApplication.showToast(response.getString("message"));
            }

            else {

                JSONArray openChatsJA = response.getJSONArray("message");

                for(int i = 0 ; i < openChatsJA.length(); i++) {

                    JSONObject messageJO = (JSONObject) openChatsJA.get(i);
                    String contactName = messageJO.getString("CONTACT_NAME");
                    String message = messageJO.getString("MESSAGE");
                    String messageTimestamp = messageJO.getString("MESSAGE_TIMESTAMP");

                    mChatsList.add( new Chat(contactName, message, messageTimestamp) );
                }

                if (mView instanceof RelativeLayout) {
                    Context context = mView.getContext();
                    mRecyclerView = mView.findViewById(R.id.chatsList);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                    mAdapter = new ChatsListRecyclerViewAdapter(mChatsList, mListener);
                    mRecyclerView.setAdapter(mAdapter);
                }

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
    public interface OnChatsListFragmentInteractionListener {
        void onChatsListFragmentInteraction(Chat item);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}