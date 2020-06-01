package com.example.gradshub.main.postcomments;

public class Comment {

    private String commentCreator;
    private String comment;
    private String commentDate;


    public Comment(String commentCreator, String comment, String commentDate) {
        this.commentCreator = commentCreator;
        this.comment = comment;
        this.commentDate = commentDate;
    }

    public Comment() {}


    public String getCommentCreator() { return commentCreator; }

    public void setCommentCreator(String commentCreator) { this.commentCreator = commentCreator; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public String getCommentDate() { return commentDate; }

    public void setCommentDate(String commentDate) { this.commentDate = commentDate; }


}
