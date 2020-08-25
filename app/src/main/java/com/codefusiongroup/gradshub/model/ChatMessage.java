package com.codefusiongroup.gradshub.model;

public class ChatMessage {

    private String userID;
    private String messageTime;
    private String messageBody;


    public ChatMessage(String userID, String messageTime, String messageBody) {
        this.userID = userID;
        this.messageTime = messageTime;
        this.messageBody = messageBody;
    }


    public ChatMessage() {}


    public String getMessageCreator() {
        return userID;
    }

    public void setMessageCreator(String userID) {
        this.userID = userID;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

}
