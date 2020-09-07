package com.codefusiongroup.gradshub.groups.searchGroups.exploredGroupProfile;

import com.codefusiongroup.gradshub.common.models.ResearchGroup;

public interface ExploredGroupProfileContract {


    interface IAvailableGroupProfileView {

        void showProgressBar();

        void hideProgressBar();

        void showServerErrorResponse(String message);

        void navigateToUserGroups();

        void showJoinGroupResponseMsg(String message);

        // once the user has joined a group successfully then remove that group from the list
        //void updateExploredGroupList();

    }

    interface IAvailableGroupProfilePresenter {

        void addUserToGroup(String userID, ResearchGroup group, String groupInviteCode);

        void onRequestToJoinGroupFinished();

        void setJoinGroupResponseCode(String responseCode);

        void setJoinGroupResponseMsg(String responseMsg);

    }

    interface IAvailableGroupProfileModel{

        void requestToJoinGroup(String userID, ResearchGroup group,  String groupInviteCode);
    }


}
