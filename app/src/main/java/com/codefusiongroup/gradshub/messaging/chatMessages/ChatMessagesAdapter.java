package com.codefusiongroup.gradshub.messaging.chatMessages;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.models.ChatMessage;

import java.util.List;


public class ChatMessagesAdapter extends RecyclerView.Adapter {

    private static final String TAG = "ChatMessagesAdapter";
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static String mCurrentUserID;

    private Context mContext;
    private List<ChatMessage> chatMessagesList;


    public ChatMessagesAdapter(Context context, List<ChatMessage> chatMessagesList) {
        Log.i(TAG, "ChatMessagesAdapter() called");
        mContext = context;
        this.chatMessagesList = chatMessagesList;
    }


    public static void setCurrentUserID(String id) {
        Log.i(TAG, "setCurrentUserID() called");
        mCurrentUserID = id;
    }


    // Determine the appropriate ViewType according to the sender of the message
    @Override
    public int getItemViewType(int position) {
        Log.i(TAG, "getViewType() called");

        ChatMessage message = (ChatMessage) chatMessagesList.get(position);

        if (message.getCorrespondentID().equals(mCurrentUserID)) {
            // if the current user id is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // if some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }

    }


    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount() called");
        return chatMessagesList.size();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Log.i(TAG, "onCreateViewHolder() called");

        if(viewType == VIEW_TYPE_MESSAGE_SENT) {

            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_chat_message_sent, parent, false);

            return new SentMessageHolder(view);

        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_chat_message_received, parent, false);

            return new ReceivedMessageHolder(view);
        }

        return null;//never mind this
    }


    // passes the message object to the ViewHolder so that the content can be bound to UI
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ChatMessage message = (ChatMessage) chatMessagesList.get(position);
        Log.i(TAG, "onBindViewHolder() called");
        switch ( holder.getItemViewType() ) {
            case VIEW_TYPE_MESSAGE_SENT:
                ( (SentMessageHolder) holder).bind(message);
                break;

            case VIEW_TYPE_MESSAGE_RECEIVED:
                ( (ReceivedMessageHolder) holder).bind(message);
                break;
        }
    }


}
