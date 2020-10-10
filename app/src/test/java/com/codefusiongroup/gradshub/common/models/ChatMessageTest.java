package com.codefusiongroup.gradshub.common.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChatMessageTest {

    //String message, String messageTimeStamp, String correspondentID
    ChatMessage chatMessage = new ChatMessage("Hello", "Today", "User1");
    @Test
    public void getCorrespondentID() {
        assertEquals("User1", chatMessage.getCorrespondentID());
    }

    @Test
    public void setCorrespondentID() {
        chatMessage.setCorrespondentID("Another user");
        assertEquals("Another user", chatMessage.getCorrespondentID());
    }

    @Test
    public void getMessage() {
        assertEquals("Hello",chatMessage.getMessage());
    }

    @Test
    public void setMessage() {
        chatMessage.setMessage("Good");
        assertEquals("Good", chatMessage.getMessage());
    }

    @Test
    public void getMessageTimeStamp() {
        assertEquals("Today", chatMessage.getMessageTimeStamp());
    }

    @Test
    public void setMessageTimeStamp() {
        chatMessage.setMessageTimeStamp("Now");
        assertEquals("Now",chatMessage.getMessageTimeStamp());
    }
}