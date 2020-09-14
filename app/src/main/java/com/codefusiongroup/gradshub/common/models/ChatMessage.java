package com.codefusiongroup.gradshub.common.models;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {


    @SerializedName("MESSAGE_TEXT")
    private String message;

    @SerializedName("MESSAGE_TIMESTAMP")
    private String messageTimeStamp;

    @SerializedName("USER_ID")
    private String correspondentID;


    public ChatMessage(String message, String messageTimeStamp, String correspondentID) {
        this.message = message;
        this.messageTimeStamp = messageTimeStamp;
        this.correspondentID = correspondentID;
    }


    public String getCorrespondentID() { return correspondentID; }


    public void setCorrespondentID(String correspondentID) { this.correspondentID = correspondentID; }


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public String getMessageTimeStamp() {
        return messageTimeStamp;
    }


    public void setMessageTimeStamp(String messageTimeStamp) {
        this.messageTimeStamp = messageTimeStamp;
    }

}
