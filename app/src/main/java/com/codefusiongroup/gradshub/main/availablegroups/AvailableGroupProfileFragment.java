package com.codefusiongroup.gradshub.main.availablegroups;

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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codefusiongroup.gradshub.R;
import com.codefusiongroup.gradshub.main.MainActivity;
import com.codefusiongroup.gradshub.model.ResearchGroup;
import com.codefusiongroup.gradshub.model.User;
import com.codefusiongroup.gradshub.network.NetworkRequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class AvailableGroupProfileFragment extends Fragment {

    private ProgressBar progressBar;

    private ResearchGroup researchGroup;
    private MainActivity mainActivity;


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

        String url = "https://gradshub.herokuapp.com/api/User/joingroup.php";
        HashMap<String, String> params = new HashMap<>();

        params.put("user_id",user.getUserID());
        params.put("group_id", researchGroup.getGroupID());
        params.put("group_visibility", researchGroup.getGroupVisibility());
        params.put("group_code", inviteCode);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        serverRequestToJoinGroupResponse(response);
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
        NetworkRequestQueue.getInstance( requireActivity().getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }


    private void serverRequestToJoinGroupResponse(JSONObject response) {

        try {

            String statusCode = response.getString("success");
            String message = response.getString("message");

            switch (statusCode) {

                // successfully joined group
                case "1":
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
                    navController.navigate(R.id.action_availableGroupProfileFragment_to_myGroupsListFragment);
                    break;

                // incorrect invite code (for private groups)
                case "0":
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
