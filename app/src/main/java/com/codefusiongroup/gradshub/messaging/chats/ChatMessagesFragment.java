package com.codefusiongroup.gradshub.messaging.chats;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.ChatMessage;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.NetworkRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Locale;


public class ChatMessagesFragment extends Fragment {


    private View mView;
    private ProgressBar mProgressBar;
    private EditText mTypeMessageET;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mMessageRecycler;
    private ChatMessagesAdapter mAdapter;
    private static List<ChatMessage> mChatMessagesList = new ArrayList<>();

    private String message;
    private User mSelectedUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        Bundle bundle = this.getArguments();
        if(bundle != null) {
            mSelectedUser = bundle.getParcelable("selected_user");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_chat_messages_list, container, false);

        // TODO: set action bar to contactName of other user
        requireActivity().setTitle(mSelectedUser.getFullName());
        setHasOptionsMenu(true);
        if (mView instanceof RelativeLayout) {
            Context context = mView.getContext();
            mMessageRecycler = mView.findViewById(R.id.chatMessagesList);
            mMessageRecycler.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new ChatMessagesAdapter(context, mChatMessagesList);
            mMessageRecycler.setAdapter(mAdapter);
        }

        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        mProgressBar = view.findViewById(R.id.progress_circular);
        mTypeMessageET = view.findViewById(R.id.typeMessageET);
        Button sendMessageBtn = view.findViewById(R.id.sendMessageBtn);

        MainActivity mainActivity = (MainActivity) requireActivity();
        User mCurrentUser = mainActivity.user;

        String currentUserID = mCurrentUser.getUserID();
        ChatMessagesAdapter.setCurrentUserID(currentUserID);

        getChatMessages(currentUserID, mSelectedUser.getUserID());

        sendMessageBtn.setOnClickListener(v -> {

            message = mTypeMessageET.getText().toString().trim();

            if ( isValidInput() ) {

                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                String messageTimeStamp = dateTime.substring( dateTime.indexOf(" ") );

                mChatMessagesList.add( new ChatMessage(currentUserID, messageTimeStamp, message, mSelectedUser.getUserID()) );
                mAdapter.notifyDataSetChanged();

                mTypeMessageET.setText("");

            }

        });


        // in the event of failed network requests for getChatMessages(), refresh page to make another request
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            getChatMessages( currentUserID, mSelectedUser.getUserID() );
        });

    }


    public static List<ChatMessage> getChatMessagesList() { return mChatMessagesList; }


    private boolean isValidInput() {

        if (message.isEmpty()) {
            mTypeMessageET.setError("Not a valid message!");
            mTypeMessageET.requestFocus();
            return false;
        }

        return true;
    }


    private void getChatMessages(String currentUserID, String selectedUserID) {

        String url = "https://gradshub.herokuapp.com/api/User/retrieveChatMessages.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("current_user_id", currentUserID);
        params.put("selected_user_id", selectedUserID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (mView instanceof ConstraintLayout) {
                            mProgressBar.setVisibility(View.GONE);
                        }

                        serverGetChatMessagesResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (mView instanceof ConstraintLayout){
                            mProgressBar.setVisibility(View.GONE);
                        }
                        GradsHubApplication.showToast("failed to load messages, please swipe to refresh page.");
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverGetChatMessagesResponse(JSONObject response) {

        try {

            mChatMessagesList.clear(); // clear to avoid duplicates in recycler view
            String statusCode = response.getString("success");

            // no messages for this chat
            if (statusCode.equals("0")) {
                GradsHubApplication.showToast(response.getString("message"));
            }

            else {

                JSONArray messagesJA = response.getJSONArray("message");

                for(int i = 0 ; i < messagesJA.length(); i++) {

                    JSONObject messageJO = (JSONObject) messagesJA.get(i);
                    String userID = messageJO.getString("USER_ID");
                    String messageTimestamp = messageJO.getString("MESSAGE_TIMESTAMP");
                    String message = messageJO.getString("MESSAGE");
                    String correspondentId = messageJO.getString("CORRESPONDENT_ID");

                    mChatMessagesList.add( new ChatMessage(userID, messageTimestamp, message, correspondentId));
                }

                if (mView instanceof RelativeLayout) {
                    Context context = mView.getContext();
                    mMessageRecycler = mView.findViewById(R.id.chatMessagesList);
                    mMessageRecycler.setLayoutManager(new LinearLayoutManager(context));
                    mAdapter = new ChatMessagesAdapter(context, mChatMessagesList);
                    mMessageRecycler.setAdapter(mAdapter);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}