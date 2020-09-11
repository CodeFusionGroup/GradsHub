package com.codefusiongroup.gradshub.messaging.openChats;

import com.codefusiongroup.gradshub.common.models.Chat;

import java.util.List;


public interface OpenChatsContract {


    interface IOpenChatsView {

        void showUserOpenChatsResponseMsg(String message);

        void updateUserOpenChats(List<Chat> openChats);

    }


    interface IOpenChatsPresenter {

        void initialiseUserOpenChats(String userID);

        void setUserOpenChatsResponseMsg(String responseMsg);

        void setUserOpenChatsResponseCode(String responseCode);

        void setUserOpenChatsList(List<Chat> chatsList);

        void onRequestUpdateUserOpenChatsFinished();

    }


    interface IOpenChatsModel {

        void fetchUserOpenChats(String userID);
    }


}
