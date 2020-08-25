package com.codefusiongroup.gradshub.model;

public class ChatMessage {

    private String messageCreator;
    private String messageTime;
    private String messageBody;


    public ChatMessage(String messageCreator, String messageTime, String messageBody) {
        this.messageCreator = messageCreator;
        this.messageTime = messageTime;
        this.messageBody = messageBody;
    }


    public ChatMessage() {}


    public String getMessageCreator() {
        return messageCreator;
    }

    public void setMessageCreator(String messageCreator) {
        this.messageCreator = messageCreator;
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
