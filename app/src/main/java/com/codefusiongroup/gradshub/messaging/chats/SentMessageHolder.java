package com.codefusiongroup.gradshub.messaging.chats;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.models.ChatMessage;


public class SentMessageHolder extends RecyclerView.ViewHolder {

    TextView messageTV, messageTimeTV;

    public SentMessageHolder(@NonNull View itemView) {
        super(itemView);

        messageTV = (TextView) itemView.findViewById(R.id.messageSentTV);
        messageTimeTV = (TextView) itemView.findViewById(R.id.messageSentTimeTV);

    }

    void bind(ChatMessage message) {

        messageTV.setText(message.getMessageBody());
        messageTimeTV.setText(message.getMessageTime());

    }


}
