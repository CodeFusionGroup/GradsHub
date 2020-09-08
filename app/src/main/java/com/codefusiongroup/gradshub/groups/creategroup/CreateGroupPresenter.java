package com.codefusiongroup.gradshub.groups.creategroup;

import com.codefusiongroup.gradshub.common.BasePresenter;
import com.codefusiongroup.gradshub.common.models.ResearchGroup;


public class CreateGroupPresenter implements BasePresenter<CreateGroupContract.ICreateGroupView>, CreateGroupContract.ICreateGroupPresenter{


    private String mCreateGroupResponseCode = null;
    private String mCreateGroupResponseMsg = null;

    private final CreateGroupContract.ICreateGroupModel mCreateGroupModel = CreateGroupModel.newInstance(this);
    private CreateGroupContract.ICreateGroupView mCreateGroupView;


    @Override
    public void subscribe(CreateGroupContract.ICreateGroupView view) {
        mCreateGroupView = view;
    }

    @Override
    public void unsubscribe() {
        mCreateGroupView = null;
    }


    @Override
    public boolean validateGroupInput(String groupName, String groupVisibility) {

        if ( groupName.isEmpty() ) {
            mCreateGroupView.showGroupNameInputError("Not a valid group name!");
            return false;
        }

        int maxCharLength = 50;
        if ( groupName.length() > maxCharLength ) {
            mCreateGroupView.showGroupNameInputError("Exceeded the maximum number of characters allowed!");
            return false;
        }

        if ( groupVisibility == null ) {
            mCreateGroupView.showGroupVisibilityError("please indicate the group visibility!");
            return false;
        }

        return true;

    }


    @Override
    public void createGroup(ResearchGroup group) {

        mCreateGroupModel.createUserGroup(group);
        mCreateGroupView.showProgressBar();
    }


    @Override
    public void onRequestToCreateGroupFinished() {

        if (mCreateGroupView != null) {
            mCreateGroupView.hideProgressBar();

            String SERVER_FAILURE_CODE = "-100";
            String SUCCESS_CODE = "1";
            if ( mCreateGroupResponseCode.equals(SERVER_FAILURE_CODE) ) {
                mCreateGroupView.showServerErrorResponse(mCreateGroupResponseMsg);
            }
            else if ( mCreateGroupResponseCode.equals(SUCCESS_CODE) ) {
                mCreateGroupView.showCreateGroupStatus(mCreateGroupResponseMsg);
                mCreateGroupView.navigateToUserGroups();
            }
            else {
                // group name already exists
                mCreateGroupView.showCreateGroupStatus(mCreateGroupResponseMsg);
            }

        }
    }


    @Override
    public void setCreateGroupResponseCode(String responseCode) {
        mCreateGroupResponseCode = responseCode;
    }


    @Override
    public void setCreateGroupResponseMsg(String responseMsg) {
        mCreateGroupResponseMsg = responseMsg;
    }

    @Override
    public void privateVisibilitySelected() {
        String groupInviteCode = GenerateInviteCode.generateString();
        mCreateGroupView.setGroupInviteCode(groupInviteCode);
    }


}
