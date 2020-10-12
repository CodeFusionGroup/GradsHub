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
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.repositories.UserRepositoryImpl;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.UserPreferences;
import com.codefusiongroup.gradshub.common.models.ChatMessage;

import com.codefusiongroup.gradshub.common.network.ApiBaseResponse;
import com.codefusiongroup.gradshub.common.network.ApiProvider;
import com.codefusiongroup.gradshub.common.network.ApiResponseConstants;
import com.codefusiongroup.gradshub.messaging.chatMessages.ChatMessagesContract;
import com.codefusiongroup.gradshub.messaging.chatMessages.ChatMessagesPresenter;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;


public class MessagingService extends FirebaseMessagingService implements ChatMessagesContract.IChatMessagesModel {

    private static final String TAG = "MessagingService" ;

    private static MessagingService instance;
    private ChatMessagesContract.IChatMessagesPresenter mPresenter;

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
        if ( remoteMessage.getData().size() > 0 ) {
            Log.i(TAG, "Message data payload: " + remoteMessage.getData() );
        }

        // Check if message contains a notification payload and notify if true.
        if (remoteMessage.getNotification() != null) {
            String messageTitle = remoteMessage.getNotification().getTitle();
            String messageBody = remoteMessage.getNotification().getBody();
            sendFCMNotification(this, messageTitle, messageBody);
        }

    }


    @Override
    public void onNewToken(@NonNull String token) {
        // update the token immediately if network request is successful otherwise save it and update
        // next time the user logs in. This ensures that the token of the user is up to date since it
        // can change for various reasons its best to update it immediately.
        String userID = UserPreferences.getInstance().getUserID(GradsHubApplication.getContext());
        if ( !userID.equals("no ID set") ) {
            Log.d(TAG, "user id from preferences: "+userID);
            UserRepositoryImpl.getInstance().updateUserToken(userID, token);
        }

    }

    @Override
    public void onDeletedMessages() {
        Log.i(TAG, "onDeletedMessages() --> Exceeded message limit");
    }

    @Override
    public void onMessageSent(@NonNull String messageID) {
        Log.i(TAG, "onMessageSent() --> message sent successfully");
    }

    @Override
    public void onSendError(@NonNull String messageID, @NonNull Exception e) {
        Log.i(TAG, "onSendError() --> error sending message");
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
                    Log.d(TAG, "sendUserMessage() --> response.isSuccessful() = true");

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
                    mPresenter.setInsertMessageResponseCode(ApiResponseConstants.SERVER_FAILURE_CODE);
                    mPresenter.setInsertMessageResponseMsg("Failed to send message, please try again later.");
                    Log.d(TAG, "sendUserMessage() --> response.isSuccessful() = false");
                    Log.d(TAG, "error code: " +response.code() );
                    Log.d(TAG, "error message: " +response.message() );
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                mPresenter.setInsertMessageResponseCode(ApiResponseConstants.SERVER_FAILURE_CODE);
                mPresenter.setInsertMessageResponseMsg("Failed to send message, please try again later.");
                mPresenter.onRequestInsertMessageFinished();
                Log.d(TAG, "sendUserMessage() --> onFailure executed, error: ", t);
            }

        });

    }


    // show notification for the message received on the receiver's device
    private void sendFCMNotification(Context context, String messageTitle, String messageBody) {

        Intent authActivityIntent = new Intent(context, AuthenticationActivity.class);
        authActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent authPendingIntent = PendingIntent.getActivity(context, 0, authActivityIntent, 0);

        // set notification properties
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "0")
                // Notification Channel Id is ignored for Android pre O (26).
                .setSmallIcon(R.mipmap.applogo)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(authPendingIntent) // intent that will fire when the user taps the notification opens app
                .setAutoCancel(true) // automatically removes the notification when the user taps it.
                .setOngoing(false); // dismiss notification on swipe gesture

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            Random random = new Random();
            int notificationID = random.nextInt(Integer.MAX_VALUE); // ensure uniqueness of notifications
            notificationManager.notify( notificationID, builder.build() ); // builder.build() returns the notification to be published
        }
        else {
            Log.i(TAG, "sendFCMNotification() --> notificationManager is null, could not send notification");
        }

    }

}
