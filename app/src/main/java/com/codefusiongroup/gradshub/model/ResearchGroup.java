package com.codefusiongroup.gradshub.model;


import android.os.Parcel;
import android.os.Parcelable;

public class ResearchGroup implements Parcelable {

    private String groupAdmin;
    private String groupName;
    private String groupVisibility;
    private String groupInviteCode;
    private String groupID;


    public ResearchGroup(String groupAdmin, String groupName, String groupVisibility, String groupInviteCode) {
        this.groupAdmin = groupAdmin;
        this.groupName = groupName;
        this.groupVisibility = groupVisibility;
        this.groupInviteCode = groupInviteCode;
    }

    // we provide the default constructor so that we can also be able to set fields on a research group object if needed.
    public ResearchGroup() {}


    protected ResearchGroup(Parcel in) {
        groupAdmin = in.readString();
        groupName = in.readString();
        groupVisibility = in.readString();
        //groupInviteCode = in.readString();
        groupID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupAdmin);
        dest.writeString(groupName);
        dest.writeString(groupVisibility);
        //dest.writeString(groupInviteCode);
        dest.writeString(groupID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResearchGroup> CREATOR = new Creator<ResearchGroup>() {
        @Override
        public ResearchGroup createFromParcel(Parcel in) {
            return new ResearchGroup(in);
        }

        @Override
        public ResearchGroup[] newArray(int size) {
            return new ResearchGroup[size];
        }
    };

    public void setGroupAdmin(String groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    public String getGroupAdmin() {
        return groupAdmin;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public  void setGroupVisibility(String groupVisibility) {
        this.groupVisibility = groupVisibility;
    }

    public String getGroupVisibility() {return groupVisibility;}

    public void setGroupInviteCode(String groupInviteCode) {this.groupInviteCode = groupInviteCode;}

    public String getGroupInviteCode() {return  groupInviteCode;}

    public void setGroupID(String groupID) {this.groupID = groupID;}

    public String getGroupID() {return groupID;}

}
