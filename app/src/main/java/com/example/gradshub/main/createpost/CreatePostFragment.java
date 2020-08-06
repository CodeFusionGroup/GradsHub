package com.example.gradshub.main.createpost;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.gradshub.R;
import com.example.gradshub.main.MainActivity;
import com.example.gradshub.main.mygroups.MyGroupsProfileFragment;
import com.example.gradshub.model.Post;
import com.example.gradshub.model.ResearchGroup;
import com.example.gradshub.model.User;
import com.example.gradshub.network.AsyncHTTpPost;
import com.example.gradshub.network.NetworkRequestQueue;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class CreatePostFragment extends Fragment {


    private EditText postSubjectET;
    private TextInputEditText postDescriptionET;
    private String postSubject, postDescription;
    private ProgressBar progressBar;

    private TextView fileAttachmentTV;
    private static final int PICK_FILE_RESULT_CODE = 1;

    private View view;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        context = this.getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_post, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {

        progressBar = view.findViewById(R.id.progress_circular);
        fileAttachmentTV = view.findViewById(R.id.fileAttachmentTV);
        postSubjectET = view.findViewById(R.id.postSubjectET);
        postDescriptionET = view.findViewById(R.id.postDescriptionET);

        Button postBtn = view.findViewById(R.id.postBtn);
        postBtn.setOnClickListener(v -> {

            postSubject = postSubjectET.getText().toString().trim();
            postDescription = postDescriptionET.getText().toString().trim();

            if (isValidInput()) {

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String postDate = df.format(date);

                MainActivity mainActivity = (MainActivity) requireActivity();
                ResearchGroup researchGroup = MyGroupsProfileFragment.getGroup();

                createGroupPost( new Post(postDate, postSubject, postDescription), mainActivity.user, researchGroup );
                progressBar.setVisibility(View.VISIBLE);
            }

        });

    }


    private boolean isValidInput() {

        if (postSubject.isEmpty()) {
            postSubjectET.setError("Not a valid post subject!");
            postSubjectET.requestFocus();
            return false;
        }

        int maxCharLength = 30;
        if (postSubject.length() > maxCharLength) {
            postSubjectET.setError("Exceeded the maximum number of characters allowed!");
            postSubjectET.requestFocus();
            return false;
        }

        if (postDescription.isEmpty()) {
            postDescriptionET.setError("Not a valid post description!");
            postDescriptionET.requestFocus();
            return false;
        }

        return true;
    }


//    private void createGroupPost(Post post, User user, ResearchGroup researchGroup) {
//
//        ContentValues params = new ContentValues();
//        params.put("GROUP_ID", researchGroup.getGroupID());
//        params.put("USER_ID", user.getUserID());
//        params.put("POST_DATE", post.getPostDate());
//        params.put("POST_TITLE", post.getPostSubject());
//        params.put("POST_URL", post.getPostDescription());
//
//        AsyncHTTpPost asyncHttpPost = new AsyncHTTpPost("https://gradshub.herokuapp.com/creategrouppost.php", params) {
//            @SuppressLint("StaticFieldLeak")
//            @Override
//            protected void onPostExecute(String output) {
//                serverCreateGroupPostResponse(output);
//            }
//
//        };
//        asyncHttpPost.execute();
//
//    }

    private void createGroupPost(Post post, User user, ResearchGroup researchGroup) {

        String url = "https://gradshub.herokuapp.com/api/GroupPost/create.php";
        HashMap<String, String> params = new HashMap<String,String>();
        params.put("group_id", researchGroup.getGroupID());
        params.put("user_id", user.getUserID());
        params.put("post_date", post.getPostDate());
        params.put("post_title", post.getPostSubject());
        params.put("post_url", post.getPostDescription());

        JsonObjectRequest postRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.err.println(response);
                        serverCreateGroupPostResponse(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        error.printStackTrace();
                    }
                });
        // Access the Global(App) RequestQueue
        NetworkRequestQueue.getInstance( context.getApplicationContext() ).addToRequestQueue(postRequest);

    }


    private void serverCreateGroupPostResponse(String output) {

        try {

            if(output.equals("")) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireActivity(), "Connection failed, please try again later.", Toast.LENGTH_SHORT).show();
            }

            else {

                JSONObject jo = new JSONObject(output);
                String success = jo.getString("success");
                String message =jo.getString("message");

                if (success.equals("1")) {
                    progressBar.setVisibility(View.GONE);
                    // Toast message: successfully created a post
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();

                    NavController navController = Navigation.findNavController(requireActivity(), R.id.main_nav_host_fragment);
                    navController.navigate(R.id.action_createPostFragment_to_myGroupProfileFragment);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.create_post_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_attach_file:
                openFile();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    // NOTE: following functions still incomplete

    private void openFile() {

        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("application/pdf");
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile = Intent.createChooser(chooseFile, "Select PDF");
        startActivityForResult(chooseFile, PICK_FILE_RESULT_CODE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PICK_FILE_RESULT_CODE && resultCode == RESULT_OK && data != null) {

            if(data.getData() != null) {
                Uri fileUri = data.getData();
                //pdfView.fromUri(fileUri).defaultPage(0).spacing(10).load();
                //String filePath;
                //File file = new File(fileUri.get)
                fileAttachmentTV.setText(fileUri.getLastPathSegment());
                fileAttachmentTV.setVisibility(View.VISIBLE);
            }
        }

    }

}
