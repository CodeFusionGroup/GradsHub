package com.codefusiongroup.gradshub.messaging.chatMessages;


import android.util.Log;

import com.codefusiongroup.gradshub.common.BasePresenter2;
import com.codefusiongroup.gradshub.common.models.ChatMessage;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.messaging.MessagingService;

import java.util.ArrayList;
import java.util.List;


public class ChatMessagesPresenter implements BasePresenter2<ChatMessagesContract.IChatMessagesView>,
    ChatMessagesContract.IChatMessagesPresenter {


    private static final String TAG = "ChatMessagesPresenter";

//    private String mFetchMessagesResponseCode = "1";
//    private String mFetchMessagesResponseMsg = null;

    private String mInsertMessageResponseCode;
    private String mInsertMessageResponseMsg;

    //private List<ChatMessage> mChatsMessages = new ArrayList<>();

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


//    @Override
//    public void initChatMessages(String currentUserID, String correspondentID) {
//        Log.i(TAG, "initChatMessages() called, ids: "+currentUserID+", "+correspondentID);
//        messagingService.fetchChatMessages(currentUserID, correspondentID);
//    }
//
//
//    @Override
//    public void setFetchMessagesResponseCode(String responseCode) {
//        mFetchMessagesResponseCode = responseCode;
//    }
//
//
//    @Override
//    public void setFetchMessagesResponseMsg(String responseMsg) {
//        mFetchMessagesResponseMsg = responseMsg;
//    }
//
//
//    @Override
//    public void setChatMessagesList(List<ChatMessage> chatMessageList) {
//        mChatsMessages = chatMessageList;
//    }
//
//
//    @Override
//    public void onRequestFetchMessagesFinished() {
//        if (mView != null) {
//            if (mFetchMessagesResponseCode.equals(ApiResponseConstants.SERVER_FAILURE_CODE)){
//                mView.showServerFailureMsg(mFetchMessagesResponseMsg);
//            }
//
//            else if (mFetchMessagesResponseCode.equals(ApiResponseConstants.API_SUCCESS_CODE)){
//                Log.i(TAG, "loading chat messages");
//                mView.loadChatMessages(mChatsMessages);
//            }
//
//            // no chat messages exist for this chat yet
//            else {
//                mView.showFetchMessagesResponse(mFetchMessagesResponseMsg);
//            }
//        }
//    }


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

    //=======================================================================
//    @Override
//    public void setChatMessage(ChatMessage message) {
//        if (mView != null) {
//            Log.i(TAG, "mView is not null");
//            mView.updateChatMessagesList(message);
//        }
//    }
//
//
//    @Override
//    public void setMessageSentState(boolean value) {
//        if (mView != null) {
//            Log.i(TAG, "mView is not null");
//            mView.setChatMessageSentStatus(value);
//        }
//    }

    //=======================================================================

}
