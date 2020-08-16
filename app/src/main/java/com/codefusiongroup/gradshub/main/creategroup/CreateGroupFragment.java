package com.codefusiongroup.gradshub.main.creategroup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.main.MainActivity;
import com.codefusiongroup.gradshub.model.ResearchGroup;
import com.codefusiongroup.gradshub.network.NetworkRequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class CreateGroupFragment extends Fragment implements View.OnClickListener {

    private ProgressBar progressBar;
    private RadioButton publicRadioBtn, privateRadioBtn;
    private EditText groupNameET;

    private String groupName, groupVisibility, groupInviteCode = null;


    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_group, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {

        progressBar = view.findViewById(R.id.progress_circular);
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

        switch ( v.getId() ) {

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

                if ( isValidInput() ) {
                    if( privateRadioBtn.isChecked() ) {
                        groupInviteCode = GenerateInviteCode.generateString();
                    }
                    createResearchGroup( new ResearchGroup( groupAdmin, groupName, groupVisibility, groupInviteCode ) );
                    progressBar.setVisibility(View.VISIBLE);
                }

                break;
        }

    }


    private boolean isValidInput() {

        if ( groupName.isEmpty() ) {
            groupNameET.setError("Not a valid group name!");
            groupNameET.requestFocus();
            return false;
        }

        // don't allow group names that are too long.
        int maxCharLength = 50;
        if ( groupName.length() > maxCharLength ) {
            groupNameET.setError("Exceeded the maximum number of characters allowed!");
            groupNameET.requestFocus();
            return false;
        }

        // check if the group visibility is selected.
        if ( !publicRadioBtn.isChecked() && !privateRadioBtn.isChecked() ) {
            Toast.makeText(requireActivity(), "please indicate the group visibility!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void createResearchGroup(ResearchGroup researchGroup) {

        String url = "https://gradshub.herokuapp.com/api/Group/create.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("email", researchGroup.getGroupAdmin());
        params.put("name", researchGroup.getGroupName());
        params.put("visibility", researchGroup.getGroupVisibility());
        params.put("code", researchGroup.getGroupInviteCode());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverCreateResearchGroupResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // means something went wrong when contacting server. Just display message indicating
                        // to user to try again
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext() ).addToRequestQueue(jsonObjectRequest);

    }


    private void serverCreateResearchGroupResponse(JSONObject response) {

        try {

            String statusCode = response.getString("success");
            String message = response.getString("message");

            switch (statusCode) {

                // New group created.
                case "1":
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    // switch to my groups fragment to see the updated list of your groups.
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
                    navController.navigate(R.id.action_createGroupFragment_to_myGroupsListFragment);
                    break;

                case "0": // Toast message: didn't send the required values (not necessarily communicated back to user for this case)
                case "-1": // Toast message: group name taken, please choose another one.
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
