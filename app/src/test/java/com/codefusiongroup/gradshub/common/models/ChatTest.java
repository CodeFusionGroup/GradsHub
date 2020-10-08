package com.codefusiongroup.gradshub.common.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChatTest {
    //String correspondentName, String latestMessage, String messageTimeStamp
    Chat chat = new Chat("Tester", "This is a test", "10:11:11");
    Chat chat2 = new Chat();

    @Test
    public void  testSetChatID(){
        String expected = "FirstChat";
        chat2.setChatID("FirstChat");
        String actual = chat2.getChatID();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetChatID(){
        chat2.setChatID("New Test");
        assertEquals("New Test", chat2.getChatID());
    }

    @Test
    public void testGetCorrespondenceName(){
        assertEquals("Tester", chat.getCorrespondentName());
    }

    @Test
    public void testSetCorrespondenceName(){
        chat2.setCorrespondentName("Tester2");
        assertEquals("Tester2", chat2.getCorrespondentName());
    }

    @Test
    public void testGetCorrespondentID(){
        chat2.setCorrespondentID("Chattee");
        assertEquals("Chattee", chat2.getCorrespondentID());
    }

    @Test
    public void testGetMessageTimeStamp(){
        chat2.setMessageTimeStamp("Now");
        assertEquals("Now", chat2.getMessageTimeStamp());
    }

    @Test
    public void testSetLatestMessage(){
        chat2.setLatestMessage("Hey");
        assertEquals("Hey", chat2.getLatestMessage());
    }

}