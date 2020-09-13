package com.codefusiongroup.gradshub.posts.postcomments;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommentTest {

    private Comment comment = new Comment("commentCreator", "comment", "commentDate");
    private Comment comment2 = new Comment();
    @Test
    public void getCommentCreatorTest() {
        assertEquals("commentCreator", comment.getCommentCreator());
    }

    @Test
    public void setCommentCreatorTest() {
        comment2.setCommentCreator("commentCreator2");
        assertEquals("commentCreator2", comment2.getCommentCreator());
    }

    @Test
    public void setComment() {
        comment2.setComment("comment 2");
        assertEquals("comment 2", comment2.getComment());
    }

    @Test
    public void getCommentDate() {
        assertEquals("commentDate", comment.getCommentDate());
    }

    @Test
    public void setCommentDate() {
        comment2.setCommentDate("date");
        assertEquals("date", comment2.getCommentDate());
    }
}