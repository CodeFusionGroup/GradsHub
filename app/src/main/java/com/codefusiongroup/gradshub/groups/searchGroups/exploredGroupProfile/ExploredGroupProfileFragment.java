package com.codefusiongroup.gradshub.groups.searchGroups.exploredGroupProfile;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.common.GradsHubApplication;
import com.codefusiongroup.gradshub.common.MainActivity;
import com.codefusiongroup.gradshub.common.models.ResearchGroup;


public class ExploredGroupProfileFragment extends Fragment implements ExploredGroupProfileContract.IAvailableGroupProfileView {


    private static final String TAG = "ExploredGroupProfile";

    private AppCompatActivity mActivity; // used as context for navigating to user's groups screen
    private View mView;
    private ProgressBar mProgressBar;
    private Button mJoinBtn;
    private TextView mGroupNameTV;

    // alert dialog fields
    private AlertDialog.Builder mBuilder;
    private View mAlertDialogView;
    private EditText mInviteCodeET;

    private ResearchGroup mGroup;
    private MainActivity mainActivity;
    private ExploredGroupProfilePresenter mPresenter;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity)
            mActivity = (AppCompatActivity) context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new ExploredGroupProfilePresenter();
        mainActivity = (MainActivity) requireActivity();

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            mGroup = bundle.getParcelable("group_item");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_explored_group_profile, container, false);
        return mView;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        initViewComponents(view);

        mPresenter.subscribe(this);

        mGroupNameTV.setText(mGroup.getGroupName());

        mBuilder.setView(mAlertDialogView)

                .setPositiveButton("CONFIRM", (dialog, which) -> {
                    // entering nothing is the same as entering an invalid invite code.
                    String inviteCode = mInviteCodeET.getText().toString().trim();
                    mPresenter.addUserToGroup( mainActivity.user.getUserID(), mGroup, inviteCode );
                    mInviteCodeET.setText("");
                })

                .setNegativeButton("CANCEL", (dialog, which) -> {
                    // just dismisses the alert dialog.
                });

        final AlertDialog alertDialog = mBuilder.create();

        mJoinBtn.setOnClickListener(v -> {

            if ( mGroup.getGroupVisibility().toLowerCase().equals("private") ) {
                alertDialog.show();
            }
            else {
                mPresenter.addUserToGroup(mainActivity.user.getUserID(), mGroup, null);
            }
        });

    }


    public void initViewComponents(View view) {

        mBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        mAlertDialogView = inflater.inflate(R.layout.dialog_private_group, null);
        mInviteCodeET = mAlertDialogView.findViewById(R.id.inviteCodeET);

        mProgressBar = view.findViewById(R.id.progress_circular);
        mJoinBtn = view.findViewById(R.id.joinBtn);
        mGroupNameTV = view.findViewById(R.id.groupNameTV);

    }


    @Override
    public void showProgressBar() {
        mProgressBar = mView.findViewById(R.id.progress_circular);
        mProgressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void hideProgressBar() {
       //TODO: fix progress bar not disappearing
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void showServerErrorResponse(String message) {
        GradsHubApplication.showToast(message);
    }


    @Override
    public void navigateToUserGroups() {
        NavController navController = Navigation.findNavController(mActivity, R.id.main_nav_host_fragment);
        navController.navigate(R.id.action_exploredGroupProfileFragment_to_myGroupsListFragment);
    }


    @Override
    public void showJoinGroupResponseMsg(String message) {
        GradsHubApplication.showToast(message);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
