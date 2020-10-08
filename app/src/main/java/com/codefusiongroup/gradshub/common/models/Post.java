package com.codefusiongroup.gradshub.common.models;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.VisibleForTesting;

import com.google.gson.annotations.SerializedName;

public class Post implements Parcelable {

    private String postID;

    @SerializedName("POST_CREATOR")
    private String postCreator;

    @SerializedName("POST_DATE")
    private String postDate;

    @SerializedName("POST_SUBJECT")
    private String postSubject;

    @SerializedName("POST_DESCRIPTION")
    private String postDescription;

    @SerializedName("POST_FILE")
    private String postFileName;

    @SerializedName("POST_LIKES")
    private int postLikesCount = 0;

    @SerializedName("POST_COMMENTS")
    private int postCommentsCount = 0;


    //Constructors
    public Post(String postDate, String postSubject, String postDescription) {
        this.postDate = postDate;
        this.postSubject = postSubject;
        this.postDescription = postDescription;
    }

    // this constructor is for a post with a file attachment
    public Post(String postDate, String postSubject, String postDescription, String postFileName) {
        this.postDate = postDate;
        this.postSubject = postSubject;
        this.postDescription = postDescription;
        this.postFileName = postFileName;
    }

    public Post() {}


    //Getters and setters
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

    public void setPostDescription(String postDescription) { this.postDescription = postDescription; }

    public int getPostLikesCount() { return postLikesCount; }

    public void setPostLikesCount(int postLikesCount) { this.postLikesCount += postLikesCount; }

    public int getPostCommentsCount() { return postCommentsCount; }

    public void setPostCommentsCount(int postCommentsCount) { this.postCommentsCount += postCommentsCount; }

    public String getPostFileName() {return postFileName; }

    public void setPostFileName(String postFileName) { this.postFileName = postFileName; }


    @VisibleForTesting
    protected Post(Parcel in) {
        postID = in.readString();
        postCreator = in.readString();
        postDate = in.readString();
        postSubject = in.readString();
        postDescription = in.readString();
        //=============================
        //postLikesCount = in.readInt();
        //============================
        postCommentsCount = in.readInt();
        postFileName = in.readString();
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
        dest.writeString(postFileName);
        //================================
        //dest.writeInt(postLikesCount);
        //dest.writeInt(postCommentsCount);
    }

}
