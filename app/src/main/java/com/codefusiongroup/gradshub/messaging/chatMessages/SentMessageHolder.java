package com.codefusiongroup.gradshub.messaging.chatMessages;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.models.ChatMessage;


public class SentMessageHolder extends RecyclerView.ViewHolder {

    TextView messageTV, messageTimeStampTV;

    public SentMessageHolder(@NonNull View itemView) {
        super(itemView);

        messageTV = itemView.findViewById(R.id.messageSentTV);
        messageTimeStampTV = itemView.findViewById(R.id.messageSentTimeTV);

    }

    void bind(ChatMessage message) {

        messageTV.setText(message.getMessage());
        messageTimeStampTV.setText(message.getMessageTimeStamp());

    }


}
