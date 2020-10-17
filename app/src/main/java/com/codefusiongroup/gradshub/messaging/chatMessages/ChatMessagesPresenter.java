package com.codefusiongroup.gradshub.messaging.chatMessages;


import android.util.Log;

import com.codefusiongroup.gradshub.common.BasePresenter2;
import com.codefusiongroup.gradshub.common.models.ChatMessage;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.messaging.MessagingService;


public class ChatMessagesPresenter implements BasePresenter2<ChatMessagesContract.IChatMessagesView>,
    ChatMessagesContract.IChatMessagesPresenter {


    private static final String TAG = "ChatMessagesPresenter";

    private String mInsertMessageResponseCode;
    private String mInsertMessageResponseMsg;

    private ChatMessagesContract.IChatMessagesView mView;

    MessagingService messagingService = MessagingService.newInstance(this);


    @Override
    public void attachView(ChatMessagesContract.IChatMessagesView view) {
        mView = view;
    }


    @Override
    public void detachView(ChatMessagesContract.IChatMessagesView view) {
        mView = null;
    }


    @Override
    public boolean validateUserMessage(String message) {
        if ( message.isEmpty() ) {
            mView.showMessageInputError("please enter a valid message.");
            return false;
        }
        return true;
    }


    @Override
    public void insertMessage(ChatMessage message, String correspondentID) {
        messagingService.sendUserMessage(message, correspondentID);
    }


    @Override
    public void setInsertMessageResponseCode(String responseCode){
        mInsertMessageResponseCode = responseCode;
    }


    @Override
    public void setInsertMessageResponseMsg(String responseMsg) {
        mInsertMessageResponseMsg = responseMsg;
    }


    @Override
    public void onRequestInsertMessageFinished() {
        if (mView != null) {
            if (mInsertMessageResponseCode.equals(ApiResponseConstants.SERVER_FAILURE_CODE)){
                mView.showServerFailureMsg(mInsertMessageResponseMsg);
            }

            else if (mInsertMessageResponseCode.equals(ApiResponseConstants.API_SUCCESS_CODE)){
                mView.showInsertMessageResponse(mInsertMessageResponseMsg);
            }
        }

    }


}
