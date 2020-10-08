package com.codefusiongroup.gradshub.common.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class PostTest {

    Post post = new Post();
    Post post2 = new Post("10/10/2020", "Post Constructors", "A post with constructors");
    Post post3 = new Post("10/10/2020", "Post Constructors", "A post with constructors", "New File");

    @Test
    public void testGetPostDate(){
        post.setPostDate("10/01/2020");
        String actual = post.getPostDate();
        String expected = "10/01/2020";
        assertEquals(expected,actual);
    }

    @Test
    public void testPostGetSubject(){
        post.setPostSubject("NewPost");
        String actual = post.getPostSubject();
        String expected = "NewPost";
        assertEquals(expected,actual);
    }

    @Test
    public void testGetPostDescription(){
        post.setPostDescription("We Test");
        String actual = post.getPostDescription();
        String expected = "We Test";
        assertEquals(expected,actual);
    }

    @Test
    public void testGetPostID(){
        post.setPostID("NEWPOST2");
        String actual = post.getPostID();
        String expected = "NEWPOST2";
        assertEquals(expected,actual);

    }

    @Test
    public void testGetPostCreator(){
        post.setPostCreator("GradsHub");
        String actual = post.getPostCreator();
        String expected = "GradsHub";
        assertEquals(expected,actual);
    }

    @Test
    public void testDescribeContents(){
        int actual = post.describeContents();
        assertEquals(0,actual);
    }

    @Test
    public void testGetPostLikesCount(){
        post.setPostLikesCount(4);
        int actual = post.getPostLikesCount();
        int expected = 4;
        assertEquals(expected,actual);
    }

    @Test
    public void testGetPostCommentsCount(){
        post.setPostCommentsCount(1);
        int actual = post.getPostCommentsCount();
        int expected = 1;
        assertEquals(expected,actual);
    }

    @Test
    public void testGetFileName(){
        String expected = "New File";
        String actual = post3.getPostFileName();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetPostFileName(){
        String expected = "New file";
        post2.setPostFileName(expected);
        assertEquals(expected, post2.getPostFileName());
    }
}