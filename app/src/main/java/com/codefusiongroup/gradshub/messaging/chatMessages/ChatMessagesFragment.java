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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.firebase.messaging.Constants.MessagePayloadKeys.SENDER_ID;


public class ChatMessagesFragment extends Fragment implements BaseView<ChatMessagesPresenter>, ChatMessagesContract.IChatMessagesView{


    private static final String TAG = "ChatMessagesFragment";

    private ProgressBar mProgressBar;
    private EditText mTypeMessageET;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ChatMessagesAdapter mAdapter;
    private List<ChatMessage> mChatMessagesList = new ArrayList<>();

    private String mCorrespondentID;
    private ChatMessagesPresenter mPresenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setPresenter(new ChatMessagesPresenter());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            String correspondentName = bundle.getString("name");
            requireActivity().setTitle(correspondentName);
            mCorrespondentID = bundle.getString("id");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_messages_list, container, false);

        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            RecyclerView messagesRecycler = view.findViewById(R.id.chatMessagesList);
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
        Log.i(TAG, "chat messages presenter has subscribed to view");

        MainActivity mainActivity = (MainActivity) requireActivity();
        String currentUserID = mainActivity.user.getUserID();

        // make initial call to get chat messages
        mPresenter.initChatMessages(currentUserID, mCorrespondentID);

        // set current user id for inflating correct layout
        ChatMessagesAdapter.setCurrentUserID( currentUserID );

        sendMessageBtn.setOnClickListener(v -> {

           String userMessage = mTypeMessageET.getText().toString().trim();

            int sizeBeforeUpdate = mChatMessagesList.size();

            if ( mPresenter.validateUserMessage(userMessage) ) {

                String messageTimeStamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

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
                mPresenter.insertMessage(message, "282");

                mChatMessagesList.add(message);
                mAdapter.notifyItemRangeInserted(sizeBeforeUpdate, mChatMessagesList.size());
                mTypeMessageET.setText("");

            }

        });


        // if network request for getting chat messages fails user can swipe to refresh page
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mPresenter.initChatMessages(currentUserID, mCorrespondentID);
            mSwipeRefreshLayout.setRefreshing(true);
        });

    }


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
    public void showMessageInputError(String message){
        mTypeMessageET.setError(message);
        mTypeMessageET.requestFocus();
    }


    @Override
    public void showServerFailureMsg(String message) {
        GradsHubApplication.showToast(message);
    }


    @Override
    public void showFetchMessagesResponse(String message) {
        GradsHubApplication.showToast(message);
    }


    @Override
    public void showInsertMessageResponse(String message){
        GradsHubApplication.showToast(message);
    }


    @Override
    public void loadChatMessages(List<ChatMessage> chatMessages) {
        hideProgressBar();
        if ( mSwipeRefreshLayout.isRefreshing() ) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        mChatMessagesList.clear();
        mChatMessagesList.addAll(chatMessages);
        mAdapter.notifyDataSetChanged();
    }


    //============================================================================
    @Override
    public void updateChatMessagesList(ChatMessage message) {
        hideProgressBar();

        if ( mSwipeRefreshLayout.isRefreshing() ) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        int sizeBeforeUpdate = mChatMessagesList.size();
        mChatMessagesList.add(message);
        mAdapter.notifyItemRangeInserted(sizeBeforeUpdate, mChatMessagesList.size());
    }

    @Override
    public void setChatMessageSentStatus(boolean value) {
        // TODO: next to user message show state of icon that determines whether message was sent or not
        if (value) {
            //change message icon to ticks
        }
        else {
            //set message icon to clock icon
        }
    }
//=================================================================================

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.detachView(this);
        Log.i(TAG, "chat messages presenter has unsubscribed from view");
        mProgressBar = null;
        mSwipeRefreshLayout = null;
        mAdapter = null;
        mCorrespondentID = null;
    }

}