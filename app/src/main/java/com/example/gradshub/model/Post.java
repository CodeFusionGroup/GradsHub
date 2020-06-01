package com.example.gradshub.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {

    private String postID;
    private String postCreator;
    private String postDate;
    private String postSubject;
    private String postDescription;
    private int postLikesCount = 0;
    private int postCommentsCount = 0;


    public String getPostID() { return postID; }

    public void setPostID(String postID) { this.postID = postID; }

    public String getPostCreator() {
        return postCreator;
    }

    public void setPostCreator(String postCreator) {
        this.postCreator = postCreator;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostSubject() { return postSubject; }

    public void setPostSubject(String postSubject) { this.postSubject = postSubject; }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public int getPostLikesCount() { return postLikesCount; }

    public void setPostLikesCount(int postLikesCount) { this.postLikesCount += postLikesCount; }

    public int getPostCommentsCount() { return postCommentsCount; }

    public void setPostCommentsCount(int postCommentsCount) { this.postCommentsCount += postCommentsCount; }


    public Post(String postDate, String postSubject, String postDescription) {
        this.postDate = postDate;
        this.postSubject = postSubject;
        this.postDescription = postDescription;
    }

    public Post() {}


    protected Post(Parcel in) {
        postID = in.readString();
        postCreator = in.readString();
        postDate = in.readString();
        postSubject = in.readString();
        postDescription = in.readString();
        //postLikesCount = in.readInt();
        postCommentsCount = in.readInt();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postID);
        dest.writeString(postCreator);
        dest.writeString(postDate);
        dest.writeString(postSubject);
        dest.writeString(postDescription);
        //dest.writeInt(postLikesCount);
        //dest.writeInt(postCommentsCount);
    }
}
