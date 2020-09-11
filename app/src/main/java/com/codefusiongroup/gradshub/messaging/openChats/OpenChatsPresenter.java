package com.codefusiongroup.gradshub.messaging.openChats;


import android.util.Log;

import com.codefusiongroup.gradshub.common.BasePresenter2;
import com.codefusiongroup.gradshub.common.models.Chat;
import com.codefusiongroup.gradshub.common.network.ApiResponseCodes;

import java.util.ArrayList;
import java.util.List;


public class OpenChatsPresenter implements BasePresenter2<OpenChatsContract.IOpenChatsView>,
        OpenChatsContract.IOpenChatsPresenter {


    private static final String TAG = "OpenChatsPresenter";

    private String mUserOpenChatsResponseCode="1";//TODO: remove this initialization later, just testing
    private String mUserOpenChatsResponseMsg;

    private List<Chat> mOpenChatsList = new ArrayList<>();//TODO: remove this initialization later, just testing

    private OpenChatsContract.IOpenChatsView mView;
    private OpenChatsModel mModel = OpenChatsModel.newInstance(this);


    @Override
    public void attachView(OpenChatsContract.IOpenChatsView view) {
        mView = view;
    }


    @Override
    public void detachView(OpenChatsContract.IOpenChatsView view) {
        mView = null;
    }


    @Override
    public void initialiseUserOpenChats(String userID) {
        //mModel.fetchUserOpenChats(userID);
        //TODO: remove below testing code later and uncomment above line
        testMethod();
        onRequestUpdateUserOpenChatsFinished();
    }


    @Override
    public void setUserOpenChatsResponseCode(String responseCode) {
        mUserOpenChatsResponseCode = responseCode;
    }


    @Override
    public void setUserOpenChatsResponseMsg(String responseMsg) {
        mUserOpenChatsResponseMsg = responseMsg;
    }


    @Override
    public void setUserOpenChatsList(List<Chat> chatsList) {
        mOpenChatsList = chatsList;
    }


    @Override
    public void onRequestUpdateUserOpenChatsFinished() {

        if (mView != null ) {
            Log.i(TAG, "mView is not null");//testing for detaching presenter from view

            if( mUserOpenChatsResponseCode.equals(ApiResponseCodes.SERVER_FAILURE_CODE) ) {
                mView.showUserOpenChatsResponseMsg(mUserOpenChatsResponseMsg);
            }

            else if ( mUserOpenChatsResponseCode.equals(ApiResponseCodes.API_SUCCESS_CODE) ) {
                mView.updateUserOpenChats(mOpenChatsList);
            }

            // no open chats exist for this user
            else {
                mView.showUserOpenChatsResponseMsg(mUserOpenChatsResponseMsg);
            }
        }

    }


    public void testMethod() {

        mOpenChatsList.clear();
        Chat chat = new Chat();
        chat.setCorrespondentName("Kamo kamo");
        chat.setCorrespondentID("1");
        chat.setLatestMessage("hi there");
        chat.setMessageTimeStamp("10:00");
        mOpenChatsList.add(chat);

        Chat chat2 = new Chat();
        chat2.setCorrespondentName("Hazel 3D");
        chat2.setCorrespondentID("2");
        chat2.setLatestMessage("we have to add matrix projection");
        chat2.setMessageTimeStamp("10:30");
        mOpenChatsList.add(chat2);

        Chat chat3 = new Chat();
        chat3.setCorrespondentName("Hazel 2D");
        chat3.setCorrespondentID("2");
        chat3.setLatestMessage("set the renderer to the current context");
        chat3.setMessageTimeStamp("12:10");
        mOpenChatsList.add(chat3);

    }

}
