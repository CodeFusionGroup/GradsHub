package com.codefusiongroup.gradshub.messaging.chatMessages;

import com.codefusiongroup.gradshub.common.models.ChatMessage;

import java.util.List;


public interface ChatMessagesContract {

    interface IChatMessagesView {

        void showMessageInputError(String message);

        void showServerFailureMsg(String message);

        void showInsertMessageResponse(String message);

        void updateChatMessagesList(ChatMessage message);

    }


    interface IChatMessagesPresenter {

        boolean validateUserMessage(String message);

        void insertMessage(ChatMessage message, String correspondentID);

        void onRequestInsertMessageFinished();

        void setInsertMessageResponseCode(String responseCode);

        void setInsertMessageResponseMsg(String responseMsg);

    }

    interface IChatMessagesModel {

        void sendUserMessage(ChatMessage message, String correspondentID);
    }


}
