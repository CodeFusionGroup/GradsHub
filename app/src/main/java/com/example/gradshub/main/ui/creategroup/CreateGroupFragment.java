package com.example.gradshub.main.ui.creategroup;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.gradshub.R;
import com.example.gradshub.main.ui.MainActivity;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.network.AsyncHTTpPost;

import org.json.JSONException;
import org.json.JSONObject;


public class CreateGroupFragment extends Fragment implements View.OnClickListener {

    private RadioButton publicRadioBtn, privateRadioBtn;
    private EditText groupNameET;
    private String groupName, groupVisibility, groupInviteCode=null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        groupNameET = view.findViewById(R.id.groupNameET);
        publicRadioBtn = view.findViewById(R.id.publicRB);
        privateRadioBtn = view.findViewById(R.id.privateRB);
        Button doneBtn = view.findViewById(R.id.doneBtn);

        publicRadioBtn.setOnClickListener(this);
        privateRadioBtn.setOnClickListener(this);
        doneBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publicRB:
                groupVisibility = publicRadioBtn.getText().toString();
                break;

            case R.id.privateRB:
                groupVisibility = privateRadioBtn.getText().toString();
                break;

            case R.id.doneBtn:
                MainActivity mainActivity = (MainActivity) requireActivity();
                String groupAdmin = mainActivity.user.getEmail();
                groupName = groupNameET.getText().toString().trim();
                if (isValidInput()) {
                    if(privateRadioBtn.isChecked()) {
                        groupInviteCode = "1";
                    }
                    createResearchGroup(new ResearchGroup(groupAdmin, groupName, groupVisibility, groupInviteCode));
                }
                break;
        }
    }


    private boolean isValidInput() {
        if (groupName.isEmpty()) {
            groupNameET.setError("Not a valid group name!");
            groupNameET.requestFocus();
            return false;
        }
        // don't allow for too long group names.
        int maxCharLength = 50;
        if (groupName.length() > maxCharLength) {
            groupNameET.setError("Exceeded the maximum number of characters allowed!");
            groupNameET.requestFocus();
            return false;
        }
        // check if the group visibility is selected.
        if (!publicRadioBtn.isChecked() && !privateRadioBtn.isChecked()) {
            Toast.makeText(requireContext(), "please indicate the group visibility!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void createResearchGroup(ResearchGroup researchGroup) {
        ContentValues params = new ContentValues();
        params.put("USER_EMAIL", researchGroup.getGroupAdmin());
        params.put("GROUP_NAME", researchGroup.getGroupName());
        params.put("GROUP_VISIBILITY", researchGroup.getGroupVisibility());
        params.put("GROUP_CODE", researchGroup.getGroupInviteCode());

        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/creategroup.php", params) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onPostExecute(String output) {
                serverCreateResearchGroupResponse(output);
            }

        };
        asyncHttpPost.execute();
    }


    private void serverCreateResearchGroupResponse(String output) {
        try {
            JSONObject jo = new JSONObject(output);
            String success = jo.getString("success");
            String message =jo.getString("message");

            switch (success) {
                case "1": // Toast message: New group created.
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    // switch to my groups fragment to see the updated list of your groups.
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.action_createGroupFragment_to_myGroupsFragment);
                    break;

                case "0": // Toast message: didn't send the required values (not necessarily communicated back to user for this case)
                case "-1": // Toast message: group name taken, please choose another one.
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
