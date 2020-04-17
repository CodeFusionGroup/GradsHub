package com.example.gradshub.model;


public class ResearchGroup {

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
