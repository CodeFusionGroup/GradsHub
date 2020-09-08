package com.codefusiongroup.gradshub.groups.creategroup;

import com.codefusiongroup.gradshub.common.models.ResearchGroup;

import java.util.List;


public interface CreateGroupContract {

    interface ICreateGroupView {

        void showProgressBar();

        void hideProgressBar();

        void showGroupNameInputError(String message);

        void showGroupVisibilityError(String message);

        void showServerErrorResponse(String message);

        void showCreateGroupStatus(String message);

        void setGroupInviteCode(String groupInviteCode);

        void navigateToUserGroups();



    }

    interface ICreateGroupPresenter {

        boolean validateGroupInput(String groupName, String groupVisibility);

        void createGroup(ResearchGroup group);

        void onRequestToCreateGroupFinished();

        void setCreateGroupResponseCode(String responseCode);

        void setCreateGroupResponseMsg(String responseMsg);

        void privateVisibilitySelected();



    }

    interface ICreateGroupModel {


        void createUserGroup(ResearchGroup group);
    }


}
