package com.codefusiongroup.gradshub.messaging;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.authentication.AuthenticationActivity;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.models.ChatMessage;

import com.codefusiongroup.gradshub.common.network.ApiBaseResponse;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.messaging.chatMessages.ChatMessagesContract;
import com.codefusiongroup.gradshub.messaging.chatMessages.ChatMessagesFragment;
import com.codefusiongroup.gradshub.messaging.chatMessages.ChatMessagesPresenter;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;


public class MessagingService extends FirebaseMessagingService implements ChatMessagesContract.IChatMessagesModel {


    private static final String TAG = "MessagingService" ;

    private ChatMessagesContract.IChatMessagesPresenter mPresenter;
    private static MessagingService instance;


    public MessagingService() {
        // empty constructor needed to instantiate FCM functions
    }

    public MessagingService(ChatMessagesContract.IChatMessagesPresenter cmp) {
        mPresenter = cmp;
    }


    public static MessagingService newInstance(ChatMessagesPresenter cmp) {
        if (instance == null) {
            instance = new MessagingService(cmp);
        }
        return instance;
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        // Check if message contains a data payload.
        //=================================================
        if ( remoteMessage.getData().size() > 0 ) {

            //if user is currently not interacting with chat messages ????

            Log.i(TAG, "Message data payload: " + remoteMessage.getData() );

//            JsonObject jsonObject = new JsonObject();
//            JsonObject dataJO = jsonObject.getAsJsonObject("data");
//            ChatMessage message = new Gson().fromJson(dataJO, ChatMessage.class);
//            ChatMessagesFragment.getInstance().updateChatMessages(message);

            //mPresenter.setChatMessage(message);
        }
        //===================================================

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            String messageTitle = remoteMessage.getNotification().getTitle();
            String messageBody = remoteMessage.getNotification().getBody();
            sendFCMNotification(this, messageTitle, messageBody);

            Log.i(TAG, "Notification Title: " + messageTitle);
            Log.i(TAG, "Notification Body: " + messageBody);
        }

    }


    @Override
    public void onDeletedMessages() {
        // perform full syc with the app server to get messages
        Log.i(TAG, "Exceeded message limit");
    }


    @Override
    public void onMessageSent(@NonNull String messageID) {

        //mPresenter.setMessageSentState(true);
    }


    @Override
    public void onSendError(@NonNull String messageID, @NonNull Exception e) {
        //mPresenter.setMessageSentState(false);
    }


    // create and show message containing the received FCM message
    private void sendFCMNotification(Context context, String messageTitle, String messageBody) {

        Intent authActivityIntent = new Intent(context, AuthenticationActivity.class);
        authActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent authPendingIntent = PendingIntent.getActivity(context, 0, authActivityIntent, 0);

        // set notification properties
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "0")
                // Notification Channel Id is ignored for Android pre O (26).
                .setSmallIcon(R.mipmap.applogo) // shows app icon next to the notification

                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

                // Set the intent that will fire when the user taps the notification
                .setContentIntent(authPendingIntent)

                // automatically removes the notification when the user taps it.
                .setAutoCancel(true);

        // builder.build() returns the notification to be published
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            // TODO: proper handle of notification IDs
            // for now generate random integers
            Random notificationID = new Random();
            notificationManager.notify( notificationID.nextInt(100), builder.build() );
        }
        else {
            Log.i(TAG, "notificationManager is null");
        }

    }


    @Override
    public void onNewToken(@NonNull String token) {
        Log.i(TAG, "onNewToken() executed, new token is: " + token);
        //updateUserToken(userID, token);
    }


    private void updateUserToken(String userID, String token) {

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", userID);
        params.put("fcm_token", token);

        MessagingAPI messagingAPI = ApiProvider.getMessageApiService();

        messagingAPI.updateUserFCMToken(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {
                    JsonObject jsonObject = response.body();
                    GradsHubApplication.showToast("successfully updated your token for messaging.");
                }
                else {
                    Log.i(TAG, "response.isSuccessful() = false");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                GradsHubApplication.showToast("your token has been refreshed, please refresh page to continue receiving messages.");
                t.printStackTrace();
            }

        });

    }


    @Override
    public void sendUserMessage(ChatMessage message, String correspondentID) {

        HashMap<String, String> params = new HashMap<>();
        params.put( "sender_id", message.getCorrespondentID() );
        params.put( "message_text", message.getMessage() );
        params.put( "message_timestamp", message.getMessageTimeStamp() );
        params.put( "recipient_id",  correspondentID );

        MessagingAPI messagingAPI = ApiProvider.getMessageApiService();
        messagingAPI.insertMessage(params).enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if ( response.isSuccessful() ) {

                    JsonObject jsonObject = response.body();

                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {

                        mPresenter.setInsertMessageResponseCode(ApiResponseConstants.API_SUCCESS_CODE);
                        mPresenter.setInsertMessageResponseMsg(jsonObject.get("message").getAsString());
                        mPresenter.onRequestInsertMessageFinished();
                    }
                    else {
                        ApiBaseResponse apiDefault = new Gson().fromJson(jsonObject, ApiBaseResponse.class);
                        GradsHubApplication.showToast(apiDefault.getMessage());
                    }
                }

                else {
                    Log.i(TAG, "response.isSuccessful() = false");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mPresenter.setInsertMessageResponseCode(ApiResponseConstants.SERVER_FAILURE_CODE);
                mPresenter.setInsertMessageResponseMsg("failed to send message, please try again later.");
                mPresenter.onRequestInsertMessageFinished();
                t.printStackTrace();
            }

        });

    }


//    @Override
//    public void fetchChatMessages(String currentUserID, String correspondentID) {
//
//        HashMap<String, String> params = new HashMap<>();
//        params.put("user_id_one", currentUserID);
//        params.put("user_id_two", correspondentID);
//
//        MessagingAPI messagingAPI = ApiProvider.getMessageApiService();
//        messagingAPI.fetchChatMessages(params).enqueue(new Callback<JsonObject>() {
//
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//
//                if ( response.isSuccessful() ) {
//                    Log.i(TAG, "response.isSuccessful() = true");
//
//                    JsonObject jsonObject = response.body();
//
//                    // open chats have messages so no else case
//                    if ( jsonObject.get("success").getAsString().equals(ApiResponseConstants.API_SUCCESS_CODE) ) {
//
//                        List<ChatMessage> chatMessagesList = new ArrayList<>();
//                        JsonArray chatMessagesJA = jsonObject.getAsJsonArray("message");
//
//                        for (JsonElement jsonElement: chatMessagesJA) {
//                            JsonObject chatMessageJO = jsonElement.getAsJsonObject();
//                            ChatMessage  message = new Gson().fromJson(chatMessageJO, ChatMessage.class);
//                            chatMessagesList.add(message);
//                        }
//
//                        mPresenter.setFetchMessagesResponseCode(ApiResponseConstants.API_SUCCESS_CODE);
//                        mPresenter.setChatMessagesList(chatMessagesList);
//                    }
//                    else {
//                        ApiBaseResponse apiDefault = new Gson().fromJson(jsonObject, ApiBaseResponse.class);
//                        mPresenter.setFetchMessagesResponseCode( apiDefault.getStatusCode() );
//                        mPresenter.setFetchMessagesResponseMsg( "no messages exist for this chat yet." );
//                    }
//
//                    mPresenter.onRequestFetchMessagesFinished();
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
//                mPresenter.setFetchMessagesResponseCode(ApiResponseConstants.SERVER_FAILURE_CODE);
//                mPresenter.setFetchMessagesResponseMsg("failed to load messages, please swipe to refresh or try again later.");
//                mPresenter.onRequestFetchMessagesFinished();
//                t.printStackTrace();
//            }
//
//        });
//
//    }

}
