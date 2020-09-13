package com.codefusiongroup.gradshub.messaging.openChats;

import android.util.Log;

import com.codefusiongroup.gradshub.common.models.Chat;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.messaging.MessagingAPI;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


public class OpenChatsModel implements OpenChatsContract.IOpenChatsModel {


    private static final String TAG = "OpenChatsModel";

    private OpenChatsContract.IOpenChatsPresenter mPresenter;
    private static OpenChatsModel instance;


    public OpenChatsModel(OpenChatsContract.IOpenChatsPresenter presenter) {
        mPresenter = presenter;
    }


    public static OpenChatsModel newInstance(OpenChatsPresenter presenter) {
        if (instance == null) {
            instance = new OpenChatsModel(presenter);
        }
        return instance;
    }

//
//    @Override
//    public void fetchUserOpenChats(String userID) {
//
//        HashMap<String, String> params = new HashMap<>();
//        params.put("user_id", userID);
//
//        MessagingAPI messagingAPI = ApiProvider.getMessageApiService();
//        messagingAPI.fetchOpenChats(params).enqueue(new Callback<JsonObject>() {
//
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//
//                if ( response.isSuccessful() ) {
//
//                    JsonObject jsonObject = response.body();
//
//                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {
//
//                        List<Chat> openChatsList = new ArrayList<>();
//                        JsonArray openChatsJA = jsonObject.getAsJsonArray("message");
//
//                        for (JsonElement jsonElement: openChatsJA) {
//                            JsonObject openChatJO = jsonElement.getAsJsonObject();
//                            Chat  chat = new Gson().fromJson(openChatJO, Chat.class);
//                            openChatsList.add(chat);
//                        }
//
//                        mPresenter.setUserOpenChatsResponseCode(ApiResponseConstants.API_SUCCESS_CODE);
//                        mPresenter.setUserOpenChatsList(openChatsList);
//                        mPresenter.onRequestUpdateUserOpenChatsFinished();
//                    }
//                    // no open chats exist for the user
//                    else {
//                        mPresenter.setUserOpenChatsResponseMsg("no open chats exits yet.");
//                    }
//                }
//
//                else {
//                    Log.i(TAG, "response.isSuccessful() = false");
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                mPresenter.setUserOpenChatsResponseCode(ApiResponseConstants.SERVER_FAILURE_CODE);
//                mPresenter.setUserOpenChatsResponseMsg("failed to load open chats, please swipe to refresh page or try again later.");
//                mPresenter.onRequestUpdateUserOpenChatsFinished();
//                t.printStackTrace();
//            }
//
//        });
//
//    }


}
