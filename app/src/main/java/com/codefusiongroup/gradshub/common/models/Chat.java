package com.codefusiongroup.gradshub.common.models;


import com.google.gson.annotations.SerializedName;

public class Chat {

    @SerializedName("FULL_NAME")
    private String correspondentName;

    @SerializedName("MESSAGE_TEXT")
    private String latestMessage;

    @SerializedName("MESSAGE_TIMESTAMP")
    private String messageTimeStamp;

    @SerializedName("USER_ID")
    private String correspondentID;


    public Chat(String correspondentName, String latestMessage, String messageTimeStamp) {
        this.correspondentName = correspondentName;
        this.latestMessage = latestMessage;
        this.messageTimeStamp = messageTimeStamp;
    }

    public Chat() {
        // empty constructor
    }


    public String getCorrespondentName() {
        return correspondentName;
    }

    public void setCorrespondentName(String correspondentName) {
        this.correspondentName = correspondentName;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public String getMessageTimeStamp() {
        return messageTimeStamp;
    }

    public void setMessageTimeStamp(String messageTimeStamp) {
        this.messageTimeStamp = messageTimeStamp;
    }

    public void setCorrespondentID(String correspondentID){ this.correspondentID = correspondentID; }

    public String getCorrespondentID() { return correspondentID; }

}
