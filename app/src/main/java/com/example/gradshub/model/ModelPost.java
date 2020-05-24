package com.example.gradshub.model;

public class ModelPost {
    String postId,postTitle,postDescr,postImage,postTime,userId,userEmail,userDp,uName;

    public ModelPost(){

    }

    public ModelPost(String postId, String postTitle, String postDescr, String postImage, String postTime, String userId, String userEmail, String userDp, String uName) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postDescr = postDescr;
        this.postImage = postImage;
        this.postTime = postTime;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userDp = userDp;
        this.uName = uName;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDescr() {
        return postDescr;
    }

    public void setPostDescr(String postDescr) {
        this.postDescr = postDescr;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserDp() {
        return userDp;
    }

    public void setUserDp(String userDp) {
        this.userDp = userDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}
