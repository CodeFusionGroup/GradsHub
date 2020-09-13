package com.codefusiongroup.gradshub.messaging.chatMessages;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.ChatMessage;
import com.codefusiongroup.gradshub.common.BaseView;
import com.codefusiongroup.gradshub.common.models.User;
import com.codefusiongroup.gradshub.common.network.ApiBaseResponse;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.messaging.MessagingAPI;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;

import static com.google.firebase.messaging.Constants.MessagePayloadKeys.SENDER_ID;


public class ChatMessagesFragment extends Fragment implements BaseView<ChatMessagesPresenter>, ChatMessagesContract.IChatMessagesView{


    private static final String TAG = "ChatMessagesFragment";

    private ProgressBar mProgressBar;
    private EditText mTypeMessageET;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ChatMessagesAdapter mAdapter;
    private ArrayList<ChatMessage> mChatMessagesList = new ArrayList<>();

    private User mSelectedUser;
    private String mOpenChatsUserID;
    private ChatMessagesPresenter mPresenter;
    private RecyclerView messagesRecycler;
    private static ChatMessagesFragment instance;

    public static ChatMessagesFragment getInstance() {

        if (instance == null) {
            instance = new ChatMessagesFragment();
        }
        return instance;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setPresenter(new ChatMessagesPresenter());

        Bundle bundle = this.getArguments();
        // 2 bundles received but cannot be accessed at the same time
        if(bundle != null) {

            // if bundle happens to come from user selecting an open chat
            if ( bundle.containsKey("name") && bundle.containsKey("id") ) {
                String mOpenChatsUsername = bundle.getString("name");
                mOpenChatsUserID = bundle.getString("id");
                requireActivity().setTitle(mOpenChatsUsername);
            }

            // if bundle happens to come from current user searching for other users they can chat with
            else if ( bundle.containsKey("user_bundle") ) {
                Bundle userBundle = bundle.getBundle("user_bundle");
                if(userBundle != null) {
                    mSelectedUser = userBundle.getParcelable("selected_user");
                    requireActivity().setTitle( mSelectedUser.getFullName() );
                }
            }

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_messages_list, container, false);
        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            messagesRecycler = view.findViewById(R.id.chatMessagesList);
            messagesRecycler.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new ChatMessagesAdapter(context, mChatMessagesList);
            messagesRecycler.setAdapter(mAdapter);
        }

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        mProgressBar = view.findViewById(R.id.progress_circular);
        mTypeMessageET = view.findViewById(R.id.typeMessageET);
        Button sendMessageBtn = view.findViewById(R.id.sendMessageBtn);

        mPresenter.attachView(this);
        //Log.i(TAG, "chat messages presenter has subscribed to view");

        MainActivity mainActivity = (MainActivity) requireActivity();
        String currentUserID = mainActivity.user.getUserID();

        // make initial call to get chat messages depending on the current application flow the user is engaged in.
        if (mOpenChatsUserID != null ) {
            //mPresenter.initChatMessages(currentUserID, mOpenChatsUserID);
            fetchChatMessages( currentUserID, mOpenChatsUserID );
            Log.i(TAG, "open chat user id obtained: "+mOpenChatsUserID);
        }
        else if (mSelectedUser != null) {
            //mPresenter.initChatMessages(currentUserID, mSelectedUser.getUserID());
            fetchChatMessages( currentUserID, mSelectedUser.getUserID() );
            Log.i(TAG, "selected user id obtained: "+mSelectedUser.getUserID());
        }


        // set current user id for inflating correct layout
        ChatMessagesAdapter.setCurrentUserID( currentUserID );

        sendMessageBtn.setOnClickListener(v -> {

           String userMessage = mTypeMessageET.getText().toString().trim();

            int sizeBeforeUpdate = mChatMessagesList.size();

            if ( mPresenter.validateUserMessage(userMessage) ) {

                String messageTimeStamp = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault()).format(new Date());

                //=================================================================================
                // send upstream messages back to the app server using the XMPP-based Cloud connection server
                // address of the receiving app server in the format: SENDER_ID + "@fcm.googleapis.com"

//                AtomicInteger msgId = new AtomicInteger();
//                FirebaseMessaging fm = FirebaseMessaging.getInstance();
//                fm.send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
//                        .setMessageId( Integer.toString( msgId.incrementAndGet() ) )
//                        .addData( "sender_user_id", currentUserID )
//                        .addData( "message", userMessage )
//                        .addData( "message_timestamp", messageTimeStamp )
//                        .addData( "correspondent_user_id", mCorrespondentID )
//                        .build());
                //=================================================================================

                ChatMessage message = new ChatMessage(userMessage, messageTimeStamp, currentUserID );

                if (mOpenChatsUserID != null ) {
                    //mPresenter.initChatMessages(currentUserID, mOpenChatsUserID);
                    mPresenter.insertMessage( message, mOpenChatsUserID );
                }
                else if (mSelectedUser != null) {
                    //mPresenter.initChatMessages(currentUserID, mSelectedUser.getUserID());
                    mPresenter.insertMessage( message, mSelectedUser.getUserID() );
                }

                mChatMessagesList.add(message);
                mAdapter.notifyItemRangeInserted( sizeBeforeUpdate, mChatMessagesList.size() );
                mTypeMessageET.setText("");

            }

        });


        // if network request for getting chat messages fails user can swipe to refresh page
        mSwipeRefreshLayout.setOnRefreshListener(() -> {

            // make initial call to get chat messages depending on the current application flow the user is engaged in.
            if (mOpenChatsUserID != null ) {
                //mPresenter.initChatMessages(currentUserID, mOpenChatsUserID);
                fetchChatMessages(currentUserID, mOpenChatsUserID );
            }
            else if (mSelectedUser != null) {
                //mPresenter.initChatMessages(currentUserID, mSelectedUser.getUserID());
                fetchChatMessages(currentUserID, mSelectedUser.getUserID() );
            }

            mSwipeRefreshLayout.setRefreshing(true);
        });

    }


    public void fetchChatMessages(String currentUserID, String correspondentID) {

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id_one", currentUserID);
        params.put("user_id_two", correspondentID);

        MessagingAPI messagingAPI = ApiProvider.getMessageApiService();
        messagingAPI.fetchChatMessages(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {
                    Log.i(TAG, "response.isSuccessful() = true");

                    JsonObject jsonObject = response.body();

                    // open chats have messages so no else case
                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        List<ChatMessage> chatMessagesList = new ArrayList<>();
                        JsonArray chatMessagesJA = jsonObject.getAsJsonArray("message");

                        for (JsonElement jsonElement: chatMessagesJA) {
                            JsonObject chatMessageJO = jsonElement.getAsJsonObject();
                            ChatMessage  message = new Gson().fromJson(chatMessageJO, ChatMessage.class);
                            chatMessagesList.add(message);
                        }

                        mChatMessagesList.clear();
                        mChatMessagesList.addAll(chatMessagesList);
                        mAdapter.notifyDataSetChanged();
                    }
                    else {
                        ApiBaseResponse apiDefault = new Gson().fromJson(jsonObject, ApiBaseResponse.class);
                        GradsHubApplication.showToast( apiDefault.getMessage() );
                    }
                }

                else {
                    Log.i(TAG, "response.isSuccessful() = false");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                GradsHubApplication.showToast( "failed to load messages, please refresh page." );
                t.printStackTrace();
            }

        });

    }


    public void updateChatMessages(ChatMessage message) {

        Log.i(TAG, "updateChatMessagesList() called");
        Log.i(TAG, "message correspondentID: "+message.getCorrespondentID());
        Log.i(TAG, "message time stamp: "+message.getMessageTimeStamp());
        Log.i(TAG, "message: "+message.getMessage());

        if ( mSwipeRefreshLayout.isRefreshing() ) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        int sizeBeforeUpdate = mChatMessagesList.size();
        mChatMessagesList.add(message);
        mAdapter.notifyItemRangeInserted(sizeBeforeUpdate, mChatMessagesList.size());
    }


    //=====================================================================================
    @Override
    public void setPresenter(ChatMessagesPresenter presenter){
        mPresenter = presenter;
    }


    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void showMessageInputError(String message) {
        mTypeMessageET.setError(message);
        mTypeMessageET.requestFocus();
    }


    @Override
    public void showServerFailureMsg(String message) {
        //hideProgressBar();
        GradsHubApplication.showToast(message);
    }


//    @Override
//    public void showFetchMessagesResponse(String message) {
//        GradsHubApplication.showToast(message);
//    }


    @Override
    public void showInsertMessageResponse(String message) {
        //hideProgressBar();
        GradsHubApplication.showToast(message);
    }


//    @Override
//    public void loadChatMessages(List<ChatMessage> chatMessages) {
//        Log.i(TAG, "loadChatMessages() called");
//        //hideProgressBar();
//        if ( mSwipeRefreshLayout.isRefreshing() ) {
//            mSwipeRefreshLayout.setRefreshing(false);
//        }
//
//
//        mChatMessagesList.clear();
//        mChatMessagesList.addAll(chatMessages);
//        messagesRecycler.setAdapter(new ChatMessagesAdapter(requireContext(), mChatMessagesList));
//        mAdapter.notifyDataSetChanged();
//        Log.i(TAG, "mChatMessagesList size: "+mChatMessagesList.size());
//        Log.i(TAG, "mChatMessagesList: "+mChatMessagesList);
//    }


    @Override
    public void updateChatMessagesList(ChatMessage message) {
        Log.i(TAG, "updateChatMessagesList() called");
        //hideProgressBar();

        if ( mSwipeRefreshLayout.isRefreshing() ) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        int sizeBeforeUpdate = mChatMessagesList.size();
        mChatMessagesList.add(message);
        mAdapter.notifyItemRangeInserted(sizeBeforeUpdate, mChatMessagesList.size());
    }

//
//    @Override
//    public void setChatMessageSentStatus(boolean value) {
//        if (value) {
//        }
//        else {
//        }
//    }
//=================================================================================

    @Override
    public void onDetach() {
        super.onDetach();
        //mPresenter.detachView(this);
        //Log.i(TAG, "chat messages presenter has unsubscribed from view");
    }

}