package com.codefusiongroup.gradshub.common.models;

public class ChatMessage {

    private String currentUserID;
    private String messageTimeStamp;
    private String messageBody;
    private String recipientUserID;


    public ChatMessage(String currentUserID, String messageTimeStamp, String messageBody, String recipientUserID) {
        this.currentUserID = currentUserID;
        this.messageTimeStamp = messageTimeStamp;
        this.messageBody = messageBody;
        this.recipientUserID = recipientUserID;
    }


    public ChatMessage() {}


    public String getMessageCreatorID() {
        return currentUserID;
    }

    public void setMessageCreatorID(String currentUserID) {
        this.currentUserID = currentUserID;
    }

    public String getRecipientUserID() { return recipientUserID; }

    public void setRecipientUserID(String recipientUserID) { this.recipientUserID = recipientUserID; }

    public String getMessageTime() {
        return messageTimeStamp;
    }

    public void setMessageTime(String messageTimeStamp) {
        this.messageTimeStamp = messageTimeStamp;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }


}
