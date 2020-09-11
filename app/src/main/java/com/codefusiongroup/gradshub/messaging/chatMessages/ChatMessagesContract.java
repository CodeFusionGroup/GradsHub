package com.codefusiongroup.gradshub.messaging.chatMessages;

import com.codefusiongroup.gradshub.common.models.ChatMessage;

import java.util.List;


public interface ChatMessagesContract {

    interface IChatMessagesView {

        void showMessageInputError(String message);

        void showServerFailureMsg(String message);

        void showInsertMessageResponse(String message);

        void showFetchMessagesResponse(String message);

        // for first time loading
        void loadChatMessages(List<ChatMessage> chatMessagesList);

        //========================================================================
        // called in MessagingService in the onMessageReceived() callback
        void updateChatMessagesList(ChatMessage message);

        // called in MessagingService in the onMessageSent() callback
        void setChatMessageSentStatus(boolean status);
        //=========================================================================

    }


    interface IChatMessagesPresenter {

        // called to load messages for an open chat
        void initChatMessages(String currentUserID, String correspondentID);

        boolean validateUserMessage(String message);

        void setFetchMessagesResponseCode(String responseCode);

        void setFetchMessagesResponseMsg(String responseMsg);

        void onRequestFetchMessagesFinished();

        void setChatMessagesList(List<ChatMessage> chatMessageList);

        void insertMessage(ChatMessage message, String correspondentID);

        void onRequestInsertMessageFinished();

        void setInsertMessageResponseCode(String responseCode);

        void setInsertMessageResponseMsg(String responseMsg);

        //===================================================================
        // after onMessageReceived is called, update the messages adapter
        void setChatMessage(ChatMessage message);

        // called in onMessageSent callback of fcm in MessagingService when user send a message
        void setMessageSentState(boolean value);
        //===================================================================

    }

    interface IChatMessagesModel {

        void fetchChatMessages(String currentUserID, String correspondentID);

        void sendUserMessage(ChatMessage message, String correspondentID);
    }


}
