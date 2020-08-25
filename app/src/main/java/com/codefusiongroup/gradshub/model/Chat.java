package com.codefusiongroup.gradshub.model;

import android.graphics.Bitmap;

public class Chat {

    //private Bitmap userImage;
    private String contactName;
    private String latestMessage;
    private String latestMessageTime;


    public Chat(String contactName, String latestMessage, String latestMessageTime) {
        //this.userImage = userImage;
        this.contactName = contactName;
        this.latestMessage = latestMessage;
        this.latestMessageTime = latestMessageTime;
    }


//    public Bitmap getUserImage() {
//        return userImage;
//    }
//
//    public void setUserImage(Bitmap userImage) {
//        this.userImage = userImage;
//    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public String getLatestMessageTime() {
        return latestMessageTime;
    }

    public void setLatestMessageTime(String latestMessageTime) {
        this.latestMessageTime = latestMessageTime;
    }


}
