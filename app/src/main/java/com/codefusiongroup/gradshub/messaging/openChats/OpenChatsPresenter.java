package com.codefusiongroup.gradshub.messaging.openChats;


import android.util.Log;

import com.codefusiongroup.gradshub.common.BasePresenter2;
import com.codefusiongroup.gradshub.common.models.Chat;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;

import java.util.ArrayList;
import java.util.List;


public class OpenChatsPresenter implements BasePresenter2<OpenChatsContract.IOpenChatsView>,
        OpenChatsContract.IOpenChatsPresenter {


    private static final String TAG = "OpenChatsPresenter";

    private String mUserOpenChatsResponseCode;
    private String mUserOpenChatsResponseMsg;

    private List<Chat> mOpenChatsList;

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

//
//    @Override
//    public void initialiseUserOpenChats(String userID) {
//        mModel.fetchUserOpenChats(userID);
//    }
//
//
//    @Override
//    public void setUserOpenChatsResponseCode(String responseCode) {
//        mUserOpenChatsResponseCode = responseCode;
//    }
//
//
//    @Override
//    public void setUserOpenChatsResponseMsg(String responseMsg) {
//        mUserOpenChatsResponseMsg = responseMsg;
//    }
//
//
//    @Override
//    public void setUserOpenChatsList(List<Chat> chatsList) {
//        mOpenChatsList = chatsList;
//    }
//
//
//    @Override
//    public void onRequestUpdateUserOpenChatsFinished() {
//
//        if (mView != null ) {
//            Log.i(TAG, "mView is not null");//testing for detaching presenter from view
//
//            if( mUserOpenChatsResponseCode.equals(ApiResponseConstants.SERVER_FAILURE_CODE) ) {
//                mView.showUserOpenChatsResponseMsg(mUserOpenChatsResponseMsg);
//            }
//
//            else if ( mUserOpenChatsResponseCode.equals(ApiResponseConstants.API_SUCCESS_CODE) ) {
//                mView.updateUserOpenChats(mOpenChatsList);
//            }
//
//            // no open chats exist for this user
//            else {
//                mView.showUserOpenChatsResponseMsg(mUserOpenChatsResponseMsg);
//            }
//        }
//
//    }

}
