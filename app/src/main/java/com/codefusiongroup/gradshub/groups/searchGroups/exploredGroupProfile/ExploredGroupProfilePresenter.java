package com.codefusiongroup.gradshub.groups.searchGroups.exploredGroupProfile;

import com.codefusiongroup.gradshub.common.BasePresenter;
import com.codefusiongroup.gradshub.common.models.ResearchGroup;


public class ExploredGroupProfilePresenter implements
        BasePresenter<ExploredGroupProfileContract.IAvailableGroupProfileView>,
        ExploredGroupProfileContract.IAvailableGroupProfilePresenter {


    private String mJoinGroupResponseCode = null;
    private String mJoinGroupResponseMsg = null;

    private final ExploredGroupProfileContract.IAvailableGroupProfileModel mModel = ExploredGroupProfileModel.newInstance(this);
    private ExploredGroupProfileContract.IAvailableGroupProfileView mView;


    @Override
    public void addUserToGroup(String userID, ResearchGroup group, String groupInviteCode) {
        mModel.requestToJoinGroup(userID, group, groupInviteCode);
        mView.showProgressBar();
    }


    @Override
    public void onRequestToJoinGroupFinished() {

        if (mView != null) {

            String SERVER_FAILURE_CODE = "-100";
            String SUCCESS_CODE = "1";
            if ( mJoinGroupResponseCode.equals(SERVER_FAILURE_CODE) ) {
                mView.showServerErrorResponse(mJoinGroupResponseMsg);
            }
            else if ( mJoinGroupResponseCode.equals(SUCCESS_CODE) ) {
                mView.showJoinGroupResponseMsg(mJoinGroupResponseMsg);
                mView.navigateToUserGroups();
            }
            else {
                // incorrect invite code
                mView.showJoinGroupResponseMsg(mJoinGroupResponseMsg);
            }

            mView.hideProgressBar();

        }

    }


    @Override
    public void setJoinGroupResponseCode(String responseCode) {
        mJoinGroupResponseCode = responseCode;
    }


    @Override
    public void setJoinGroupResponseMsg(String responseMsg) {
        mJoinGroupResponseMsg = responseMsg;
    }


    @Override
    public void subscribe(ExploredGroupProfileContract.IAvailableGroupProfileView view) {
        mView = view;
    }


    @Override
    public void unsubscribe() {
        mView = null;
    }

}
