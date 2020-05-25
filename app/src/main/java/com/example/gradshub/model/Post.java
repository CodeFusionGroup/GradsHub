package com.example.gradshub.model;


public class Post {

    private String postID;
    private String postCreator;
    private String postSubject;
    private String postDescription;
    private String postDate;

    // have to change these to int types but for now just String types will do
    private String postLikesCount = "0";
    private String postCommentsCount = "0";


    public String getPostID() { return postID; }

    public void setPostID(String postID) { this.postID = postID; }

    public String getPostCreator() {
        return postCreator;
    }

    public void setPostCreator(String postCreator) {
        this.postCreator = postCreator;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostSubject() { return postSubject; }

    public void setPostSubject(String postSubject) { this.postSubject = postSubject; }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public void setPostLikesCount(String postLikesCount) { this.postLikesCount = postLikesCount; }

    public String getPostLikesCount() { return postLikesCount; }

    public void setPostCommentsCount(String postCommentsCount) { this.postCommentsCount = postCommentsCount; }

    public String getPostCommentsCount() { return postCommentsCount; }


    public Post(String postDate, String postSubject, String postDescription) {
        this.postDate = postDate;
        this.postSubject = postSubject;
        this.postDescription = postDescription;
    }

    public Post() {}

}
