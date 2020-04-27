package com.example.gradshub.main.availablegroups;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gradshub.R;
import com.example.gradshub.main.MainActivity;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.model.User;
import com.example.gradshub.network.AsyncHTTpPost;

import org.json.JSONException;
import org.json.JSONObject;


public class AvailableGroupProfileFragment extends Fragment {

    private ResearchGroup researchGroup;
    private MainActivity mainActivity;
    private ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) requireActivity();
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            researchGroup = bundle.getParcelable("group_item");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_available_group_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        progressBar = view.findViewById(R.id.progress_circular);
        Button joinBtn = view.findViewById(R.id.joinBtn);
        TextView groupNameTV = view.findViewById(R.id.groupNameTV);
        groupNameTV.setText(researchGroup.getGroupName());


        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View alertDialogView = inflater.inflate(R.layout.dialog_private_group, null);
        EditText inviteCodeET = alertDialogView.findViewById(R.id.inviteCodeET);

        builder.setView(alertDialogView)
                .setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // entering nothing is the same as entering an invalid invite code.
                        String inviteCode = inviteCodeET.getText().toString().trim();
                        requestToJoinGroup(mainActivity.user, researchGroup, inviteCode);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // just dismisses the alert dialog.
                    }
                });

        final AlertDialog alertDialog = builder.create();

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(researchGroup.getGroupVisibility().toLowerCase().equals("private")) {
                    alertDialog.show();
                }
                else {
                    requestToJoinGroup(mainActivity.user, researchGroup, null);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    private void requestToJoinGroup(User user, ResearchGroup researchGroup, String inviteCode) {

        ContentValues params = new ContentValues();
        params.put("USER_ID",user.getUserID());
        params.put("GROUP_ID", researchGroup.getGroupID());
        params.put("GROUP_VISIBILITY", researchGroup.getGroupVisibility());
        params.put("GROUP_CODE", inviteCode);

        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/joingroup.php",params) {
            @Override
            protected void onPostExecute(String output) {
                serverRequestToJoinGroupResponse(output);
            }

        };
        asyncHttpPost.execute();
    }


    private void serverRequestToJoinGroupResponse(String output) {

        try {

            if(output.equals("")) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
            }
            else {

                JSONObject jo = new JSONObject(output);
                String success = jo.getString("success");

                // successfully joined group
                if(success.equals("1")) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), jo.getString("message"), Toast.LENGTH_SHORT).show();

                    // make another call to getGroupsToExplore() to reflect the updated list of available groups for the current user
                    AvailableGroupsListFragment availableGroupsFragment = AvailableGroupsListFragment.getInstance();
                    availableGroupsFragment.getGroupsToExplore(mainActivity.user);

                    NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
                    navController.navigate(R.id.action_availableGroupProfileFragment_to_myGroupsListFragment);
                }
                // verification of invite code failed if user requested to join private group.
                else if(success.equals("0")) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), jo.getString("message"), Toast.LENGTH_SHORT).show();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
