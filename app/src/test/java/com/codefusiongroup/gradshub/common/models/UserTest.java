package com.codefusiongroup.gradshub.common.models;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;

public class UserTest {

    @Mock
    User userMock;

    String firstName = "fname";
    String lastName = "lname";
    String email = "usermail@gmail.com";
    String phoneNumber = "0123456789";
    String academicStatus = "Masters";
    String password = "password";
    String fcmToken = "tokenMobile";
    String userName = "user";
    User user = new User(firstName, lastName, email, phoneNumber, academicStatus, password, fcmToken);
    User user2 = new User();
    @Test
    public void setUsername() {
        user2.setUsername(userName);
        assertEquals(userName, user2.getUsername());
    }

    @Test
    public void setFirstName() {
        user2.setFirstName(firstName);
        assertEquals(firstName, user2.getFirstName());
    }

    @Test
    public void getFirstName() {
        assertEquals(firstName, user.getFirstName());
    }

    @Test
    public void setLastName() {
        user2.setLastName(lastName);
        assertEquals(lastName, user2.getLastName());
    }

    @Test
    public void getLastName() {
        assertEquals(lastName, user.getLastName());
    }

    @Test
    public void setEmail() {
        user2.setEmail(email);
        assertEquals(email, user2.getEmail());
    }

    @Test
    public void getEmail() {
        assertEquals(email, user.getEmail());
    }

    @Test
    public void setPhoneNumber() {
        user2.setPhoneNumber(phoneNumber);
        assertEquals(phoneNumber, user2.getPhoneNumber());
    }

    @Test
    public void getPhoneNumber() {
        assertEquals(phoneNumber, user.getPhoneNumber());
    }

    @Test
    public void setAcademicStatus() {
        user2.setAcademicStatus(academicStatus);
        assertEquals(academicStatus, user2.getAcademicStatus());
    }

    @Test
    public void getAcademicStatus() {
        assertEquals(academicStatus, user.getAcademicStatus());
    }

    @Test
    public void getPassword() {
        assertEquals(password, user.getPassword());
    }

    @Test
    public void setUserID() {
        user2.setUserID("userID");
        assertEquals("userID", user2.getUserID());
    }

    @Test
    public void setProfilePicture() {
        user2.setProfilePicture("picture.jpg");
        assertEquals("picture.jpg", user2.getProfilePicture());
    }

    @Test
    public void getFullName() {
        String fullName = firstName + " " + lastName;
        assertEquals(fullName, user.getFullName());
    }

    @Test
    public void getFCMToken() {
        assertEquals(fcmToken, user.getFCMToken());
    }

    @Test
    public void setBlockedStatus() {
        user2.setBlockedStatus(true);
        assertTrue(user2.isBlocked());
    }

    @Test
    public void setFriendStatus() {
        user2.setFriendStatus(true);
        assertTrue(user2.isAFriend());

    }

    @Test
    public void describeContents() {
        assertEquals(0, user2.describeContents());
    }


}