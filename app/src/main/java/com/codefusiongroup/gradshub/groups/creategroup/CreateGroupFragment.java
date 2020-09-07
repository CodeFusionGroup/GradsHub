package com.codefusiongroup.gradshub.groups.creategroup;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.ResearchGroup;


public class CreateGroupFragment extends Fragment implements CreateGroupContract.ICreateGroupView, View.OnClickListener {


    private static final String TAG = "CreateGroupFragment";

    private AppCompatActivity mActivity; // used as context for navigating to user's groups screen
    private ProgressBar mProgressBar;
    private RadioButton mPublicRadioBtn, mPrivateRadioBtn;
    private EditText mGroupNameET;

    private String mGroupVisibility;
    private String mGroupInviteCode = null;
    private CreateGroupPresenter mPresenter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity)
            mActivity = (AppCompatActivity) context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new CreateGroupPresenter();
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {

        mPresenter.subscribe(this);
        Log.i(TAG, "create group presenter has subscribed to view --> CreateGroupFragment");

        mProgressBar = view.findViewById(R.id.progress_circular);
        mGroupNameET = view.findViewById(R.id.groupNameET);
        mPublicRadioBtn = view.findViewById(R.id.publicRB);
        mPrivateRadioBtn = view.findViewById(R.id.privateRB);
        Button doneBtn = view.findViewById(R.id.doneBtn);

        mPublicRadioBtn.setOnClickListener(this);
        mPrivateRadioBtn.setOnClickListener(this);
        doneBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch ( v.getId() ) {

            case R.id.publicRB:
                mGroupVisibility = mPublicRadioBtn.getText().toString();
                break;

            case R.id.privateRB:
                mGroupVisibility = mPrivateRadioBtn.getText().toString();
                break;

            case R.id.doneBtn:

                MainActivity mainActivity = (MainActivity) requireActivity();
                String groupAdmin = mainActivity.user.getEmail();
                String groupName = mGroupNameET.getText().toString().trim();

                if ( mPresenter.validateGroupInput(groupName, mGroupVisibility) ) {
                    if ( mPrivateRadioBtn.isChecked() ) {
                        mPresenter.privateVisibilitySelected();
                    }
                    mPresenter.createGroup(new ResearchGroup(groupAdmin, groupName, mGroupVisibility, mGroupInviteCode));
                }
                break;
        }

    }


    @Override
    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void showGroupNameInputError(String message) {
        mGroupNameET.setError(message);
        mGroupNameET.requestFocus();
    }


    @Override
    public void showGroupVisibilityError(String message) {
        GradsHubApplication.showToast(message);
    }


    @Override
    public void showServerErrorResponse(String message) {
        GradsHubApplication.showToast(message);
    }


    @Override
    public void showCreateGroupStatus(String message) {
        GradsHubApplication.showToast(message);
    }


    @Override
    public void setGroupInviteCode(String groupInviteCode) {
        mGroupInviteCode = groupInviteCode;
    }


    @Override
    public void navigateToUserGroups() {
        NavController navController = Navigation.findNavController(mActivity, R.id.main_nav_host_fragment);
        navController.navigate(R.id.action_createGroupFragment_to_myGroupsListFragment);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        // NOTE: setting fields to null makes the app crash
        //mActivity = null;
        //mCreateGroupPresenter.unsubscribe();
        //Log.i(TAG, "create group presenter has unsubscribed from View --> CreateGroupFragment");
    }

}
