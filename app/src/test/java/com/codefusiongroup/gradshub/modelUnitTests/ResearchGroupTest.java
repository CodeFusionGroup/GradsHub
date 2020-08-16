package com.codefusiongroup.gradshub.modelUnitTests;


import com.codefusiongroup.gradshub.model.ResearchGroup;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ResearchGroupTest {

    @Test
    public void ResearchGroupConstcutorTest(){
        ResearchGroup researchGroupTest = new ResearchGroup("Admin", "TestGroup", "visible", "code4");
        String expectedGroupName = "TestGroup";
        String expectedGroupAdmin = "Admin";
        String expectedGroupVisibility = "visible";
        String expectedGroupInviteCode = "code4";
        assertEquals(expectedGroupAdmin, researchGroupTest.getGroupAdmin());
        assertEquals(expectedGroupName, researchGroupTest.getGroupName());
        assertEquals(expectedGroupVisibility, researchGroupTest.getGroupVisibility());
        assertEquals(expectedGroupInviteCode, researchGroupTest.getGroupInviteCode());

    }

    //No params constructor
    @Test
    public void ResearchGroupTest(){
        ResearchGroup researchGroupTest = new ResearchGroup();

        researchGroupTest.setGroupAdmin("Admin");
        researchGroupTest.setGroupName("TestGroup");
        researchGroupTest.setGroupVisibility("visible");
        researchGroupTest.setGroupInviteCode("code4");
        researchGroupTest.setGroupID("ID1");

        String expectedGroupName = "TestGroup";
        String expectedGroupAdmin = "Admin";
        String expectedGroupVisibility = "visible";
        String expectedGroupInviteCode = "code4";
        String expectedGroupdID = "ID1";
        assertEquals(expectedGroupAdmin, researchGroupTest.getGroupAdmin());
        assertEquals(expectedGroupName, researchGroupTest.getGroupName());
        assertEquals(expectedGroupVisibility, researchGroupTest.getGroupVisibility());
        assertEquals(expectedGroupInviteCode, researchGroupTest.getGroupInviteCode());
        assertEquals(expectedGroupdID, researchGroupTest.getGroupID());
        assertEquals(0, researchGroupTest.describeContents());

    }
}
