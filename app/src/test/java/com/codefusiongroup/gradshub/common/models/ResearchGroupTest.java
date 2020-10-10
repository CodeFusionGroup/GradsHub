package com.codefusiongroup.gradshub.common.models;

import android.os.Parcel;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResearchGroupTest {
    String admin = "Admin";
    String name = "Group";
    String visibility = "visible";
    String code = "12345";
    ResearchGroup researchGroup = new ResearchGroup(admin, name, visibility, code);
    ResearchGroup researchGroup2 = new ResearchGroup();

    //Note: Still returns 0 by default
    @Test
    public void describeContents() {
        assertEquals(0 ,researchGroup.describeContents());
    }

    @Test
    public void setGroupAdmin() {
        researchGroup2.setGroupAdmin("Admin");
        assertEquals(admin, researchGroup2.getGroupAdmin());
    }

    @Test
    public void getGroupAdmin() {
        assertEquals(admin, researchGroup.getGroupAdmin());
    }

    @Test
    public void setGroupName() {
        researchGroup2.setGroupName(name);
        assertEquals(name, researchGroup2.getGroupName());
    }

    @Test
    public void getGroupName() {
        assertEquals(name, researchGroup.getGroupName());
    }

    @Test
    public void setGroupVisibility() {
        researchGroup2.setGroupVisibility(visibility);
        assertEquals(visibility, researchGroup2.getGroupVisibility());
    }

    @Test
    public void getGroupVisibility() {
        assertEquals(visibility, researchGroup.getGroupVisibility());
    }

    @Test
    public void setGroupInviteCode() {
        researchGroup2.setGroupInviteCode("12345");
        assertEquals("12345", researchGroup2.getGroupInviteCode());
    }

    @Test
    public void getGroupInviteCode() {
        assertEquals(code, researchGroup.getGroupInviteCode());
    }

    @Test
    public void setGroupID() {
        researchGroup2.setGroupID("1");
        assertEquals("1", researchGroup2.getGroupID());
    }

}